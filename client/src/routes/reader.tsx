import { Note, NoteAdd } from "@mui/icons-material";
import {
  Badge,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  Drawer,
  Fab,
  Input,
  Modal,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import { createFileRoute } from "@tanstack/react-router";
import { EpubCFI, Rendition } from "epubjs";
import { nanoid } from "nanoid";
import { useCallback, useEffect, useRef, useState } from "react";
import { ReactReader } from "react-reader";

export const Route = createFileRoute("/reader")({
  component: RouteComponent,
});

type Memo = {
  id: string;
  cfiRange: string;
  content: string;
};
type MemoDiff = {
  added: Memo[];
  removed: Memo[];
};
type Selection = {
  left: number;
  top: number;
  text: string;
  epubcfi: string;
};

function RouteComponent() {
  const theme = useTheme();
  const [location, setLocation] = useState<string | number>(10);
  const [memos, setMemos] = useState<Memo[]>([]);
  const [memosInPage, setMemosInPage] = useState<Memo[]>([]);
  const [epubUrl, setEpubUrl] = useState<ArrayBuffer>(new ArrayBuffer());
  const [rendition, setRendition] = useState<Rendition | undefined>(undefined);
  const [openMemoDrawer, setOpenMemoDrawer] = useState(false);
  const [selection, setSelection] = useState<Selection | null>(null);
  const [openMemoCreationModal, setOpenMemoCreationModal] =
    useState<boolean>(false);
  const previousMemosInPage = useRef<Memo[]>([]);

  const uploadEpub = useCallback(() => {
    let inputElement = document.createElement("input");
    inputElement.type = "file";
    inputElement.onchange = async (event) => {
      const target = event.target as HTMLInputElement;
      const file = target.files?.[0];
      if (!file) {
        return;
      }

      setEpubUrl(await file.arrayBuffer());
    };

    inputElement.click();
  }, [setEpubUrl]);

  useEffect(() => {
    previousMemosInPage.current = memosInPage;
    if (!rendition || !rendition.location) {
      setMemosInPage([]);
      return;
    }

    const cfi = new EpubCFI();
    const startCfi = rendition.location.start.cfi;
    const endCfi = rendition.location.end.cfi;
    const newMemosInPage = memos.filter((memo) => {
      if (cfi.compare(startCfi, memo.cfiRange) >= 0) {
        return false;
      }
      if (cfi.compare(memo.cfiRange, endCfi) >= 0) {
        return false;
      }
      return true;
    });

    setMemosInPage(newMemosInPage);
    return;
  }, [memos, rendition, location, setMemosInPage]);

  useEffect(() => {
    if (!rendition) {
      return;
    }

    const { added, removed } = diffMemos(
      previousMemosInPage.current,
      memosInPage
    );
    added.forEach((memo) => {
      rendition.annotations.highlight(memo.cfiRange, {}, () => {});
    });
    removed.forEach((memo) => {
      rendition.annotations.remove(memo.cfiRange, "highlight");
    });
  }, [rendition, memosInPage]);

  return (
    <Box
      sx={{
        maxWidth: "none",
        width: "100vw",
        height: "100vh",
        position: "relative",
      }}
    >
      <Box
        sx={{
          width: "100vw",
          position: "absolute",
          zIndex: theme.zIndex.fab,
        }}
      >
        <Button onClick={uploadEpub}>Upload EPUB</Button>
        <Fab
          size="small"
          sx={{
            position: "absolute",
            right: theme.spacing(2),
            top: theme.spacing(2),
          }}
          onClick={() => {
            setOpenMemoDrawer(true);
          }}
        >
          <Badge badgeContent={memosInPage.length} color="primary">
            <Note />
          </Badge>
        </Fab>
        {selection && (
          <Fab
            size="small"
            sx={{
              position: "absolute",
              left: selection.left,
              top: selection.top,
              translate: "50% 50%",
            }}
            onClick={() => {
              setOpenMemoCreationModal(true);
            }}
          >
            <NoteAdd />
          </Fab>
        )}
      </Box>
      <MemoCreationModal
        open={openMemoCreationModal}
        onClose={() => setOpenMemoCreationModal(false)}
        selection={selection}
        addMemo={(memo) => {
          setMemos((prev) => [...prev, memo]);
        }}
      />
      <Drawer
        anchor="right"
        open={openMemoDrawer}
        onClose={() => setOpenMemoDrawer(false)}
      >
        <Stack spacing={theme.spacing(2)} p={theme.spacing(2)} width={256}>
          {memosInPage.map((memo) => (
            <MemoCard key={memo.id} memo={memo} />
          ))}
        </Stack>
      </Drawer>
      <ReactReader
        url={epubUrl}
        epubOptions={{
          spread: "none",
        }}
        location={location}
        locationChanged={(epubcfi: string) => {
          setLocation(epubcfi);
        }}
        handleTextSelected={(cfiRange, contents) => {
          const window = contents.window;
          const selection = window.getSelection();
          if (!selection) {
            setSelection(null);
            return;
          }

          contents.document.addEventListener(
            "selectionchange",
            () => {
              const selection = window.getSelection();
              if (!selection || selection.isCollapsed) {
                setSelection(null);
              }
            },
            { once: true }
          );

          const boundingClientRect = selection
            .getRangeAt(0)
            .getBoundingClientRect();
          setSelection({
            left: boundingClientRect.left,
            top: boundingClientRect.top,
            text: selection.toString(),
            epubcfi: cfiRange,
          });
        }}
        showToc={false}
        getRendition={(newRendition) => {
          setRendition(newRendition);
        }}
      />
    </Box>
  );
}

function diffMemos(prev: Memo[], next: Memo[]): MemoDiff {
  const added = next.filter(
    (memo) => !prev.some((prevMemo) => prevMemo.id === memo.id)
  );
  const removed = prev.filter(
    (memo) => !next.some((nextMemo) => nextMemo.id === memo.id)
  );

  return {
    added,
    removed,
  };
}

function MemoCard({ memo }: { memo: Memo }) {
  return (
    <Card>
      <CardHeader title="Nickname" avatar="N" />
      <CardContent>
        <Typography variant="body1">{memo.content}</Typography>
      </CardContent>
    </Card>
  );
}

function MemoCreationModal({
  open,
  onClose,
  selection,
  addMemo,
}: {
  open: boolean;
  onClose: () => void;
  selection: Selection | null;
  addMemo: (memo: Memo) => void;
}) {
  const [content, setContent] = useState("");

  if (!selection) {
    return null;
  }

  return (
    <Modal open={open} onClose={onClose}>
      <Box
        sx={{
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          width: 400,
        }}
      >
        <Card>
          <CardHeader title="Create Memo" />
          <CardContent>
            <Typography color="textSecondary" variant="body1">
              {selection.text}
            </Typography>
            <Divider />
            <Input
              value={content}
              onChange={(e) => setContent(e.target.value)}
              type="textarea"
              multiline
              fullWidth
            />
          </CardContent>
          <CardActions>
            <Button color="secondary" onClick={onClose}>
              Cancel
            </Button>
            <Button
              variant="contained"
              color="primary"
              onClick={() => {
                if (!selection) {
                  return;
                }
                const newMemo: Memo = {
                  id: nanoid(),
                  cfiRange: selection.epubcfi,
                  content,
                };
                addMemo(newMemo);
                setContent("");
                onClose();
              }}
            >
              Create
            </Button>
          </CardActions>
        </Card>
      </Box>
    </Modal>
  );
}
