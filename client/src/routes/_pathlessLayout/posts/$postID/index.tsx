import {
  createFileRoute,
  useParams,
  Link,
  useNavigate,
} from "@tanstack/react-router";
import {
  Typography,
  Container,
  Button,
  Paper,
  Divider,
  Stack,
} from "@mui/material";
import { initialPosts } from "../../../../types/post";
import { useState } from "react";
import CommentSection from "../../../../component/CommentSection";
import { Comment } from "../../../../types/comment";

export const Route = createFileRoute("/_pathlessLayout/posts/$postID/")({
  component: PostID,
});

function PostID() {
  const navigate = useNavigate();
  const { postID } = Route.useParams();
  const post = initialPosts.find((p) => p.id === Number(postID));

  if (!post) return <Typography>게시글을 찾을 수 없습니다.</Typography>;

  const [comments, setComments] = useState<Comment[]>([
    {
      id: 1,
      author: "사용자1",
      content: "좋은 글이네요!",
      createdDate: new Date(),
    },
  ]);
  const handleAddComment = (newComment: string) => {
    setComments([
      ...comments,
      {
        id: Date.now(), // 임시 id
        author: "익명", // 또는 로그인 사용자 이름
        content: newComment,
        createdDate: new Date(),
      },
    ]);
  };
  const handleDeleteComment = (id: number) => {
    alert("정말 삭제하시겠습니까?");
    setComments((prev) => prev.filter((comment) => comment.id !== id));
  };

  return (
    <Container maxWidth="md" sx={{ mt: 5 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        {/* 제목 */}
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          {post.title}
        </Typography>

        {/* 작성자 및 작성일 */}
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          sx={{ mb: 2 }}
        >
          <Typography variant="subtitle2" color="text.secondary">
            작성자: {post.author}
          </Typography>
          <Typography variant="subtitle2" color="text.secondary">
            작성일: {new Date(post.createdDate).toLocaleDateString()}
          </Typography>
        </Stack>

        <Divider sx={{ my: 2 }} />

        {/* 본문 */}
        <Typography
          variant="body1"
          sx={{ whiteSpace: "pre-line", minHeight: "200px" }}
        >
          {post.content}
        </Typography>

        <Divider sx={{ my: 3 }} />

        {/* 버튼 영역 */}
        <Stack direction="row" spacing={2} justifyContent="flex-end">
          <Button
            variant="contained"
            color="primary"
            onClick={() => navigate({ to: `/posts/${post.id}/edit` })}
          >
            수정
          </Button>
          <Button variant="outlined" onClick={() => navigate({ to: "/posts" })}>
            뒤로 가기
          </Button>
        </Stack>
      </Paper>
      <CommentSection
        comments={comments}
        onAddComment={handleAddComment}
        onDeleteComment={handleDeleteComment}
      />
    </Container>
  );
}
