import { useCallback } from "react";
import { ApiBuilder } from "../apiBuilder";

export default function useJoin() {
  const join = useCallback(
    async ({
      nickname,
      username,
      password,
    }: {
      nickname: string;
      username: string;
      password: string;
    }) => {
      new ApiBuilder<"POST", "users/join">("POST", "users/join")
        .body({
          nickname,
          username,
          password,
        })
        .send()
        .then((response) => {
          if (response.isSuccessful) {
            console.log(response.data);
          } else {
            console.error(response.error);
          }
        });
    },
    []
  );

  return {
    join,
  };
}
