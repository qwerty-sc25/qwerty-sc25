package qwerty.chaekit.domain.ebook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import qwerty.chaekit.domain.ebook.Ebook;
import qwerty.chaekit.domain.member.publisher.PublisherProfile;

import java.util.Optional;

@Repository
public interface EbookRepository {
    Page<Ebook> findAllByTitleAndAuthor(String title, String author, Pageable pageable);
    Optional<Ebook> findById(Long id);
    Optional<Ebook> findByIdWithPublisher(Long id);
    Ebook save(Ebook ebook);
    boolean existsByTitle(String title);

    Page<Ebook> findAllByPublisher(PublisherProfile publisher, Pageable pageable);
    void incrementViewCount(Long ebookId);
    long count();
}
