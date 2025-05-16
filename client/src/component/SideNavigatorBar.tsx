import { Menu } from "@mui/icons-material";
import {
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  styled,
  Drawer as MuiDrawer,
  ListItemText,
  Theme,
  CSSObject,
} from "@mui/material";
import { createLink, LinkComponentProps } from "@tanstack/react-router";
import { JSX, useState } from "react";

const drawerWidth = 240;

export default function SideNavigationBar(props: {
  items: {
    to: LinkComponentProps["to"];
    icon: JSX.Element;
    label: string;
    disableShowActive?: boolean;
  }[];
}) {
  const { items } = props;
  const [open, setOpen] = useState(false);

  return (
    <Drawer open={open} variant="permanent">
      <List>
        <ListItem disablePadding>
          <ListItemButton
            onClick={() => {
              setOpen(!open);
            }}
          >
            <ListItemIcon>
              <Menu />
            </ListItemIcon>
          </ListItemButton>
        </ListItem>
        {items.map(({ to, icon, label, disableShowActive }) => {
          return (
            <LinkListItem disablePadding to={to} key={to}>
              {({ isActive }) => {
                return (
                  <ListItemButton
                    selected={disableShowActive ? false : isActive}
                  >
                    <ListItemIcon>{icon}</ListItemIcon>
                    <ListItemText primary={label} />
                  </ListItemButton>
                );
              }}
            </LinkListItem>
          );
        })}
      </List>
    </Drawer>
  );
}

const openedMixin = (theme: Theme): CSSObject => ({
  width: drawerWidth,
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  overflowX: "hidden",
});

const closedMixin = (theme: Theme): CSSObject => ({
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  overflowX: "hidden",
  width: `calc(${theme.spacing(7)} + 1px)`,
  [theme.breakpoints.up("sm")]: {
    width: `calc(${theme.spacing(7)} + 1px)`,
  },
});

const Drawer = styled(MuiDrawer, {
  shouldForwardProp: (prop) => prop !== "open",
})(({ theme }) => ({
  width: drawerWidth,
  flexShrink: 0,
  whiteSpace: "nowrap",
  boxSizing: "border-box",
  "& .MuiDrawer-paper": {
    marginTop: theme.spacing(8),
  },

  variants: [
    {
      props: ({ open }) => open,
      style: {
        ...openedMixin(theme),
        "& .MuiDrawer-paper": {
          ...openedMixin(theme),
        },
      },
    },
    {
      props: ({ open }) => !open,
      style: {
        ...closedMixin(theme),
        "& .MuiDrawer-paper": {
          ...closedMixin(theme),
        },
      },
    },
  ],
}));

const LinkListItem = createLink(ListItem);
