import { createFileRoute } from "@tanstack/react-router";
import { GroupInfo, GroupMembershipStatus } from "../../../types/groups";
import {
  Container,
  Divider,
  Paper,
  Skeleton,
  Stack,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import API_CLIENT from "../../../api/api";
import { ActivityCard } from "../../../component/_pathlessLayout/groups/$groupId/ActivityCard";
import { GroupHeader } from "../../../component/_pathlessLayout/groups/$groupId/GroupHeader";
import GroupReviewCard from "../../../component/GroupReviewCard";

export const Route = createFileRoute("/groups/$groupId/")({
  component: RouteComponent,
  params: {
    parse: (params) => {
      const groupId = parseInt(params.groupId);
      if (isNaN(groupId)) {
        throw new Error("Invalid groupId");
      }
      return {
        groupId,
      };
    },
  },
});

function RouteComponent() {
  const { groupId } = Route.useParams();

  const { data: group } = useQuery({
    queryKey: ["group", groupId],
    queryFn: async () => {
      const response = await API_CLIENT.groupController.getGroup(groupId);
      if (!response.isSuccessful) {
        throw new Error(response.errorMessage);
      }

      return response.data as GroupInfo;
    },
  });

  return (
    <Container sx={{ my: 8 }}>
      <Stack spacing={4}>
        <GroupHeader group={group} groupId={groupId} />
        <ActivityCard
          groupId={groupId}
          canCreate={group?.myMemberShipStatus === GroupMembershipStatus.OWNED}
        />
        <Paper sx={{ p: 2 }} variant="outlined">
          <Stack spacing={2}>
            <Typography variant="h4">모임 대화방</Typography>
            <Divider />
            <Stack spacing={2}>
              <Skeleton variant="rectangular" width="100%" height={200} />
            </Stack>
          </Stack>
        </Paper>
        <GroupReviewCard groupId={groupId} canWriteReview={true} />
      </Stack>
    </Container>
  );
}
