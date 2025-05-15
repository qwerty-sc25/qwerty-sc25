import { Button, Container, Stack } from "@mui/material";
import {
  createFileRoute,
  createLink,
  useNavigate,
} from "@tanstack/react-router";
import GroupList from "../../component/groupList";

export const Route = createFileRoute("/_pathlessLayout/")({
  component: Home,
});

function Home() {
  const navigate = useNavigate();

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Stack spacing={2}>
        <GroupList
          size="small"
          action={
            <Button onClick={() => navigate({ to: "/groups" })}>더보기</Button>
          }
          title="인기 그룹"
          key="popularGroups"
        />
        <LinkButton variant="contained" color="primary" to="/about">
          Go to About Page
        </LinkButton>
        <LinkButton variant="contained" color="primary" to="/posts">
          Go to Post List Page
        </LinkButton>
      </Stack>
    </Container>
  );
}

const LinkButton = createLink(Button);
