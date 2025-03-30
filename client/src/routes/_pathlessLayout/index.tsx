import { Container, Typography } from "@mui/material";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_pathlessLayout/")({
  component: Home,
});

function Home() {
  return (
    <Container maxWidth="lg">
      <Typography variant="h2" align="center">
        qwerty
      </Typography>
    </Container>
  );
}
