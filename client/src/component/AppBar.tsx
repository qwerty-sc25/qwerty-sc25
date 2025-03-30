import {
  Toolbar,
  Typography,
  AppBar as MuiAppBar,
  Button,
} from "@mui/material";
import { Link } from "@tanstack/react-router";

export default function AppBar() {
  return (
    <MuiAppBar position="static">
      <Toolbar>
        <Typography variant="h6" sx={{ mr: "auto" }}>
          QWERTY
        </Typography>
        <Link to="/login">
          {({ isActive }) => {
            return (
              <Button variant={isActive ? "contained" : "text"}>로그인</Button>
            );
          }}
        </Link>
      </Toolbar>
    </MuiAppBar>
  );
}
