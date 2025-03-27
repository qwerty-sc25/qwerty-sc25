import { Container, Typography } from "@mui/material";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_pathlessLayout/about")({
  component: About,
});

function About() {
  return (
    <Container maxWidth="lg">
      <Typography variant="h2" align="center">
        About Page
      </Typography>
    </Container>
  );
}
