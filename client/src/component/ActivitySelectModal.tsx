import {
  Avatar,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  ListItemAvatar,
  ListItemButton,
  Menu,
  Typography,
} from "@mui/material";
import { useInfiniteQuery, useQuery } from "@tanstack/react-query";
import API_CLIENT from "../api/api";
import { ActivityFetchResponse } from "../api/api.gen";
import { useMemo, useState } from "react";
import { ExpandMore } from "@mui/icons-material";

export default function ActivitySelectModal(props: {
  description?: string;
  bookId?: number;
  open: boolean;
  onClose: () => void;
  onSelect?: (activity: { id: number; name: string }) => void;
}) {
  const { bookId, open, onClose, description, onSelect } = props;
  const [selectMenuAnchor, setSelectMenuAnchor] = useState<
    (EventTarget & HTMLDivElement) | null
  >(null);
  const [selectedActivity, setSelectedActivity] = useState<{
    id: number;
    name: string;
    bookImageURL: string;
  } | null>(null);

  const {
    data: activityPages,
    hasNextPage,
    fetchNextPage,
  } = useInfiniteQuery({
    queryKey: ["myActivities", bookId],
    queryFn: async ({ pageParam }) => {
      const response = await API_CLIENT.userController.getMyActivities({
        bookId,
        pageable: {
          page: pageParam,
          size: 10,
        },
      });
      if (!response.isSuccessful) {
        console.error(response.errorCode);
        throw new Error(response.errorCode);
      }
      return response.data;
    },
    initialPageParam: 0,
    getNextPageParam: (lastPage) => {
      if (lastPage.currentPage! + 1 < lastPage.totalPages!) {
        return lastPage.currentPage! + 1;
      }
      return undefined;
    },
  });

  const activities = useMemo(() => {
    if (!activityPages) {
      return [];
    }
    return activityPages.pages.flatMap((page) => {
      if (!page || !page.content) {
        return [];
      }
      return page.content;
    });
  }, [activityPages]);

  return (
    <Dialog
      open={open}
      onClose={onClose}
      slotProps={{ paper: { sx: { minWidth: 400 } } }}
    >
      <DialogTitle>활동 선택</DialogTitle>
      <DialogContent>
        {description && <Typography>{description}</Typography>}
        <Menu
          open={!!selectMenuAnchor}
          anchorEl={selectMenuAnchor}
          onClose={() => setSelectMenuAnchor(null)}
        >
          {activities.length === 0 && (
            <ListItemButton disabled>
              <Typography color="textSecondary">활동이 없습니다</Typography>
            </ListItemButton>
          )}
          {activities.map((activity) => (
            <ActivityMenuItem
              key={activity.activityId}
              activity={activity}
              handleSelect={(activity) => {
                setSelectedActivity(activity);
                setSelectMenuAnchor(null);
              }}
            />
          ))}
          {hasNextPage && (
            <Button
              fullWidth
              onClick={() => fetchNextPage()}
              sx={{ justifyContent: "center" }}
            >
              더보기
            </Button>
          )}
        </Menu>
        <ListItemButton
          onClick={(event) => {
            setSelectMenuAnchor(event.currentTarget);
          }}
          sx={{
            justifyContent: "space-between",
            textAlign: "left",
          }}
        >
          {selectedActivity?.bookImageURL && (
            <ListItemAvatar>
              <Avatar variant="rounded" src={selectedActivity.bookImageURL} />
            </ListItemAvatar>
          )}
          {selectedActivity?.name || "활동을 선택하세요"}
          <ExpandMore />
        </ListItemButton>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>취소</Button>
        <Button
          variant="contained"
          disabled={!selectedActivity}
          onClick={() => {
            if (onSelect && selectedActivity) {
              onSelect(selectedActivity);
            }
            onClose();
          }}
        >
          선택
        </Button>
      </DialogActions>
    </Dialog>
  );
}

function ActivityMenuItem(props: {
  activity: ActivityFetchResponse;
  handleSelect: (activity: {
    id: number;
    name: string;
    bookImageURL: string;
  }) => void;
}) {
  const { activity, handleSelect } = props;
  const { groupId } = activity;

  const { data: groupData } = useQuery({
    queryKey: ["group", groupId],
    queryFn: async () => {
      const response = await API_CLIENT.groupController.getGroup(groupId!);
      if (!response.isSuccessful) {
        console.error(response.errorCode);
        throw new Error(response.errorCode);
      }
      return response.data;
    },
    enabled: !!groupId,
  });

  const { data: bookData } = useQuery({
    queryKey: ["book", activity.bookId],
    queryFn: async () => {
      const response = await API_CLIENT.ebookController.getBook(
        activity.bookId!
      );
      if (!response.isSuccessful) {
        console.error(response.errorCode);
        throw new Error(response.errorCode);
      }
      return response.data;
    },
    enabled: !!activity.bookId,
  });

  const activityName =
    (groupData ? `${groupData.name} - ` : "") + `${activity.bookTitle}`;

  return (
    <ListItemButton
      onClick={() => {
        handleSelect({
          id: activity.activityId!,
          name: activityName,
          bookImageURL: bookData?.bookCoverImageURL || "",
        });
      }}
    >
      <ListItemAvatar>
        <Avatar
          variant="rounded"
          src={bookData ? bookData.bookCoverImageURL : ""}
        >
          {!bookData && <CircularProgress />}
        </Avatar>
      </ListItemAvatar>
      {activityName}
    </ListItemButton>
  );
}
