import { GroupAdd } from "@mui/icons-material";
import { IconButton, Stack } from "@mui/material";
import { createFileRoute } from "@tanstack/react-router";
import GroupCreateModal from "../../../component/groupCreate/GroupCreateModal";
import { useState } from "react";
import GroupList from "../../../component/groupList";

export const Route = createFileRoute("/_pathlessLayout/mypage/groups")({
  component: RouteComponent,
});

function RouteComponent() {
  const [openGroupCreateModal, setOpenGroupCreateModal] = useState(false);

  return (
    <>
      <GroupCreateModal
        open={openGroupCreateModal}
        onClose={() => setOpenGroupCreateModal(false)}
      />
      <Stack spacing={2} sx={{ padding: 2 }}>
        {/* TODO */}
        <GroupList
          key="myGroups"
          size="small"
          title="내 그룹"
          action={
            <IconButton onClick={() => setOpenGroupCreateModal(true)}>
              <GroupAdd />
            </IconButton>
          }
        />
        {/* TODO */}
        <GroupList
          key="participantGroups"
          size="small"
          title="가입된 그룹"
          action={
            <IconButton onClick={() => setOpenGroupCreateModal(true)}>
              <GroupAdd />
            </IconButton>
          }
        />
      </Stack>
    </>
  );
}
