import {
  AppBar,
  Box,
  Button,
  Card,
  CardActions,
  CardHeader,
  CssBaseline,
  Stack,
  styled,
  ThemeProvider,
  Toolbar,
  Typography,
} from "@mui/material";
import createThemeWithUserColorScheme from "./util/createThemeWithUserColorScheme";
import { ReactReader } from "react-reader";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Rendition } from "epubjs";
import { nanoid } from "nanoid";

const theme = createThemeWithUserColorScheme();

type Memo = {
  id: string;
  cfiRange: string;
};

export default function App() {
  const [epubUrl, setEpubUrl] = useState<ArrayBuffer>(new ArrayBuffer());
  const [location, setLocation] = useState<string | number>(10);
  const [rendition, setRendition] = useState<Rendition | undefined>(undefined);
  const [memos, setMemos] = useState<Memo[]>([]);

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
    if (!rendition) {
      return;
    }

    for (const memo of memos) {
      rendition.annotations.remove(memo.cfiRange, "highlight");
      rendition.annotations.highlight(memo.cfiRange);
    }
  }, [memos, rendition]);

  const memoCards = useMemo(() => {
    return memos.map((memo) => {
      return (
        <Card key={memo.id}>
          <CardHeader title={memo.cfiRange} />
          <CardActions>
            <Button
              onClick={() => {
                rendition?.annotations.remove(memo.cfiRange, "highlight");
                setMemos((prev) =>
                  prev.filter((memo_) => memo_.id !== memo.id)
                );
              }}
            >
              delete
            </Button>
          </CardActions>
        </Card>
      );
    });
  }, [memos, rendition]);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static">
        <Toolbar>
          <Typography
            variant="h6"
            sx={{
              flexGrow: 1,
            }}
          >
            QWERTY
          </Typography>
          <Button onClick={uploadEpub}>Upload EPUB</Button>
        </Toolbar>
      </AppBar>
      <RootContainer>
        <Box
          sx={{
            width: 1,
          }}
        >
          <ReactReader
            url={epubUrl}
            epubOptions={{
              spread: "none",
            }}
            location={location}
            locationChanged={(epubcfi: string) => {
              setLocation(epubcfi);
              console.log(epubcfi);
            }}
            handleTextSelected={(cfiRange, _contents) => {
              setMemos((prev) => [...prev, { id: nanoid(), cfiRange }]);
            }}
            showToc={false}
            getRendition={(rendition) => setRendition(rendition)}
          />
        </Box>
        <Box
          sx={{
            width: "sm",
            maxWidth: "sm",
          }}
        >
          <Stack
            sx={{
              width: 1,
              padding: "sm",
            }}
            spacing={1}
          >
            {memoCards}
          </Stack>
        </Box>
      </RootContainer>
    </ThemeProvider>
  );
}

const RootContainer = styled("div")({
  display: "flex",
  flexDirection: "row",
  height: "100vh",
});
