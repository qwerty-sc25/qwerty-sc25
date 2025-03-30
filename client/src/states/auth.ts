import { atom } from "jotai";
import { Role } from "../types/role";

export namespace AuthState {
  export type LoggedInUser = {
    id: number;
    nickname: string;
    username: string;
    role: Role;
  };
  export const user = atom<LoggedInUser | undefined>();
}
