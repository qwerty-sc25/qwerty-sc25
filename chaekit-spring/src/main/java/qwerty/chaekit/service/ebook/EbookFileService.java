package qwerty.chaekit.service.ebook;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.ebook.Ebook;
import qwerty.chaekit.domain.ebook.repository.EbookRepository;
import qwerty.chaekit.domain.member.publisher.PublisherProfile;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.dto.ebook.upload.EbookDownloadResponse;
import qwerty.chaekit.dto.ebook.upload.EbookPostRequest;
import qwerty.chaekit.dto.ebook.upload.EbookPostResponse;
import qwerty.chaekit.global.enums.ErrorCode;
import qwerty.chaekit.global.exception.ForbiddenException;
import qwerty.chaekit.global.security.resolver.PublisherToken;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.service.util.EntityFinder;
import qwerty.chaekit.service.util.FileService;

@Service
@RequiredArgsConstructor
public class EbookFileService {
    private final EbookRepository ebookRepository;
    private final EbookPolicy ebookPolicy;
    private final FileService fileService;
    private final EntityFinder entityFinder;


    @Transactional
    public EbookPostResponse uploadEbook(PublisherToken publisherToken, EbookPostRequest request) {
        PublisherProfile publisher = entityFinder.findPublisher(publisherToken.publisherId());

        if(!publisher.isApproved()) {
            throw new ForbiddenException(ErrorCode.PUBLISHER_NOT_APPROVED);
        }

        String fileKey = fileService.uploadEbook(request.file());
        String coverImageKey = fileService.uploadEbookCoverImageIfPresent(request.coverImageFile());
        String coverImageURL = fileService.convertToPublicImageURL(coverImageKey);

        Ebook ebook = Ebook.builder()
                .title(request.title())
                .author(request.author())
                .description(request.description())
                .size(request.file().getSize())
                .price(request.price())
                .fileKey(fileKey)
                .coverImageKey(coverImageKey)
                .publisher(publisher)
                .build();
        Ebook saved = ebookRepository.save(ebook);

        return EbookPostResponse.of(saved, coverImageURL);
    }

    @Transactional
    public EbookDownloadResponse getPresignedEbookUrl(UserToken userToken, Long ebookId) {
        UserProfile user = entityFinder.findUser(userToken.userId());
        Ebook ebook = entityFinder.findEbook(ebookId);

        ebookPolicy.assertEBookPurchased(user, ebook);


        String ebookFileKey = ebook.getFileKey();
        String downloadUrl = fileService.getEbookDownloadUrl(ebookFileKey);

        return EbookDownloadResponse.of(downloadUrl);
    }
}

