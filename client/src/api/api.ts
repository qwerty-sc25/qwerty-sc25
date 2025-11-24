import { getDefaultStore } from "jotai";
import { ENV } from "../env";
import { Api } from "./api.gen";
import State from "../states";

const api = new Api<undefined>({
  baseUrl: ENV.CHAEKIT_API_ENDPOINT,
  baseApiParams: {
    secure: true,
  },
  securityWorker: async () => {
    const user = getDefaultStore().get(State.Auth.user);
    const accessToken = user?.accessToken; // 옵셔널 체이닝 사용

    // 1. 인증 헤더 준비
    const authHeaders = {
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
    };

    // 2. ngrok 스킵 헤더 준비 (개발 환경일 경우에만 추가)
    const ngrokHeaders = { "ngrok-skip-browser-warning": "true" };

    return {
      headers: {
        ...authHeaders,
        ...ngrokHeaders, // 인증 헤더와 ngrok 헤더를 함께 적용
      },
    };
  },
  onExpiredAccessToken: () => {
    getDefaultStore().set(
      State.Auth.refreshState,
      State.Auth.RefreshState.NEED_REFRESH,
    );
  },
});

const API_CLIENT = api;

export default API_CLIENT;
