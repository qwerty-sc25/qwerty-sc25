import { createFileRoute, useNavigate } from "@tanstack/react-router";
import {
  Typography,
  Container,
  Button,
  Paper,
  Stack,
  Box,
  Card,
  CardContent,
  Divider,
  Avatar,
  Chip,
  CircularProgress,
  CardActionArea,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import API_CLIENT from "../../../../../../../api/api";
import { Discussion } from "../../../../../../../types/discussion";
import MessageIcon from "@mui/icons-material/Message";
// import StarIcon from "@mui/icons-material/Star";

export const Route = createFileRoute(
  "/_pathlessLayout/groups/$groupId/activities/$activityId/discussions/"
)({
  component: RouteComponent,
  params: {
    parse: (params) => {
      const groupId = parseInt(params.groupId);
      if (isNaN(groupId)) {
        throw new Error("Invalid groupId");
      }
      const activityId = parseInt(params.activityId);
      if (isNaN(activityId)) {
        throw new Error("Invalid activityId");
      }
      return {
        groupId,
        activityId,
      };
    },
  },
});

function RouteComponent() {
  const navigate = useNavigate();
  const { groupId, activityId } = Route.useParams();

  const {
    data: discussions,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["discussions", activityId],
    queryFn: async () => {
      try {
        const response =
          await API_CLIENT.discussionController.getDiscussions(activityId);
        if (!response.isSuccessful) {
          throw new Error(response.errorMessage);
        }
        return response.data.content as Discussion[];
      } catch (error) {
        console.error("게시글 목록을 불러오는 중 오류가 발생했습니다:", error);
        throw error;
      }
    },
  });

  const handleNavigateToDiscussion = (discussionId: number) => {
    navigate({
      from: Route.to,
      to: "$discussionId",
      params: {
        discussionId: discussionId.toString(),
      },
    });
  };

  const handleNavigateToGroup = () => {
    navigate({
      from: Route.to,
      to: "/groups/$groupId",
      params: {
        groupId,
      },
    });
  };

  const handleCreateNewDiscussion = () => {
    navigate({
      from: Route.to,
      to: `new`,
    });
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      {/* 헤더 영역 */}
      <Box sx={{ mb: 4 }}>
        <Typography
          variant="h4"
          fontWeight="bold"
          textAlign="center"
          color="primary.main"
          sx={{ mb: 4 }}
        >
          게시판
        </Typography>

        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          sx={{ mb: 2 }}
        >
          <Button
            variant="outlined"
            color="primary"
            onClick={handleNavigateToGroup}
            size="medium"
          >
            이전 페이지
          </Button>

          <Button
            variant="contained"
            color="primary"
            onClick={handleCreateNewDiscussion}
            size="medium"
          >
            새 게시글 작성
          </Button>
        </Stack>
      </Box>

      <Divider sx={{ mb: 4 }} />

      {/* 게시글 목록 영역 */}
      {isLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Paper
          elevation={0}
          sx={{
            p: 4,
            textAlign: "center",
            bgcolor: "error.light",
            borderRadius: 2,
          }}
        >
          <Typography color="error.main">
            게시글을 불러오는 중 오류가 발생했습니다. 다시 시도해주세요.
          </Typography>
        </Paper>
      ) : (
        <Stack spacing={2}>
          {!discussions || discussions.length === 0 ? (
            <Paper
              elevation={0}
              sx={{
                p: (a) => a.spacing(6),
                textAlign: "center",
                bgcolor: "grey.50",
                borderRadius: 2,
              }}
            >
              <Typography color="text.secondary" fontSize="1.1rem">
                등록된 게시글이 없습니다.
              </Typography>
              <Button
                variant="outlined"
                sx={{ mt: 2 }}
                onClick={handleCreateNewDiscussion}
              >
                첫 게시글 작성하기
              </Button>
            </Paper>
          ) : (
            discussions.map((discussion) => (
              <DiscussionCard
                key={discussion.discussionId}
                discussion={discussion}
                onClick={handleNavigateToDiscussion}
              />
            ))
          )}
        </Stack>
      )}
    </Container>
  );
}

interface DiscussionCardProps {
  discussion: Discussion;
  onClick: (id: number) => void;
}

function DiscussionCard({ discussion, onClick }: DiscussionCardProps) {
  const formattedDate = new Date(
    discussion.modifiedAt || discussion.createdAt
  ).toLocaleString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  });

  return (
    <Paper
      elevation={2}
      sx={{
        borderRadius: 2,
        overflow: "hidden",
        transition: "transform 0.2s, box-shadow 0.2s",
        "&:hover": {
          transform: "translateY(-4px)",
          boxShadow: 6,
        },
      }}
    >
      <CardActionArea onClick={() => onClick(discussion.discussionId)}>
        <Card sx={{ height: "100%" }}>
          <CardContent sx={{ p: 3 }}>
            {/* 상단 영역: 제목 및 뱃지 */}
            <Stack
              direction="row"
              alignItems="flex-start"
              justifyContent="space-between"
              sx={{ mb: 1.5 }}
            >
              <Stack direction="row" spacing={1} alignItems="center">
                <Typography
                  variant="h6"
                  fontWeight="bold"
                  sx={{ color: "text.primary" }}
                >
                  {discussion.title}
                </Typography>
              </Stack>
              <Stack spacing={1} alignItems="center">
                <Chip
                  icon={<MessageIcon fontSize="small" />}
                  label={discussion.commentCount}
                  size="small"
                  color="default"
                  variant="outlined"
                  sx={{ height: 24 }}
                />
                {discussion.isDebate && (
                  <Chip
                    label="토론"
                    color="success"
                    size="small"
                    variant="outlined"
                  />
                )}
              </Stack>
            </Stack>

            {/* 메타데이터 Chips */}

            {/* 본문 미리보기 */}
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{
                mb: 2,
                overflow: "hidden",
                textOverflow: "ellipsis",
                display: "-webkit-box",
                WebkitLineClamp: 2,
                WebkitBoxOrient: "vertical",
                minHeight: "2.5rem",
                lineHeight: 1.5,
              }}
            >
              {discussion.content.replace(/#\w[\w-]*/g, "메모").trim() ||
                "내용 없음"}
            </Typography>

            {/* 하단 영역: 작성자 정보 및 메타데이터 */}
            <Divider sx={{ mb: 2 }} />

            <Stack
              direction="row"
              alignItems="center"
              justifyContent="space-between"
              color="text.secondary"
            >
              <Stack direction="row" spacing={1} alignItems="center">
                <Avatar
                  src={discussion.authorProfileImage}
                  alt={discussion.authorName}
                  sx={{ width: 24, height: 24 }}
                />
                <Typography variant="body2">{discussion.authorName}</Typography>
                {/* { 모임지기 별달아주기
                  <Chip
                    icon={<StarIcon fontSize="small" />}
                    label="모임지기"
                    size="small"
                    color="warning"
                    variant="outlined"
                    sx={{ height: 24 }}
                  />
                } */}
              </Stack>

              <Stack direction="row" spacing={0.5} alignItems="center">
                <Typography variant="caption">{formattedDate}</Typography>
              </Stack>
            </Stack>
          </CardContent>
        </Card>
      </CardActionArea>
    </Paper>
  );
}
