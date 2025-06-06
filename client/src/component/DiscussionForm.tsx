import API_CLIENT from "../api/api";
import { useState, useEffect } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useSnackbar } from "notistack";
import {
  Container,
  Card,
  CardContent,
  OutlinedInput,
  Button,
  useTheme,
  CardHeader,
  Checkbox,
  Stack,
  FormControlLabel,
  Box,
} from "@mui/material";
import { Discussion } from "../types/discussion";
import HighlightBrowserModal from "./HighlightBrowserModal";

export default function DiscussionForm({
  activityId,
  discussionId,
  handlePostRoute,
  handleBack,
}: {
  activityId: number;
  discussionId?: number;
  handlePostRoute: (discussionId?: number) => void;
  handleBack: () => void;
}) {
  const { enqueueSnackbar } = useSnackbar();
  const theme = useTheme();
  const isEdit = !!discussionId;
  const queryClient = useQueryClient();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [isDebate, setDebate] = useState(false);

  const { data: discussion } = useQuery({
    queryKey: ["discussion", discussionId],
    enabled: isEdit,
    queryFn: async () => {
      const response = await API_CLIENT.discussionController.getDiscussion(
        discussionId!!
      );
      if (!response.isSuccessful) {
        throw new Error(response.errorMessage);
      }
      return response.data as Discussion;
    },
  });

  // useEffect를 사용하여 discussion 데이터가 로드되었을 때만 state 업데이트
  useEffect(() => {
    if (discussion) {
      setTitle(discussion.title ?? "");
      setContent(discussion.content ?? "");
      setDebate(discussion.isDebate ?? false);
    }
  }, [discussion]);

  const handlePostDiscussion = () => {
    if (!title.trim() || !content.trim()) {
      enqueueSnackbar("제목과 내용을 입력해주세요.", { variant: "warning" });
      return;
    }
    const parts = content.split(/(#\w[\w-]*)/g); // '#'로 시작하는 단어 추출
    const idSet = new Set<number>();
    parts.forEach((part) => {
      const match = part.match(/#(\w[\w-]*)/);
      if (match) {
        idSet.add(parseInt(match[1])); // '#' 제거
      }
    });
    const highlightIds = idSet.size > 0 ? Array.from(idSet) : undefined;
    if (isEdit) {
      // 수정 모드: 기존 게시글 수정
      API_CLIENT.discussionController
        .updateDiscussion(discussionId!!, {
          title,
          content,
          highlightIds,
        })
        .then((response) => {
          if (response.isSuccessful) {
            queryClient.invalidateQueries({
              queryKey: ["discussion", discussionId],
            });
            enqueueSnackbar("게시글이 수정되었습니다.", { variant: "success" });
            handlePostRoute();
          } else {
            enqueueSnackbar(response.errorMessage, { variant: "error" });
          }
        });
    } else {
      // 작성 모드: 새 게시글 추가
      API_CLIENT.discussionController
        .createDiscussion(activityId, {
          title,
          content,
          isDebate,
          highlightIds,
        })
        .then((response) => {
          if (response.isSuccessful) {
            queryClient.invalidateQueries({
              queryKey: ["discussion", discussionId],
            });
            enqueueSnackbar("게시글이 작성되었습니다.", { variant: "success" });
            handlePostRoute(response.data.discussionId);
          } else {
            enqueueSnackbar(response.errorMessage, { variant: "error" });
          }
        });
    }
  };

  const handleCheckbox = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDebate(event.target.checked);
    if (event.target.checked) {
      enqueueSnackbar("토론이 체크되었습니다.", { variant: "info" });
    }
  };

  const [openMemoBrowser, setOpenMemoBrowser] = useState(false);

  return (
    <Container
      maxWidth="md"
      sx={{ mt: theme.spacing(4), justifyContent: "space-between" }}
    >
      <Card variant="outlined" sx={{ padding: 3 }}>
        <CardHeader title={isEdit ? "게시글 수정" : "게시글 작성"} />
        <CardContent
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: theme.spacing(2),
          }}
        />
        {/*제목 title */}
        <OutlinedInput
          sx={{ mb: 2 }}
          placeholder="제목을 입력하세요"
          value={title}
          fullWidth
          multiline
          onChange={(e) => setTitle(e.target.value)}
        />
        <Button
          variant="outlined"
          onClick={() => setOpenMemoBrowser(true)}
          sx={{ mb: 2 }}
        >
          Open highlight browser
        </Button>
        <HighlightBrowserModal
          open={openMemoBrowser}
          activityId={activityId}
          onClose={() => {
            setOpenMemoBrowser(false);
          }}
          onSelectHighlight={() => {}}
          onUseHighlight={(highlight) => {
            setContent((prev) => prev + "#" + highlight.id);
          }}
        />
        {/* 본문 content */}
        <OutlinedInput
          sx={{ mb: 2, alignItems: "flex-start", minHeight: "300px" }}
          placeholder="내용을 입력하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          fullWidth
          multiline
        />
        <Stack direction="row" justifyContent="space-between" sx={{ m: 1 }}>
          <Box sx={{ justifySelf: "flex-start" }}>
            {isEdit ? null : (
              <FormControlLabel
                label="토론"
                control={
                  <Checkbox
                    name="isDebate"
                    checked={isDebate}
                    onChange={handleCheckbox}
                  />
                }
              />
            )}
          </Box>
          <Box sx={{ justifySelf: "flex-end" }}>
            <Button onClick={handlePostDiscussion}>
              {isEdit ? "수정" : "작성"}
            </Button>
            <Button onClick={handleBack}>취소</Button>
          </Box>
        </Stack>
      </Card>
    </Container>
  );
}
