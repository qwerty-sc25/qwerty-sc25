import { describe, it, expect } from "vitest";
import createReactionMap from "./createReactionMap";
import { HighlightReaction, HighlightReactionType } from "../types/highlight";

// 테스트용 HighlightReaction 생성 함수
const createTestReaction = (
  id: number,
  reactionType: HighlightReactionType,
  authorId: number = 123,
  authorName: string = "testUser"
): HighlightReaction => ({
  id,
  authorId,
  authorName,
  reactionType,
  emoji: "👍",
  commentId: 1,
  createdAt: "2024-01-01T00:00:00Z",
});

describe("createReactionMap", () => {
  it("빈 반응 배열에 대해 모든 반응 타입의 빈 배열을 가진 맵을 생성해야 한다", () => {
    const reactions: HighlightReaction[] = [];
    const result = createReactionMap(reactions);

    expect(result.size).toBe(7);
    expect(result.get("GREAT")).toEqual([]);
    expect(result.get("HEART")).toEqual([]);
    expect(result.get("SMILE")).toEqual([]);
    expect(result.get("CLAP")).toEqual([]);
    expect(result.get("SAD")).toEqual([]);
    expect(result.get("ANGRY")).toEqual([]);
    expect(result.get("SURPRISED")).toEqual([]);
  });

  it("단일 반응에 대해 올바른 카테고리에 배치해야 한다", () => {
    const reactions: HighlightReaction[] = [createTestReaction(1, "HEART")];

    const result = createReactionMap(reactions);

    expect(result.get("HEART")).toHaveLength(1);
    expect(result.get("HEART")?.[0]).toEqual(reactions[0]);
    expect(result.get("GREAT")).toHaveLength(0);
    expect(result.get("SMILE")).toHaveLength(0);
  });

  it("여러 반응을 올바른 카테고리별로 분류해야 한다", () => {
    const reactions: HighlightReaction[] = [
      createTestReaction(1, "HEART", 123, "user1"),
      createTestReaction(2, "HEART", 124, "user2"),
      createTestReaction(3, "CLAP", 125, "user3"),
      createTestReaction(4, "SURPRISED", 126, "user4"),
    ];

    const result = createReactionMap(reactions);

    expect(result.get("HEART")).toHaveLength(2);
    expect(result.get("CLAP")).toHaveLength(1);
    expect(result.get("SURPRISED")).toHaveLength(1);
    expect(result.get("GREAT")).toHaveLength(0);
    expect(result.get("SMILE")).toHaveLength(0);
    expect(result.get("SAD")).toHaveLength(0);
    expect(result.get("ANGRY")).toHaveLength(0);
  });

  it("같은 타입의 반응들이 배열에 순서대로 추가되어야 한다", () => {
    const reactions: HighlightReaction[] = [
      createTestReaction(1, "SMILE", 123, "user1"),
      createTestReaction(2, "SMILE", 124, "user2"),
    ];

    const result = createReactionMap(reactions);
    const smileReactions = result.get("SMILE");

    expect(smileReactions).toHaveLength(2);
    expect(smileReactions?.[0].id).toBe(1);
    expect(smileReactions?.[1].id).toBe(2);
  });

  it("모든 반응 타입에 대해 반응이 있을 때 올바르게 분류해야 한다", () => {
    const reactionTypes: HighlightReactionType[] = [
      "GREAT",
      "HEART",
      "SMILE",
      "CLAP",
      "SAD",
      "ANGRY",
      "SURPRISED",
    ];

    const reactions: HighlightReaction[] = reactionTypes.map((type, index) =>
      createTestReaction(index + 1, type, 100 + index, `user${index}`)
    );

    const result = createReactionMap(reactions);

    reactionTypes.forEach((type) => {
      expect(result.get(type)).toHaveLength(1);
      expect(result.get(type)?.[0].reactionType).toBe(type);
    });
  });

  it("reactionType이 null인 반응에 대해서도 처리해야 한다", () => {
    const reactions: HighlightReaction[] = [
      {
        ...createTestReaction(1, "HEART"),
        reactionType: null as any,
      },
    ];

    // null 값이 있어도 에러가 발생하지 않아야 함
    expect(() => createReactionMap(reactions)).not.toThrow();
  });
});
