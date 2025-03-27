import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_pathlessLayout/pages/postdetail")({
  component: PostDetail,
});

function PostDetail() {
  return <div>Hello "/_pathlessLayout/pages/postdetail"!</div>;
}
