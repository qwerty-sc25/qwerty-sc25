import {
  Avatar,
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Box,
  Chip,
  TextField,
  InputAdornment,
  IconButton,
  Menu,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Paper,
  Divider,
  Tabs,
  Tab,
  FormControl,
  InputLabel,
  Select,
  Checkbox,
  Alert,
  LinearProgress,
} from "@mui/material";
import {
  Search as SearchIcon,
  MoreVert as MoreVertIcon,
  PersonAdd as PersonAddIcon,
  Group as GroupIcon,
  Settings as SettingsIcon,
  BarChart as BarChartIcon,
  Notifications as NotificationsIcon,
  FilterList as FilterListIcon,
  Download as DownloadIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
  AdminPanelSettings as AdminIcon,
} from "@mui/icons-material";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import API_CLIENT from "../../../../api/api";
import PageNavigation from "../../../../component/PageNavigation";

export const Route = createFileRoute("/_pathlessLayout/groups/$groupId/manage")(
  {
    component: RouteComponent,
    params: {
      parse: (params) => {
        const groupId = parseInt(params.groupId);
        if (isNaN(groupId)) {
          throw new Error("Invalid groupId");
        }
        return { groupId };
      },
    },
  }
);

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`group-management-tabpanel-${index}`}
      aria-labelledby={`group-management-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

function RouteComponent() {
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Stack spacing={4}>
        {/* 헤더 영역 */}
        <Box>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            그룹 관리
          </Typography>
          <Typography variant="body1" color="text.secondary">
            멤버 관리, 설정 변경 및 그룹 운영을 관리하세요
          </Typography>
        </Box>

        {/* 대시보드 카드 */}
        <GroupDashboard />

        {/* 탭 네비게이션 */}
        <Paper elevation={0} sx={{ borderBottom: 1, borderColor: "divider" }}>
          <Tabs
            value={tabValue}
            onChange={handleTabChange}
            aria-label="그룹 관리 탭"
            variant="scrollable"
            scrollButtons="auto"
          >
            <Tab
              icon={<PersonAddIcon />}
              label="가입 신청"
              iconPosition="start"
            />
            <Tab icon={<GroupIcon />} label="멤버 관리" iconPosition="start" />
            <Tab
              icon={<SettingsIcon />}
              label="그룹 설정"
              iconPosition="start"
            />
            <Tab icon={<BarChartIcon />} label="통계" iconPosition="start" />
          </Tabs>
        </Paper>

        {/* 탭 패널 */}
        <TabPanel value={tabValue} index={0}>
          <PendingMemberCard />
        </TabPanel>

        <TabPanel value={tabValue} index={1}>
          <MembersCard />
        </TabPanel>

        <TabPanel value={tabValue} index={2}>
          <GroupSettingsCard />
        </TabPanel>

        <TabPanel value={tabValue} index={3}>
          <GroupStatisticsCard />
        </TabPanel>
      </Stack>
    </Container>
  );
}

function GroupDashboard() {
  const { groupId } = Route.useParams();

  // Mock data - 실제로는 API에서 가져와야 함
  const dashboardData = {
    totalMembers: 156,
    pendingRequests: 8,
    activeMembers: 89,
    newMembersThisMonth: 12,
  };

  return (
    <Grid container spacing={3}>
      <Grid item xs={12} sm={6} md={3}>
        <Paper sx={{ p: 3, textAlign: "center" }}>
          <GroupIcon color="primary" sx={{ fontSize: 40, mb: 1 }} />
          <Typography variant="h4" fontWeight="bold">
            {dashboardData.totalMembers}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            총 멤버 수
          </Typography>
        </Paper>
      </Grid>

      <Grid item xs={12} sm={6} md={3}>
        <Paper sx={{ p: 3, textAlign: "center" }}>
          <NotificationsIcon color="warning" sx={{ fontSize: 40, mb: 1 }} />
          <Typography variant="h4" fontWeight="bold">
            {dashboardData.pendingRequests}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            대기 중인 신청
          </Typography>
        </Paper>
      </Grid>

      <Grid item xs={12} sm={6} md={3}>
        <Paper sx={{ p: 3, textAlign: "center" }}>
          <CheckCircleIcon color="success" sx={{ fontSize: 40, mb: 1 }} />
          <Typography variant="h4" fontWeight="bold">
            {dashboardData.activeMembers}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            활성 멤버
          </Typography>
        </Paper>
      </Grid>

      <Grid item xs={12} sm={6} md={3}>
        <Paper sx={{ p: 3, textAlign: "center" }}>
          <PersonAddIcon color="info" sx={{ fontSize: 40, mb: 1 }} />
          <Typography variant="h4" fontWeight="bold">
            +{dashboardData.newMembersThisMonth}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            이번 달 신규 멤버
          </Typography>
        </Paper>
      </Grid>
    </Grid>
  );
}

function PendingMemberCard() {
  const { groupId } = Route.useParams();
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [selectedRequests, setSelectedRequests] = useState<number[]>([]);

  const { data: pendingRequests, refetch } = useQuery({
    queryKey: ["pendingList", groupId, page],
    queryFn: async () => {
      const response = await API_CLIENT.groupController.getPendingList(
        groupId,
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

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedRequests(pendingRequests?.map((req) => req.userId!) || []);
    } else {
      setSelectedRequests([]);
    }
  };

  const handleSelectRequest = (userId: number, checked: boolean) => {
    if (checked) {
      setSelectedRequests((prev) => [...prev, userId]);
    } else {
      setSelectedRequests((prev) => prev.filter((id) => id !== userId));
    }
  };

  const handleBulkApprove = async () => {
    if (selectedRequests.length === 0) return;

    // 벌크 승인 로직
    for (const userId of selectedRequests) {
      await API_CLIENT.groupController.approveJoinRequest(groupId, userId);
    }

    setSelectedRequests([]);
    refetch();
    alert(`${selectedRequests.length}명의 신청이 승인되었습니다.`);
  };

  const handleBulkReject = async () => {
    if (selectedRequests.length === 0) return;

    // 벌크 거절 로직
    for (const userId of selectedRequests) {
      await API_CLIENT.groupController.rejectJoinRequest(groupId, userId);
    }

    setSelectedRequests([]);
    refetch();
    alert(`${selectedRequests.length}명의 신청이 거절되었습니다.`);
  };

  const onApproveButtonClicked = async (request: any) => {
    const response = await API_CLIENT.groupController.approveJoinRequest(
      groupId,
      request.userId!
    );
    if (!response.isSuccessful) {
      alert("요청 수락에 실패했습니다.");
      return;
    }
    refetch();
    alert("요청이 수락되었습니다.");
  };

  const onRejectButtonClicked = async (request: any) => {
    const response = await API_CLIENT.groupController.rejectJoinRequest(
      groupId,
      request.userId!
    );
    if (!response.isSuccessful) {
      alert("요청 거절에 실패했습니다.");
      return;
    }
    refetch();
    alert("요청이 거절되었습니다.");
  };

  return (
    <Card>
      <CardHeader
        title="가입 신청 관리"
        action={
          selectedRequests.length > 0 && (
            <Stack direction="row" spacing={1}>
              <Button
                variant="contained"
                color="primary"
                size="small"
                onClick={handleBulkApprove}
              >
                선택 승인 ({selectedRequests.length})
              </Button>
              <Button
                variant="outlined"
                color="error"
                size="small"
                onClick={handleBulkReject}
              >
                선택 거절
              </Button>
            </Stack>
          )
        }
      />
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
                  <TableCell padding="checkbox">
                    <Checkbox
                      checked={
                        selectedRequests.length === pendingRequests?.length &&
                        pendingRequests.length > 0
                      }
                      indeterminate={
                        selectedRequests.length > 0 &&
                        selectedRequests.length < (pendingRequests?.length || 0)
                      }
                      onChange={(e) => handleSelectAll(e.target.checked)}
                    />
                  </TableCell>
                  <TableCell>사용자 정보</TableCell>
                  <TableCell>신청일</TableCell>
                  <TableCell>작업</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {pendingRequests ? (
                  pendingRequests.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={4} align="center" sx={{ py: 4 }}>
                        <Stack spacing={2} alignItems="center">
                          <PersonAddIcon
                            sx={{ fontSize: 48, color: "text.disabled" }}
                          />
                          <Typography color="text.secondary">
                            대기 중인 가입 신청이 없습니다.
                          </Typography>
                        </Stack>
                      </TableCell>
                    </TableRow>
                  ) : (
                    pendingRequests.map((pending) => (
                      <TableRow key={pending.userId}>
                        <TableCell padding="checkbox">
                          <Checkbox
                            checked={selectedRequests.includes(pending.userId!)}
                            onChange={(e) =>
                              handleSelectRequest(
                                pending.userId!,
                                e.target.checked
                              )
                            }
                          />
                        </TableCell>
                        <TableCell>
                          <Stack
                            direction="row"
                            spacing={2}
                            alignItems="center"
                          >
                            <Avatar src={pending.profileImageURL} />
                            <Box>
                              <Typography variant="subtitle2">
                                {pending.nickname}
                              </Typography>
                              <Typography
                                variant="caption"
                                color="text.secondary"
                              >
                                ID: {pending.userId}
                              </Typography>
                            </Box>
                          </Stack>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">
                            {pending.createdAt
                              ? new Date(pending.createdAt).toLocaleDateString(
                                  "ko-KR"
                                )
                              : "-"}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Stack direction="row" spacing={1}>
                            <Button
                              variant="contained"
                              color="primary"
                              size="small"
                              startIcon={<CheckCircleIcon />}
                              onClick={() => onApproveButtonClicked(pending)}
                            >
                              승인
                            </Button>
                            <Button
                              variant="outlined"
                              color="error"
                              size="small"
                              startIcon={<CancelIcon />}
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
                  <TableRow>
                    <TableCell colSpan={4}>
                      <LinearProgress />
                    </TableCell>
                  </TableRow>
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
  const [searchTerm, setSearchTerm] = useState("");
  const [roleFilter, setRoleFilter] = useState("all");
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedMember, setSelectedMember] = useState<any>(null);

  const { data: members } = useQuery({
    queryKey: ["groupMembers", groupId, page],
    queryFn: async () => {
      const response = await API_CLIENT.groupController.getGroupMembers(
        groupId,
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

  const handleMenuClick = (
    event: React.MouseEvent<HTMLElement>,
    member: any
  ) => {
    setAnchorEl(event.currentTarget);
    setSelectedMember(member);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedMember(null);
  };

  return (
    <Card>
      <CardHeader
        title="멤버 관리"
        action={
          <Stack direction="row" spacing={1}>
            <Button
              variant="outlined"
              startIcon={<DownloadIcon />}
              size="small"
            >
              내보내기
            </Button>
            <Button
              variant="contained"
              startIcon={<PersonAddIcon />}
              size="small"
            >
              멤버 초대
            </Button>
          </Stack>
        }
      />
      <CardContent>
        <Stack spacing={3}>
          {/* 검색 및 필터 */}
          <Stack direction="row" spacing={2} alignItems="center">
            <TextField
              placeholder="멤버 검색..."
              size="small"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
              sx={{ minWidth: 300 }}
            />

            <FormControl size="small" sx={{ minWidth: 120 }}>
              <InputLabel>역할</InputLabel>
              <Select
                value={roleFilter}
                label="역할"
                onChange={(e) => setRoleFilter(e.target.value)}
              >
                <MenuItem value="all">전체</MenuItem>
                <MenuItem value="admin">관리자</MenuItem>
                <MenuItem value="member">일반 멤버</MenuItem>
              </Select>
            </FormControl>
          </Stack>

          <PageNavigation
            pageZeroBased={page}
            setPage={setPage}
            totalPages={totalPages}
          />

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>멤버 정보</TableCell>
                  <TableCell>역할</TableCell>
                  <TableCell>가입일</TableCell>
                  <TableCell>최근 활동</TableCell>
                  <TableCell>작업</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {members ? (
                  members.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={5} align="center" sx={{ py: 4 }}>
                        <Stack spacing={2} alignItems="center">
                          <GroupIcon
                            sx={{ fontSize: 48, color: "text.disabled" }}
                          />
                          <Typography color="text.secondary">
                            멤버가 없습니다.
                          </Typography>
                        </Stack>
                      </TableCell>
                    </TableRow>
                  ) : (
                    members.map((member) => (
                      <TableRow key={member.userId}>
                        <TableCell>
                          <Stack
                            direction="row"
                            spacing={2}
                            alignItems="center"
                          >
                            <Avatar src={member.profileImageURL} />
                            <Box>
                              <Typography variant="subtitle2">
                                {member.nickname}
                              </Typography>
                              <Typography
                                variant="caption"
                                color="text.secondary"
                              >
                                ID: {member.userId}
                              </Typography>
                            </Box>
                          </Stack>
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={member.role || "멤버"}
                            color={
                              member.role === "admin" ? "primary" : "default"
                            }
                            size="small"
                            icon={
                              member.role === "admin" ? (
                                <AdminIcon />
                              ) : undefined
                            }
                          />
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">
                            {member.createdAt
                              ? new Date(member.createdAt).toLocaleDateString(
                                  "ko-KR"
                                )
                              : "-"}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" color="text.secondary">
                            {member.lastActiveAt
                              ? new Date(
                                  member.lastActiveAt
                                ).toLocaleDateString("ko-KR")
                              : "정보 없음"}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <IconButton
                            size="small"
                            onClick={(e) => handleMenuClick(e, member)}
                          >
                            <MoreVertIcon />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))
                  )
                ) : (
                  <TableRow>
                    <TableCell colSpan={5}>
                      <LinearProgress />
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>

          {/* 멤버 액션 메뉴 */}
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
          >
            <MenuItem onClick={handleMenuClose}>
              <EditIcon sx={{ mr: 1 }} />
              역할 변경
            </MenuItem>
            <MenuItem onClick={handleMenuClose}>
              <NotificationsIcon sx={{ mr: 1 }} />
              메시지 보내기
            </MenuItem>
            <Divider />
            <MenuItem onClick={handleMenuClose} sx={{ color: "error.main" }}>
              <DeleteIcon sx={{ mr: 1 }} />
              멤버 제거
            </MenuItem>
          </Menu>

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

function GroupSettingsCard() {
  return (
    <Card>
      <CardHeader title="그룹 설정" />
      <CardContent>
        <Stack spacing={3}>
          <Alert severity="info">그룹 설정 기능은 개발 중입니다.</Alert>
          <Typography variant="body2" color="text.secondary">
            • 그룹 정보 수정
            <br />
            • 공개/비공개 설정
            <br />
            • 가입 승인 방식 설정
            <br />
            • 권한 관리
            <br />• 그룹 삭제
          </Typography>
        </Stack>
      </CardContent>
    </Card>
  );
}

function GroupStatisticsCard() {
  return (
    <Card>
      <CardHeader title="통계 및 분석" />
      <CardContent>
        <Stack spacing={3}>
          <Alert severity="info">통계 기능은 개발 중입니다.</Alert>
          <Typography variant="body2" color="text.secondary">
            • 멤버 활동 통계
            <br />
            • 가입 신청 추이
            <br />
            • 게시글/댓글 분석
            <br />
            • 참여도 분석
            <br />• 월별/주별 리포트
          </Typography>
        </Stack>
      </CardContent>
    </Card>
  );
}
