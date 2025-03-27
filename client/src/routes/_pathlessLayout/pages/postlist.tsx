import { useState } from "react";
import { Card, CardContent, Typography, Button } from "@mui/material";
import DeleteButton from "../../../component/deleteButton"; // 삭제 버튼 컴포넌트 import
import { createFileRoute, Link } from "@tanstack/react-router";

export const Route = createFileRoute("/_pathlessLayout/pages/postlist")({
  component: PostList,
});

type Post = {
  id: number;
  title: string;
  content: string;
};

function PostList() {
  const [posts, setPosts] = useState<Post[]>([
    { id: 1, title: "첫 번째 글", content: "이것은 첫 번째 게시글입니다." },
    { id: 2, title: "두 번째 글", content: "이것은 두 번째 게시글입니다." },
    { id: 3, title: "세 번째 글", content: "이것은 세 번째 게시글입니다." },
  ]);

  // 게시글 삭제 함수
  const handleDelete = (id: number) => {
    setPosts((prev) => prev.filter((post) => post.id !== id));
  };

  return (
    <div>
      <Typography variant="h4" gutterBottom>
        게시판
      </Typography>
      <Button
        variant="contained"
        onClick={() =>
          setPosts((prev) => [
            ...prev,
            {
              id: prev.length + 1,
              title: `새 글 ${prev.length + 1}`,
              content: "새로 추가된 게시글입니다.",
            },
          ])
        }
      >
        새 게시글 추가
      </Button>
      {posts.map((post) => (
        <Card key={post.id} sx={{ marginBottom: 2 }}>
          <CardContent>
            <Typography variant="h5">{post.title}</Typography>
            <Typography variant="body2" color="text.secondary">
              {post.content}
            </Typography>
            {/* 삭제 버튼 추가 */}
            <DeleteButton onClick={() => handleDelete(post.id)} />
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
