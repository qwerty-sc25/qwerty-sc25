import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { createFileRoute } from "@tanstack/react-router";
import API_CLIENT from "../../../../../api/api";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import PageNavigation from "../../../../../component/PageNavigation";

export const Route = createFileRoute(
  "/_pathlessLayout/groups/$groupId/manage/member"
)({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <Stack spacing={4}>
      <PendingMemberCard />
      <MembersCard />
    </Stack>
  );
}

function PendingMemberCard() {
  const { groupId } = Route.useParams();

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const { data: pendingRequests, refetch } = useQuery({
    queryKey: ["getPendingList", groupId, page],
    queryFn: async () => {
      const groupIdNumber = parseInt(groupId);
      if (isNaN(groupIdNumber)) {
        throw new Error("Invalid group ID");
      }
      const response = await API_CLIENT.groupController.getPendingList(
        groupIdNumber,
        {
          page,
          size: 20,
        }
      );
      if (!response.isSuccessful) {
        throw new Error(response.errorMessage);
      }
      setTotalPages(response.data.totalPages!);
      return response.data.content;
    },
    initialData: [],
  });

  const onApproveButtonClicked = async (
    request: NonNullable<typeof pendingRequests>[number]
  ) => {
    const groupIdNumber = parseInt(groupId);
    if (isNaN(groupIdNumber)) {
      throw new Error("Invalid group ID");
    }

    const response = await API_CLIENT.groupController.approveJoinRequest(
      groupIdNumber,
      request.userId!
    );
    if (!response.isSuccessful) {
      console.error("Failed to accept request:", response.errorMessage);
      alert("요청 수락에 실패했습니다. 잠시 후 다시 시도해주세요.");
      return;
    }

    refetch();
    alert("요청이 수락되었습니다.");
  };
  const onRejectButtonClicked = async (
    request: NonNullable<typeof pendingRequests>[number]
  ) => {
    const groupIdNumber = parseInt(groupId);
    if (isNaN(groupIdNumber)) {
      throw new Error("Invalid group ID");
    }

    const response = await API_CLIENT.groupController.rejectJoinRequest(
      groupIdNumber,
      request.userId!
    );
    if (!response.isSuccessful) {
      console.error("Failed to reject request:", response.errorMessage);
      alert("요청 거절에 실패했습니다. 잠시 후 다시 시도해주세요.");
      return;
    }

    refetch();
    alert("요청이 거절되었습니다.");
  };

  return (
    <Card>
      <CardHeader title="대기중인 모임원 가입 신청 목록" />
      <CardContent>
        <Stack spacing={2}>
          <PageNavigation
            pageZeroBased={page}
            setPage={setPage}
            totalPages={totalPages}
          />
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>닉네임</TableCell>
                  <TableCell>작업</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {pendingRequests ? (
                  pendingRequests.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={7} align="center">
                        대기 중인 신청이 없습니다.
                      </TableCell>
                    </TableRow>
                  ) : (
                    pendingRequests.map((pending) => (
                      <TableRow key={pending.userId}>
                        <TableCell>{pending.userId}</TableCell>
                        <TableCell>
                          <Stack
                            direction={"row"}
                            spacing={1}
                            alignItems={"center"}
                          >
                            <Typography>{pending.nickname}</Typography>
                          </Stack>
                        </TableCell>
                        <TableCell>
                          <Stack direction={"row"} spacing={1}>
                            <Button
                              variant="contained"
                              color="primary"
                              onClick={() => onApproveButtonClicked(pending)}
                            >
                              승인
                            </Button>
                            <Button
                              variant="contained"
                              color="error"
                              onClick={() => onRejectButtonClicked(pending)}
                            >
                              거절
                            </Button>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    ))
                  )
                ) : (
                  <Skeleton />
                )}
              </TableBody>
            </Table>
          </TableContainer>
          <PageNavigation
            pageZeroBased={page}
            setPage={setPage}
            totalPages={totalPages}
          />
        </Stack>
      </CardContent>
    </Card>
  );
}

function MembersCard() {
  const { groupId } = Route.useParams();

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const { data: pendingRequests } = useQuery({
    queryKey: ["getPendingList", groupId, page],
    queryFn: async () => {
      const groupIdNumber = parseInt(groupId);
      if (isNaN(groupIdNumber)) {
        throw new Error("Invalid group ID");
      }
      // TODO: Fetch members
      const response = await API_CLIENT.groupController.getPendingList(
        groupIdNumber,
        {
          page,
          size: 20,
        }
      );
      if (!response.isSuccessful) {
        throw new Error(response.errorMessage);
      }
      setTotalPages(response.data.totalPages!);
      return response.data.content;
    },
    initialData: [],
  });

  return (
    <Card>
      <CardHeader title="모임원 목록" />
      <CardContent>
        <Stack spacing={2}>
          <PageNavigation
            pageZeroBased={page}
            setPage={setPage}
            totalPages={totalPages}
          />
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>닉네임</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {pendingRequests ? (
                  pendingRequests.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={7} align="center">
                        대기 중인 신청이 없습니다.
                      </TableCell>
                    </TableRow>
                  ) : (
                    pendingRequests.map((pending) => (
                      <TableRow key={pending.userId}>
                        <TableCell>{pending.userId}</TableCell>
                        <TableCell>
                          <Stack
                            direction={"row"}
                            spacing={1}
                            alignItems={"center"}
                          >
                            <Typography>{pending.nickname}</Typography>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    ))
                  )
                ) : (
                  <Skeleton />
                )}
              </TableBody>
            </Table>
          </TableContainer>
          <PageNavigation
            pageZeroBased={page}
            setPage={setPage}
            totalPages={totalPages}
          />
        </Stack>
      </CardContent>
    </Card>
  );
}
