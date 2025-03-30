package qwerty.chaekit.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.PublisherInfoResponse;
import qwerty.chaekit.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/publishers/pending")
    public List<PublisherInfoResponse> fetchPendingList() {
        return adminService.getNotAcceptedPublishers();
    }

    @PostMapping("/publishers/{id}/accept")
    public Boolean acceptPublisher(@PathVariable Long id) {
        return adminService.acceptPublisher(id);
    }
}
