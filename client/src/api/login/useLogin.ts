import { useCallback } from "react";
import { ApiBuilder } from "../apiBuilder";
import { useSetAtom } from "jotai";
import State from "../../states";

export default function useLogin() {
  const setLoggedInUser = useSetAtom(State.Auth.user);

  const login = useCallback(
    async ({ username, password }: { username: string; password: string }) => {
      new ApiBuilder<"POST", "login">("POST", "login")
        .body({
          username,
          password,
        })
        .send()
        .then((response) => {
          if (response.isSuccessful) {
            setLoggedInUser(response.data);
            console.log(response.data);
          } else {
            console.error(response.error);
          }
        });
    },
    [setLoggedInUser]
  );

  return {
    login,
  };
}
