import { createFileRoute } from "@tanstack/react-router";
import { useSnackbar } from "notistack";
import DiscussionForm from "../../../../../../component/DiscussionForm";

export const Route = createFileRoute(
  "/groups/$groupId/activities/$activityId/discussions/new"
)({
  component: RouteComponent,
});

function RouteComponent() {
  const { activityId } = Route.useParams();
  const navigate = Route.useNavigate();
  const { enqueueSnackbar } = useSnackbar();
  const goToDiscussion = (discussionId?: number) => {
    if (!discussionId) {
      enqueueSnackbar("Invalid discussion ID", { variant: "error" });
      return;
    }
    navigate({
      from: Route.to,
      to: "/groups/$groupId/activities/$activityId/discussions/$discussionId",
      params: {
        discussionId: discussionId.toString(),
      },
    });
  };
  return (
    <DiscussionForm
      activityId={parseInt(activityId)}
      handlePostRoute={goToDiscussion}
      handleBack={() => {
        navigate({
          to: "..",
        });
      }}
    />
  );
}
