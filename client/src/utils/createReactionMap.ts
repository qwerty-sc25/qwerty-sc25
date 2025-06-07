import { HighlightReaction, HighlightReactionType } from "../types/highlight";

export default function createReactionMap(
  reactions: HighlightReaction[]
): Map<HighlightReactionType, HighlightReaction[]> {
  const map: Map<HighlightReactionType, HighlightReaction[]> = new Map([
    ["GREAT", []],
    ["HEART", []],
    ["SMILE", []],
    ["CLAP", []],
    ["SAD", []],
    ["ANGRY", []],
    ["SURPRISED", []],
  ]);
  reactions.forEach((reaction) => {
    if (reaction.reactionType && map.has(reaction.reactionType)) {
      map.get(reaction.reactionType)!.push(reaction);
    }
  });
  return map;
}
