import { describe, it, expect } from "vitest";
import { getTrendText, getRevenueTrendText, formatFileSize } from "./bookUtils";

describe("bookUtils", () => {
  describe("getTrendText", () => {
    it("양수 변화량에 대해 올바른 텍스트와 색상을 반환해야 한다", () => {
      const result = getTrendText(1000);
      expect(result.text).toBe("전월 대비 +1,000");
      expect(result.color).toBe("#4caf50");
    });

    it("음수 변화량에 대해 올바른 텍스트와 색상을 반환해야 한다", () => {
      const result = getTrendText(-500);
      expect(result.text).toBe("전월 대비 -500");
      expect(result.color).toBe("#f44336");
    });

    it("변화량이 0일 때 올바른 텍스트와 색상을 반환해야 한다", () => {
      const result = getTrendText(0);
      expect(result.text).toBe("변동 없음");
      expect(result.color).toBe("#757575");
    });

    it("큰 수에 대해 천 단위 구분자가 올바르게 적용되어야 한다", () => {
      const result = getTrendText(1234567);
      expect(result.text).toBe("전월 대비 +1,234,567");
      expect(result.color).toBe("#4caf50");
    });

    it("소수점이 있는 수에 대해서도 작동해야 한다", () => {
      const result = getTrendText(123.45);
      expect(result.text).toBe("전월 대비 +123.45");
      expect(result.color).toBe("#4caf50");
    });
  });

  describe("getRevenueTrendText", () => {
    it("양수 매출 변화량에 대해 올바른 원화 표시를 반환해야 한다", () => {
      const result = getRevenueTrendText(50000);
      expect(result.text).toBe("전월 대비 +₩50,000");
      expect(result.color).toBe("#4caf50");
    });

    it("음수 매출 변화량에 대해 절댓값과 함께 올바른 표시를 반환해야 한다", () => {
      const result = getRevenueTrendText(-30000);
      expect(result.text).toBe("전월 대비 -₩30,000");
      expect(result.color).toBe("#f44336");
    });

    it("매출 변화가 0일 때 올바른 텍스트를 반환해야 한다", () => {
      const result = getRevenueTrendText(0);
      expect(result.text).toBe("변동 없음");
      expect(result.color).toBe("#757575");
    });

    it("큰 매출 변화량에 대해 천 단위 구분자가 적용되어야 한다", () => {
      const result = getRevenueTrendText(1500000);
      expect(result.text).toBe("전월 대비 +₩1,500,000");
      expect(result.color).toBe("#4caf50");
    });
  });

  describe("formatFileSize", () => {
    it("0 바이트를 올바르게 포맷해야 한다", () => {
      expect(formatFileSize(0)).toBe("0 Bytes");
    });

    it("바이트 단위를 올바르게 포맷해야 한다", () => {
      expect(formatFileSize(512)).toBe("512 Bytes");
      expect(formatFileSize(1023)).toBe("1023 Bytes");
    });

    it("킬로바이트 단위를 올바르게 포맷해야 한다", () => {
      expect(formatFileSize(1024)).toBe("1 KB");
      expect(formatFileSize(1536)).toBe("1.5 KB");
      expect(formatFileSize(2048)).toBe("2 KB");
    });

    it("메가바이트 단위를 올바르게 포맷해야 한다", () => {
      expect(formatFileSize(1024 * 1024)).toBe("1 MB");
      expect(formatFileSize(1024 * 1024 * 2.5)).toBe("2.5 MB");
    });

    it("기가바이트 단위를 올바르게 포맷해야 한다", () => {
      expect(formatFileSize(1024 * 1024 * 1024)).toBe("1 GB");
      expect(formatFileSize(1024 * 1024 * 1024 * 1.75)).toBe("1.75 GB");
    });

    it("소수점 둘째 자리까지만 표시해야 한다", () => {
      expect(formatFileSize(1024 * 1.234567)).toBe("1.23 KB");
    });

    it("음수 입력에 대해서도 처리해야 한다", () => {
      expect(formatFileSize(-1024)).toBe("-1 KB");
    });
  });
});
