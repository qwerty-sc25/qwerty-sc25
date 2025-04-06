package qwerty.chaekit.domain.Member.ebook;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.Member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ebook extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column
    private String description;

    private long size;

    @Column(nullable = false)
    private String fileKey;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Member publisher;

    @Builder
    public Ebook(String title, String author, String description, long size, String fileKey, Member publisher) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.size = size;
        this.fileKey = fileKey;
        this.publisher = publisher;
    }
}
