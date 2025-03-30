import { CssBaseline, ThemeProvider } from "@mui/material";
import createThemeWithUserColorScheme from "./util/createThemeWithUserColorScheme";
import { routeTree } from "./routeTree.gen";
import { createRouter, RouterProvider } from "@tanstack/react-router";

const router = createRouter({ routeTree });
declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router;
  }
}

const theme = createThemeWithUserColorScheme();

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <RouterProvider router={router} />
    </ThemeProvider>
  );
}
