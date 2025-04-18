package qwerty.chaekit.domain.highlight.entity.reaction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.highlight.entity.Highlight;
import qwerty.chaekit.domain.member.user.UserProfile;

@Entity
@Getter
@Table(name = "highlight_reaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HighlightReaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="author_id")
    private UserProfile author;

    @ManyToOne
    @JoinColumn(name="highlight_id")
    private Highlight highlight;

    @Column(nullable = false)
    private String reactionType;

}