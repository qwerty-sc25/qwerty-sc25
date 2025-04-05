package qwerty.chaekit.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import qwerty.chaekit.domain.ebook.Ebook;
import qwerty.chaekit.domain.ebook.EbookRepository;
import qwerty.chaekit.domain.member.publisher.PublisherProfile;
import qwerty.chaekit.dto.upload.EbookDownloadResponse;
import qwerty.chaekit.dto.upload.EbookUploadRequest;
import qwerty.chaekit.global.exception.BadRequestException;
import qwerty.chaekit.global.exception.NotFoundException;
import qwerty.chaekit.global.properties.AwsProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class EbookFileService {
    private final EntityManager em;
    private final EbookRepository ebookRepository;
    private final AdminService adminService;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final String bucketName;
    private final Long ebookMaxFileSize;
    private final Long presignedUrlExpirationTime;

    public EbookFileService(EntityManager em, EbookRepository ebookRepository, AdminService adminService, S3Client s3Client, S3Presigner s3Presigner, AwsProperties awsProperties) {
        this.em = em;
        this.ebookRepository = ebookRepository;
        this.adminService = adminService;

        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;

        this.bucketName = awsProperties.ebookBucketName();
        this.ebookMaxFileSize = awsProperties.ebookMaxFileSize();
        this.presignedUrlExpirationTime = awsProperties.presignedUrlExpirationTime();
    }

    @Transactional
    public String uploadEbookByAdmin(EbookUploadRequest request) throws IOException {
        String title = request.title();
        String author = request.author();
        String description = request.description();
        MultipartFile file = request.file();

        if (file == null || file.getOriginalFilename() == null) {
            throw new BadRequestException("EBOOK_FILE_MISSING", "파일이 누락되었습니다.");
        }
        String fileName = file.getOriginalFilename();
        String fileKey = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));


        if (file.getSize() > ebookMaxFileSize) {
            throw new BadRequestException("EBOOK_FILE_SIZE_EXCEEDED",
                    "업로드 용량이 " + (ebookMaxFileSize / 1024 / 1024) + "MB를 초과했습니다.");
        }

        if (!isValidEpub(file)) {
            throw new BadRequestException("INVALID_EBOOK_FILE", "epub 파일이 아닙니다.");
        }

        // 업로드
        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileKey)
                            .build(),
                    RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        Ebook ebook = Ebook.builder()
                .title(title)
                .author(author)
                .description(description)
                .size(file.getSize())
                .fileKey(fileKey)
                .publisher(em.getReference(PublisherProfile.class, adminService.getAdminPublisherId()))
                .build();
        ebookRepository.save(ebook);

        return "File uploaded successfully: " + fileKey;
    }

    private boolean isValidEpub(MultipartFile file) throws IOException {
        // epub 파일 검증 코드 by ChatGPT 4o mini
        // 파일 확장자 검사 (이미 .epub 파일로 확정)
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".epub")) {
            return false;
        }

        // MIME 타입 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/epub+zip")) {
            return false;
        }

        // 실제 파일 내용 검사: EPUB 파일 내부에 mimetype 파일이 있는지 검사
        try (InputStream inputStream = file.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // mimetype 파일이 있는지 확인
                if ("mimetype".equals(entry.getName())) {
                    // "mimetype" 파일이 존재하고, 내용이 "application/epub+zip"인지 확인
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    String mimetype = reader.readLine();
                    if ("application/epub+zip".equals(mimetype)) {
                        return true; // 유효한 EPUB 파일
                    }
                }
            }
        }

        return false; // "mimetype"이 없거나, 내용이 맞지 않으면 EPUB 파일이 아님
    }

    @Transactional
    public EbookDownloadResponse getPresignedEbookUrl(Long ebookId) {
        Ebook ebook = ebookRepository.findById(ebookId)
                .orElseThrow(() -> new NotFoundException("EBOOK_NOT_FOUND", "해당 전자책이 존재하지 않습니다"));

        String fileKey = ebook.getFileKey();

        return EbookDownloadResponse.of(generatePresignedUrlFromS3(fileKey));
    }

    private String generatePresignedUrlFromS3(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(builder -> builder
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofSeconds(presignedUrlExpirationTime))
        );

        return presignedRequest.url().toString();
    }
}

