import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Divider,
  InputLabel,
  OutlinedInput,
  useTheme,
} from "@mui/material";
import { createFileRoute } from "@tanstack/react-router";
import useJoin from "../../api/join/useJoin";
import { useCallback, useState } from "react";

export const Route = createFileRoute("/_pathlessLayout/register")({
  component: RouteComponent,
});

function RouteComponent() {
  const theme = useTheme();
  const { join } = useJoin();
  const [nickname, setNickname] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const onRegisterButtonClick = useCallback(() => {
    // TODO: Test username and password constraints

    if (password !== confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    join({
      nickname,
      username,
      password,
    });
  }, [join, username, password, confirmPassword]);

  return (
    <Container maxWidth="sm" sx={{ mt: theme.spacing(4) }}>
      <Card>
        <CardHeader title="회원가입" />
        <CardContent
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: theme.spacing(2),
          }}
        >
          <InputLabel>Nickname</InputLabel>
          <OutlinedInput
            placeholder="Nickname"
            fullWidth
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
          />

          <Divider />

          <InputLabel>ID</InputLabel>
          <OutlinedInput
            placeholder="ID"
            fullWidth
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />

          <Divider />

          <InputLabel>Password</InputLabel>
          <OutlinedInput
            placeholder="Password"
            fullWidth
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <OutlinedInput
            placeholder="Confirm Password"
            fullWidth
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />

          <Divider />

          <Button fullWidth variant="contained" onClick={onRegisterButtonClick}>
            가입하기
          </Button>
        </CardContent>
      </Card>
    </Container>
  );
}
