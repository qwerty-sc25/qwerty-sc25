package qwerty.chaekit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qwerty.chaekit.domain.member.publisher.PublisherProfile;
import qwerty.chaekit.domain.member.publisher.PublisherProfileRepository;
import qwerty.chaekit.global.exception.NotFoundException;
import qwerty.chaekit.service.member.admin.AdminService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private PublisherProfileRepository publisherProfileRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    @DisplayName("출판사 승인 - 성공")
    void test1() {
        // given
        Long publisherId = 1L;
        PublisherProfile publisher = mock(PublisherProfile.class);
        given(publisher.isAccepted()).willReturn(false);
        given(publisherProfileRepository.findById(publisherId))
                .willReturn(Optional.of(publisher));
        // when
        boolean result = adminService.acceptPublisher(publisherId);
        // then
        assertTrue(result);
        verify(publisher).acceptPublisher();
    }

    @Test
    @DisplayName("출판사 승인 - 실패")
    void test2() {
        // given
        Long publisherId = 2L;
        PublisherProfile publisher = mock(PublisherProfile.class);
        given(publisher.isAccepted()).willReturn(true);
        given(publisherProfileRepository.findById(publisherId))
                .willReturn(Optional.of(publisher));
        // when
        boolean result = adminService.acceptPublisher(publisherId);
        // then
        assertFalse(result);
        verify(publisher, never()).acceptPublisher();
    }

    @Test
    @DisplayName("출판사 승인 - 실패")
    void test3() {
        // given
        Long publisherId = 3L;
        given(publisherProfileRepository.findById(publisherId))
                .willReturn(Optional.empty());
        // when
        NotFoundException e = assertThrows(NotFoundException.class, () ->
                adminService.acceptPublisher(publisherId)
        );
        // then
        assertEquals("PUBLISHER_NOT_FOUND", e.getErrorCode());
    }
}
