export default function getUserColorScheme() {
  // 테스트 환경에서 localStorage가 없는 경우를 대비한 안전한 접근
  let userColorSchemeLocalStorage: "light" | "dark" | null = null;

  try {
    if (typeof localStorage !== "undefined") {
      userColorSchemeLocalStorage = localStorage.getItem("colorScheme") as
        | "light"
        | "dark"
        | null;
    }
  } catch (error) {
    // localStorage 접근 실패 시 무시
  }

  if (userColorSchemeLocalStorage) {
    return userColorSchemeLocalStorage;
  }

  // window.matchMedia도 안전하게 접근
  try {
    return window.matchMedia &&
      window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  } catch (error) {
    return "light";
  }
}
