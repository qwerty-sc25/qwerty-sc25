import { Button, Card, CardContent, CardHeader } from "@mui/material";
import { createFileRoute } from "@tanstack/react-router";
import HighlightBrowserModal from "../../../component/HighlightBrowserModal";
import { useState } from "react";

export const Route = createFileRoute("/_pathlessLayout/admin/")({
  component: RouteComponent,
});

function RouteComponent() {
  const [openMemoBrowser, setOpenMemoBrowser] = useState(false);

  return (
    <Card>
      <CardHeader title="Dashboard" />
      <CardContent>
        <Button onClick={() => setOpenMemoBrowser(true)}>
          Open highlight browser
        </Button>
        <HighlightBrowserModal
          open={openMemoBrowser}
          onClose={() => {
            setOpenMemoBrowser(false);
          }}
          onSelectHighlight={() => {}}
          onUseHighlight={() => {}}
        />
      </CardContent>
    </Card>
  );
}
