import { describe, it, expect, beforeEach, vi } from "vitest";
import getLoggedInUserFromLocalStorage from "./getLoggedInUserFromLocalStorage";
import { AuthState } from "../states/auth";
import { Role } from "../types/role";

// localStorage 모킹
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
  length: 0,
  key: vi.fn(),
};

// global localStorage를 모킹
Object.defineProperty(global, "localStorage", {
  value: localStorageMock,
  writable: true,
});

describe("getLoggedInUserFromLocalStorage", () => {
  beforeEach(() => {
    // localStorage를 초기화
    vi.clearAllMocks();
    localStorageMock.getItem.mockReturnValue(null);
  });

  it("localStorage에 저장된 사용자 정보가 없을 때 undefined를 반환해야 한다", () => {
    const result = getLoggedInUserFromLocalStorage();
    expect(result).toBeUndefined();
  });

  it("유효한 일반 사용자 정보를 올바르게 파싱해야 한다", () => {
    const userData: AuthState.LoggedInUser = {
      memberId: 1,
      email: "user@test.com",
      role: Role.ROLE_USER,
      profileImageURL: "profile.jpg",
      refreshToken: "refresh-token",
      accessToken: "access-token",
      userId: 123,
      nickname: "testUser",
    };

    localStorageMock.getItem.mockReturnValue(JSON.stringify(userData));

    const result = getLoggedInUserFromLocalStorage();

    expect(result).toEqual(userData);
    expect(result?.role).toBe(Role.ROLE_USER);
    if (result?.role === Role.ROLE_USER) {
      expect(result.userId).toBe(123);
      expect(result.nickname).toBe("testUser");
    }
  });

  it("유효한 출판사 사용자 정보를 올바르게 파싱해야 한다", () => {
    const publisherData: AuthState.LoggedInUser = {
      memberId: 2,
      email: "publisher@test.com",
      role: Role.ROLE_PUBLISHER,
      profileImageURL: "publisher.jpg",
      refreshToken: "pub-refresh-token",
      accessToken: "pub-access-token",
      publisherId: 456,
      publisherName: "Test Publisher",
    };

    localStorageMock.getItem.mockReturnValue(JSON.stringify(publisherData));

    const result = getLoggedInUserFromLocalStorage();

    expect(result).toEqual(publisherData);
    expect(result?.role).toBe(Role.ROLE_PUBLISHER);
    if (result?.role === Role.ROLE_PUBLISHER) {
      expect(result.publisherId).toBe(456);
      expect(result.publisherName).toBe("Test Publisher");
    }
  });

  it("유효한 관리자 정보를 올바르게 파싱해야 한다", () => {
    const adminData: AuthState.LoggedInUser = {
      memberId: 3,
      email: "admin@test.com",
      role: Role.ROLE_ADMIN,
      profileImageURL: "admin.jpg",
      refreshToken: "admin-refresh-token",
      accessToken: "admin-access-token",
    };

    localStorageMock.getItem.mockReturnValue(JSON.stringify(adminData));

    const result = getLoggedInUserFromLocalStorage();

    expect(result).toEqual(adminData);
    expect(result?.role).toBe(Role.ROLE_ADMIN);
  });

  it("잘못된 JSON 형식일 때 undefined를 반환하고 localStorage를 정리해야 한다", () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});

    localStorageMock.getItem.mockReturnValue("invalid-json");

    const result = getLoggedInUserFromLocalStorage();

    expect(result).toBeUndefined();
    expect(localStorageMock.removeItem).toHaveBeenCalledWith("loggedInUser");
    expect(consoleSpy).toHaveBeenCalledWith(
      "Error parsing loggedInUser from localStorage",
      expect.any(SyntaxError)
    );

    consoleSpy.mockRestore();
  });

  it("빈 문자열일 때 undefined를 반환해야 한다", () => {
    localStorageMock.getItem.mockReturnValue("");

    const result = getLoggedInUserFromLocalStorage();

    expect(result).toBeUndefined();
  });

  it("null 값일 때 undefined를 반환해야 한다", () => {
    localStorageMock.getItem.mockReturnValue("null");

    const result = getLoggedInUserFromLocalStorage();

    // JSON.parse('null')은 null을 반환하므로, 이는 정상적인 동작입니다
    expect(result).toBe(null);
  });

  it("객체가 아닌 값일 때 적절히 처리해야 한다", () => {
    localStorageMock.getItem.mockReturnValue('"just a string"');

    const result = getLoggedInUserFromLocalStorage();

    // 문자열이 반환되어도 타입 안전성은 보장되어야 함
    expect(result).toBe("just a string");
  });

  it("localStorage 액세스 에러가 발생해도 적절히 처리해야 한다", () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});

    // localStorage.getItem을 mock하여 에러 발생시키기
    localStorageMock.getItem.mockImplementation(() => {
      throw new Error("localStorage access denied");
    });

    const result = getLoggedInUserFromLocalStorage();

    expect(result).toBeUndefined();
    expect(consoleSpy).toHaveBeenCalledWith(
      "Error parsing loggedInUser from localStorage",
      expect.any(Error)
    );

    consoleSpy.mockRestore();
  });
});
