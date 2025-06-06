import {
  Toolbar,
  AppBar as MuiAppBar,
  Button,
  Divider,
  Avatar,
  Stack,
  IconButton,
  Chip,
} from "@mui/material";
import { createLink } from "@tanstack/react-router";
import { useAtom, useAtomValue } from "jotai";
import { AuthState } from "../states/auth";
import { Role } from "../types/role";
import useLogout from "../api/login/useLogout";
import LinkButton from "./LinkButton";
import NotificationButton from "./NotificationButton";
import State from "../states";
import { Menu, Nightlight, Sunny } from "@mui/icons-material";
import { useState } from "react";
import SideNavigationBar, { NavigationItem } from "./SideNavigatorBar";
import { useQuery } from "@tanstack/react-query";
import API_CLIENT from "../api/api";
import CreditPurchaseModal from "./CreditPurchaseModal";

export default function AppBar(props: {
  sideNavigationBarItemsWithGroups: NavigationItem[][];
}) {
  const { sideNavigationBarItemsWithGroups } = props;
  const user = useAtomValue(AuthState.user);
  const [colorScheme, setColorScheme] = useAtom(State.UI.userColorScheme);
  const { logout } = useLogout();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [creditPurchaseModalOpen, setCreditPurchaseModalOpen] = useState(false);

  const isUser = user && user.role === Role.ROLE_USER;
  const isAdmin = user && user.role === Role.ROLE_ADMIN;
  const isPublisher = user && user.role === Role.ROLE_PUBLISHER;

  const { data: myWallet } = useQuery({
    queryKey: ["myWallet"],
    queryFn: async () => {
      const response = await API_CLIENT.creditController.getMyWallet();
      if (!response.isSuccessful) {
        throw new Error(response.errorMessage);
      }
      return response.data;
    },
    enabled: isUser,
  });

  const onColorSchemeChangeButtonClicked = () => {
    setColorScheme((prev) => {
      const next = prev === "light" ? "dark" : "light";
      localStorage.setItem("colorScheme", next);
      return next;
    });
  };

  return (
    <>
      <CreditPurchaseModal
        open={creditPurchaseModalOpen}
        onClose={() => setCreditPurchaseModalOpen(false)}
      />
      <SideNavigationBar
        open={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
        itemsWithGroups={sideNavigationBarItemsWithGroups}
      />
      <MuiAppBar
        position="fixed"
        color="transparent"
        sx={{
          backdropFilter: "blur(10px)",
        }}
        elevation={0}
      >
        <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
          <Stack direction="row" spacing={1} alignItems="center">
            <IconButton
              onClick={() => {
                setSidebarOpen((prev) => !prev);
              }}
            >
              <Menu />
            </IconButton>
            <LogoButton to="/" sx={{ display: { xs: "none", sm: "flex" } }}>
              <img src="/logoTitle.png" alt="Logo" height={40} />
            </LogoButton>
          </Stack>
          <Stack direction="row" spacing={1}>
            <IconButton onClick={onColorSchemeChangeButtonClicked}>
              {colorScheme === "light" ? <Sunny /> : <Nightlight />}
            </IconButton>
            <Divider orientation="vertical" flexItem variant="middle" />
            {user ? (
              <>
                {myWallet && (
                  <Chip
                    label={`${myWallet.balance?.toLocaleString() ?? 0} 크레딧`}
                    color="info"
                    size="small"
                    sx={{
                      display: {
                        xs: "none",
                        sm: "flex",
                      },
                      alignSelf: "center",
                    }}
                    onClick={() => setCreditPurchaseModalOpen(true)}
                  />
                )}
                <LinkButton
                  color="inherit"
                  to={
                    isAdmin
                      ? "/mypage/admin"
                      : isPublisher
                        ? "/mypage/publisher"
                        : "/mypage"
                  }
                >
                  <Stack
                    direction="row"
                    alignItems="center"
                    sx={{ textWrap: "nowrap" }}
                  >
                    <Avatar
                      src={user.profileImageURL}
                      sx={{ width: 24, height: 24, mr: 1 }}
                    />
                    {user.role === Role.ROLE_USER
                      ? user.nickname
                      : user.role === Role.ROLE_PUBLISHER
                        ? user.publisherName
                        : "ADMIN"}
                  </Stack>
                </LinkButton>
                <NotificationButton />
                <Button
                  color="inherit"
                  onClick={logout}
                  sx={{ textWrap: "nowrap" }}
                >
                  로그아웃
                </Button>
              </>
            ) : (
              <LinkButton color="inherit" to={"/login"}>
                로그인
              </LinkButton>
            )}
          </Stack>
        </Toolbar>
      </MuiAppBar>
    </>
  );
}

const LogoButton = createLink(Button);
