import { Delete, Notifications } from "@mui/icons-material";
import {
  Badge,
  Box,
  Card,
  Grid,
  IconButton,
  LinearProgress,
  Popover,
  Stack,
  Typography,
} from "@mui/material";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import API_CLIENT from "../api/api";
import { useRef, useState } from "react";
import { Notification } from "../types/notification";
import { useSnackbar } from "notistack";
import LinkButton from "./LinkButton";
import { Client } from "@stomp/stompjs";
import { ENV } from "../env";
import { useAtomValue } from "jotai";
import State from "../states";
import { Role } from "../types/role";

export default function NotificationButton() {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const clientRef = useRef<Client | null>(null);
  const user = useAtomValue(State.Auth.user);
  const userId = user?.role === Role.ROLE_USER && user.userId;
  const { enqueueSnackbar } = useSnackbar();

  const { data: notifications, refetch: refetchNotifications } = useQuery({
    queryKey: ["notifications"],
    queryFn: async () => {
      const response =
        await API_CLIENT.notificationController.getNotifications1({
          pageable: {
            page: 0,
            size: 5,
          },
        });
      if (!response.isSuccessful) {
        throw new Error(response.errorMessage);
      }
      const notifications = response.data.content! as Notification[];
      return notifications.filter(
        (notification) => notification.isRead === false
      );
    },
    placeholderData: keepPreviousData,
    enabled: !!userId,
  });

  const { data: _client } = useQuery({
    queryKey: ["notificationClient", userId],
    queryFn: async () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
      const client = new Client({
        brokerURL: `${ENV.CHAEKIT_API_ENDPOINT}/ws`,
        onConnect: () => {
          client.subscribe(`/topic/notification/${userId}`, function (message) {
            const notification = JSON.parse(message.body);
            console.log(notification);
            enqueueSnackbar(notification.message, { variant: "info" });
            refetchNotifications();
          });
        },
      });
      client.activate();
      clientRef.current = client;
      return client;
    },
    enabled: !!userId,
  });

  return (
    <>
      <Popover
        open={!!anchorEl}
        anchorEl={anchorEl}
        onClose={() => setAnchorEl(null)}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
      >
        <Stack spacing={2} padding={1} sx={{ maxWidth: 352 }}>
          {notifications?.map((notification) => (
            <NotificationItem
              notification={notification}
              key={notification.id}
              refetchNotifications={refetchNotifications}
            />
          ))}
          {notifications?.length === 0 && (
            <Typography>알림이 없습니다</Typography>
          )}
        </Stack>
      </Popover>
      <IconButton onClick={(e) => setAnchorEl(e.currentTarget)}>
        <Badge badgeContent={notifications?.length} color="info">
          <Notifications />
        </Badge>
      </IconButton>
    </>
  );
}

function NotificationItem({
  notification,
  refetchNotifications,
}: {
  notification: Notification;
  refetchNotifications: () => void;
}) {
  const onMarkAsRead = () => {
    API_CLIENT.notificationController
      .markAsRead1(notification.id)
      .then((response) => {
        if (!response.isSuccessful) {
          throw new Error(response.errorMessage);
        }
        refetchNotifications();
      });
  };

  const notRead = !notification.isRead;

  return (
    <Card variant="outlined">
      <LinearProgress
        value={notRead ? 100 : 0}
        variant="determinate"
        color={notRead ? "info" : "secondary"}
      />
      <Stack spacing={0.5} sx={{ padding: 1 }}>
        <Box sx={{ display: "flex", alignItems: "flex-start" }}>
          <Typography
            variant="body2"
            color={notRead ? "textPrimary" : "textDisabled"}
          >
            {notification.message}
          </Typography>
          {notRead && (
            <IconButton
              size="small"
              sx={{ marginLeft: "auto" }}
              onClick={onMarkAsRead}
            >
              <Delete />
            </IconButton>
          )}
        </Box>
        <NotificationItemContent notification={notification} />
        <Typography variant="caption" color="textSecondary" textAlign={"right"}>
          {new Date(notification.createdAt).toLocaleString("ko-KR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
          })}
        </Typography>
      </Stack>
    </Card>
  );
}

function NotificationItemContent({
  notification,
}: {
  notification: Notification;
}) {
  switch (notification.type) {
    case "GROUP_JOIN_REQUEST": {
      const { groupId } = notification;
      return (
        <ButtonContainer>
          <LinkButton
            size="small"
            to={"/groups/$groupId/manage"}
            params={{
              groupId,
            }}
          >
            관리페이지로 이동
          </LinkButton>
        </ButtonContainer>
      );
    }
    case "GROUP_JOIN_APPROVED": {
      const { groupId } = notification;
      return (
        <ButtonContainer>
          <LinkButton
            size="small"
            to={"/groups/$groupId"}
            params={{
              groupId,
            }}
          >
            모임페이지로 이동
          </LinkButton>
        </ButtonContainer>
      );
    }
    case "GROUP_JOIN_REJECTED": {
      return null;
    }
    case "PUBLISHER_JOIN_REQUEST": {
      return (
        <ButtonContainer>
          <LinkButton size="small" to={"/admin/publisher"}>
            가입대기목록으로 이동
          </LinkButton>
        </ButtonContainer>
      );
    }
    case "PUBLISHER_APPROVED": {
      return null;
    }
    case "PUBLISHER_REJECTED": {
      return null;
    }
    case "DISCUSSION_COMMENT": {
      return null;
    }
    case "COMMENT_REPLY": {
      return null;
    }
    case "HIGHLIGHT_COMMENT": {
      return null;
    }
    case "HIGHLIGHT_COMMENT_REPLY": {
      return null;
    }
    case "GROUP_BANNED": {
      return null;
    }
    default: {
      throw new Error("Unknown notification type");
      return null;
    }
  }
}

function ButtonContainer({ children }: { children: React.ReactNode }) {
  return (
    <Grid
      container
      direction={"row"}
      spacing={1}
      sx={{
        flexGrow: 1,
        alignContent: "flex-end",
        flexWrap: "wrap",
        justifyContent: "flex-end",
      }}
    >
      {children}
    </Grid>
  );
}
