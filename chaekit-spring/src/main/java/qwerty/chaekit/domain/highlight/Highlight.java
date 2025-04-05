package qwerty.chaekit.domain.highlight;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.ebook.Ebook;
import qwerty.chaekit.domain.member.user.UserProfile;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Highlight extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserProfile author;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Ebook book;

    @Column(nullable = false)
    private String cfi;

    @Column(nullable = false)
    private String spine;

    private String memo;


    // TODO: Activity(ManyToOne)

    @Builder
    public Highlight(UserProfile author, Ebook book, String cfi, String spine, String memo) {
        this.author = author;
        this.book = book;
        this.cfi = cfi;
        this.spine = spine;
        this.memo = memo;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
