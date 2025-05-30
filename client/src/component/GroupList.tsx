import {
  Box,
  Card,
  CardActionArea,
  CardMedia,
  Container,
  Grid,
  Skeleton,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import API_CLIENT from "../api/api";
import { JSX, PropsWithChildren, useState } from "react";
import { useNavigate } from "@tanstack/react-router";
import PageNavigation from "./PageNavigation";
import { GroupInfo } from "../types/groups";
const ITEM_HEIGHT = 256;
const IMAGE_HEIGHT = 128;

export enum GroupListKind {
  ALL_GROUP = "ALL_GROUP",
  MY_GROUP = "MY_GROUP",
  JOINED_GROUP = "JOINED_GROUP",
}

export default function GroupList(props: {
  size: "small" | "large";
  action?: JSX.Element;
  title: string;
  key: string;
  kind?: GroupListKind;
}) {
  const { size, action, title, key } = props;

  const [page, setPage] = useState(0);
  const [sort, _setSort] = useState<string[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();
  const theme = useTheme();

  const pageSize = size === "small" ? 6 : 12;
  const groupType =
    props.kind === undefined ? GroupListKind.ALL_GROUP : props.kind;

  const { data: groups } = useQuery({
    queryKey: [key, groupType, page, sort, pageSize],
    queryFn: async () => {
      const response = await getFetchFunction(groupType)({
        page,
        size: pageSize,
        sort,
      });

      if (response.isSuccessful) {
        setTotalPages(response.data.totalPages!);
        return response.data.content! as GroupInfo[];
      }

      throw new Error(response.errorMessage);
    },
    initialData: new Array(pageSize).fill(undefined) as (
      | GroupInfo
      | undefined
    )[],
    placeholderData: keepPreviousData,
  });

  return (
    <Container>
      <Stack spacing={1}>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
        >
          <Typography variant="h4">{title}</Typography>
          {action}
        </Stack>
        <PageNavigation
          pageZeroBased={page}
          setPage={setPage}
          totalPages={totalPages}
        />
        <Grid container spacing={2}>
          {groups.length === 0 && (
            <Grid size={12} textAlign={"center"}>
              <Typography variant="body1" color="textSecondary">
                {groupType === GroupListKind.MY_GROUP
                  ? "아직 생성한 모임이 없습니다."
                  : groupType === GroupListKind.JOINED_GROUP
                    ? "아직 가입한 모임이 없습니다."
                    : "아직 모임이 없습니다."}
              </Typography>
            </Grid>
          )}
          {groups?.map((group, index) =>
            group ? (
              <ItemContainer key={group.groupId}>
                <Card sx={{ height: ITEM_HEIGHT }} variant="outlined">
                  <CardActionArea
                    sx={{
                      height: ITEM_HEIGHT,
                      display: "flex",
                      alignItems: "start",
                      flexDirection: "column",
                      justifyContent: "start",
                    }}
                    onClick={() => {
                      navigate({
                        to: "/groups/$groupId",
                        params: { groupId: group.groupId! },
                      });
                    }}
                  >
                    <CardMedia
                      height={IMAGE_HEIGHT}
                      component={"img"}
                      image={
                        group.groupImageURL || "https://picsum.photos/192/192"
                      }
                    />
                    <Box
                      sx={{
                        p: 1,
                        gap: 0.5,
                        display: "flex",
                        flexDirection: "column",
                        "&::after": {
                          content: '""',
                          position: "absolute",
                          left: 0,
                          right: 0,
                          bottom: 0,
                          height: "2em",
                          pointerEvents: "none",
                          background: `linear-gradient(to bottom, rgba(255,255,255,0) 0%, ${theme.palette.background.paper} 100%)`,
                        },
                      }}
                    >
                      <Typography
                        variant="h6"
                        sx={{
                          textOverflow: "ellipsis",
                          whiteSpace: "nowrap",
                        }}
                      >
                        {group.name}
                      </Typography>
                      <Typography
                        variant="body2"
                        color="textSecondary"
                        component={"div"}
                        sx={{
                          display: "-webkit-box",
                          overflow: "hidden",
                          whiteSpace: "normal",
                        }}
                      >
                        {group.description}
                      </Typography>
                    </Box>
                  </CardActionArea>
                </Card>
              </ItemContainer>
            ) : (
              <ItemContainer key={`placeholder-${index}`}>
                <Skeleton height={ITEM_HEIGHT} variant="rounded" />
              </ItemContainer>
            )
          )}
        </Grid>
        <PageNavigation
          pageZeroBased={page}
          setPage={setPage}
          totalPages={totalPages}
        />
      </Stack>
    </Container>
  );
}

function ItemContainer(props: PropsWithChildren) {
  const { children } = props;

  return (
    <Grid
      size={{
        xs: 12,
        sm: 6,
        md: 4,
        lg: 4,
        xl: 4,
      }}
    >
      {children}
    </Grid>
  );
}

function getFetchFunction(groupType: GroupListKind) {
  switch (groupType) {
    case GroupListKind.ALL_GROUP:
      return API_CLIENT.groupController.getAllGroups;
    case GroupListKind.MY_GROUP:
      return API_CLIENT.groupController.getCreatedGroups;
    case GroupListKind.JOINED_GROUP:
      return API_CLIENT.groupController.getJoinedGroups;
    default:
      throw new Error("Invalid group type");
  }
}
