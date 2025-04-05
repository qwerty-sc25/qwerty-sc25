package qwerty.chaekit.controller;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.PublisherInfoResponse;
import qwerty.chaekit.dto.ebook.EbookListResponse;
import qwerty.chaekit.dto.upload.EbookDownloadResponse;
import qwerty.chaekit.dto.upload.EbookUploadRequest;
import qwerty.chaekit.service.AdminService;
import qwerty.chaekit.service.EbookFileService;
import qwerty.chaekit.service.EbookService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final EbookFileService ebookFileService;
    private final EbookService ebookService;

    @GetMapping("/publishers/pending")
    public List<PublisherInfoResponse> fetchPendingList() {
        return adminService.getNotAcceptedPublishers();
    }

    @PostMapping("/publishers/{id}/accept")
    public Boolean acceptPublisher(@PathVariable Long id) {
        return adminService.acceptPublisher(id);
    }

    @PostMapping("/books/upload")
    public String uploadFile(@ModelAttribute EbookUploadRequest request) throws IOException {
        return ebookFileService.uploadEbookByAdmin(request);
    }

    @GetMapping("/books/{ebookId}")
    public EbookDownloadResponse downloadFile(@PathVariable Long ebookId) {
        return ebookFileService.getPresignedEbookUrl(ebookId);
    }

    @GetMapping("/books")
    public EbookListResponse getBooks(@ParameterObject Pageable pageable) {
        return ebookService.fetchEbookList(pageable);
    }
}
