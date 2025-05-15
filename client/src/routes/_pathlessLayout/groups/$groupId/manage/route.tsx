import { Box, Container } from "@mui/material";
import { createFileRoute, Outlet } from "@tanstack/react-router";
import SideNavigationBar from "../../../../../component/SideNavigatorBar";
import { Home, People } from "@mui/icons-material";

export const Route = createFileRoute("/_pathlessLayout/groups/$groupId/manage")(
  {
    component: RouteComponent,
  }
);

function RouteComponent() {
  return (
    <Box sx={{ display: "flex" }}>
      <SideNavigationBar
        items={[
          {
            to: "/groups/$groupId/manage",
            icon: <Home />,
            label: "Home",
            disableShowActive: true,
          },
          {
            to: "/groups/$groupId/manage/member",
            icon: <People />,
            label: "모임원",
          },
        ]}
      />
      <Container sx={{ margin: 2, marginX: "auto" }}>
        <Outlet />
      </Container>
    </Box>
  );
}
