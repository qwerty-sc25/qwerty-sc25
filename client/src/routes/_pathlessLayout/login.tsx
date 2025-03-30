import {
  Container,
  Card,
  CardContent,
  OutlinedInput,
  Divider,
  Button,
  useTheme,
  CardHeader,
} from "@mui/material";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import useLogin from "../../api/login/useLogin";
import { useCallback, useState } from "react";

export const Route = createFileRoute("/_pathlessLayout/login")({
  component: RouteComponent,
});

function RouteComponent() {
  const theme = useTheme();
  const navigate = useNavigate();
  const { login } = useLogin();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const onLoginButtonClick = useCallback(() => {
    // TODO: handle error

    login({
      username,
      password,
    });
  }, [login, username, password]);
  return (
    <Container maxWidth="sm" sx={{ mt: theme.spacing(4) }}>
      <Card>
        <CardHeader title="로그인" />
        <CardContent
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: theme.spacing(2),
          }}
        >
          <OutlinedInput
            placeholder="ID"
            fullWidth
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <OutlinedInput
            placeholder="Password"
            fullWidth
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Divider />
          <Button fullWidth variant="contained" onClick={onLoginButtonClick}>
            로그인
          </Button>
          <Button
            fullWidth
            variant="text"
            onClick={() => {
              navigate({
                to: "/register",
              });
            }}
          >
            회원가입
          </Button>
        </CardContent>
      </Card>
    </Container>
  );
}
