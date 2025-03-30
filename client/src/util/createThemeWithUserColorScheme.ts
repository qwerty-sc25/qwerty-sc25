import { createTheme } from "@mui/material";

export default function createThemeWithUserColorScheme() {
  const userColorScheme =
    window.matchMedia &&
    window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  const theme = createTheme({
    palette: {
      mode: userColorScheme,
    },
  });
  return theme;
}
