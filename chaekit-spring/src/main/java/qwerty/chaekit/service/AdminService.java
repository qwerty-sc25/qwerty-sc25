package qwerty.chaekit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.Member.publisher.PublisherProfile;
import qwerty.chaekit.domain.Member.publisher.PublisherProfileRepository;
import qwerty.chaekit.dto.PublisherInfoResponse;
import qwerty.chaekit.global.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final PublisherProfileRepository publisherProfileRepository;

    @Transactional(readOnly = true)
    public List<PublisherInfoResponse> getNotAcceptedPublishers() {
        return publisherProfileRepository.findAllByAcceptedFalseOrderByCreatedAtDesc().stream()
                .map(PublisherInfoResponse::of)
                .toList();
    }

    @Transactional
    public boolean acceptPublisher(Long publisherId) {
        Optional<PublisherProfile> profile = publisherProfileRepository.findByMember_Id(publisherId);
        if (profile.isPresent()) {
            if(profile.get().isAccepted()) {
                return false;
            }
            profile.get().acceptPublisher();
            return true;
        } else {
            throw new NotFoundException("PUBLISHER_NOT_FOUND", "해당 출판사가 존재하지 않습니다");
        }
    }
}
