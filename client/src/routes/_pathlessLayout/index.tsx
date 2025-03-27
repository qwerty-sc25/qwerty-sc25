import { Container, Typography, Button } from "@mui/material";
import { createFileRoute, Link } from "@tanstack/react-router";

export const Route = createFileRoute("/_pathlessLayout/")({
  component: Home,
});

function Home() {
  return (
    <Container maxWidth="lg">
      <Typography variant="h2" align="center">
        qwerty
      </Typography>
      <Button variant="contained" color="primary" component={Link} to="/about">
        Go to About Page
      </Button>
      <br></br>
      <br></br>
      <Button
        variant="contained"
        color="primary"
        component={Link}
        to="/pages/postlist"
      >
        Go to Post List Page
      </Button>
    </Container>
  );
}
