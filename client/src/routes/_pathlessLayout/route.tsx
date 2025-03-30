import { createFileRoute, Outlet } from "@tanstack/react-router";
import AppBar from "../../component/AppBar";

export const Route = createFileRoute("/_pathlessLayout")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <>
      <AppBar />
      <Outlet />
    </>
  );
}
