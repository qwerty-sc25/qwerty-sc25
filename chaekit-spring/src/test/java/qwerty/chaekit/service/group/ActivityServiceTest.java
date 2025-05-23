package qwerty.chaekit.service.group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import qwerty.chaekit.domain.ebook.Ebook;
import qwerty.chaekit.domain.group.ReadingGroup;
import qwerty.chaekit.domain.group.activity.Activity;
import qwerty.chaekit.domain.group.activity.repository.ActivityRepository;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.dto.group.activity.ActivityFetchResponse;
import qwerty.chaekit.dto.group.activity.ActivityPatchRequest;
import qwerty.chaekit.dto.group.activity.ActivityPostRequest;
import qwerty.chaekit.dto.group.activity.ActivityPostResponse;
import qwerty.chaekit.dto.page.PageResponse;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.service.ebook.EbookPolicy;
import qwerty.chaekit.service.util.EntityFinder;
import qwerty.chaekit.service.util.FileService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {
    @InjectMocks
    private ActivityService activityService;

    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private ActivityPolicy activityPolicy;
    @Mock
    private EbookPolicy ebookPolicy;
    @Mock
    private FileService fileService;
    @Mock
    private EntityFinder entityFinder;

    @Test
    void createActivity() {
        // given
        long userId = 1L;
        long groupId = 1L;
        long bookId = 1L;
        long createdActivityId = 1L;
        LocalDate startTime = LocalDate.parse("2025-04-01");
        LocalDate endTime = LocalDate.parse("2026-04-14");
        ActivityPostRequest postRequest = ActivityPostRequest.builder()
                .bookId(bookId)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        UserToken userToken = UserToken.builder()
                .userId(userId)
                .build();

        UserProfile leader = UserProfile.builder()
                .id(userId)
                .build();
        ReadingGroup readingGroup = ReadingGroup.builder()
                .id(groupId)
                .groupLeader(leader)
                .build();
        Ebook ebook = Ebook.builder()
                .id(bookId)
                .build();

        Activity createdActivity = Activity.builder()
                .id(createdActivityId)
                .group(readingGroup)
                .startTime(startTime)
                .endTime(endTime)
                .book(ebook)
                .build();

        given(entityFinder.findUser(userId))
                .willReturn(leader);
        given(entityFinder.findGroup(groupId))
                .willReturn(readingGroup);
        given(entityFinder.findEbook(bookId))
                .willReturn(ebook);
        given(activityRepository.save(any(Activity.class)))
                .willReturn(createdActivity);

        // when
        ActivityPostResponse result = activityService.createActivity(userToken, groupId, postRequest);

        // then
        assertNotNull(result);
        assertEquals(createdActivityId, result.activityId());
        assertEquals(bookId, result.bookId());
        assertEquals(startTime, result.startTime());
        assertEquals(endTime, result.endTime());
    }

    @Test
    void updateActivity() {
        // given
        long userId = 1L;
        long groupId = 1L;
        long bookId = 1L;
        long activityId = 1L;
        LocalDate startTime = LocalDate.parse("2025-04-01");
        LocalDate endTime = LocalDate.parse("2026-04-14");
        String oldDescription = "Old description";
        LocalDate newStartTime = LocalDate.parse("2025-05-01");
        LocalDate newEndTime = LocalDate.parse("2025-06-01");
        String newDescription = "Updated description";

        ActivityPatchRequest patchRequest = ActivityPatchRequest.builder()
                .activityId(activityId)
                .startTime(newStartTime)
                .endTime(newEndTime)
                .description(newDescription)
                .build();

        UserToken userToken = UserToken.builder()
                .userId(userId)
                .build();
        UserProfile leader = UserProfile.builder()
                .id(userId)
                .build();
        ReadingGroup readingGroup = ReadingGroup.builder()
                .id(groupId)
                .groupLeader(leader)
                .build();
        Ebook ebook = Ebook.builder()
                .id(bookId)
                .build();

        Activity oldActivity = Activity.builder()
                .id(activityId)
                .group(readingGroup)
                .startTime(startTime)
                .endTime(endTime)
                .book(ebook)
                .description(oldDescription)
                .build();

        given(entityFinder.findUser(userId))
                .willReturn(leader);
        given(entityFinder.findGroup(groupId))
                .willReturn(readingGroup);
        given(entityFinder.findActivity(activityId))
                .willReturn(oldActivity);
        
        // when
        ActivityPostResponse result = activityService.updateActivity(userToken, groupId, patchRequest);

        // then
        assertNotNull(result);
        assertEquals(activityId, result.activityId());
        assertEquals(newStartTime, result.startTime());
        assertEquals(newEndTime, result.endTime());
        assertEquals(newDescription, result.description());
    }

    @Test
    void fetchAllActivities() {
        // given
        long userId = 1L;
        long groupId = 5L;
        
        UserToken userLogin = UserToken.builder()
                .userId(userId)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        ReadingGroup readingGroup = ReadingGroup.builder()
                .id(groupId)
                .build();
        Ebook ebook = Ebook.builder()
                .id(2L)
                .build();

        Activity activity1 = new Activity(1L, readingGroup, ebook, LocalDate.parse("2025-04-01"), LocalDate.parse("2026-04-14"), "Activity 1");
        Activity activity2 = new Activity(2L, readingGroup, ebook, LocalDate.parse("2025-04-15"), LocalDate.parse("2026-04-28"), "Activity 2");

        // Activity 객체를 List로 설정
        List<Activity> activityList = List.of(activity1, activity2);
        Page<Activity> page = new PageImpl<>(activityList);

        given(activityRepository.findByGroup_IdWithBook(groupId, pageable)).willReturn(page);

        // when
        PageResponse<ActivityFetchResponse> result = activityService.fetchAllActivities(userLogin, pageable, groupId);

        // then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals("Activity 1", result.content().get(0).description());
        assertEquals("Activity 2", result.content().get(1).description());
    }
}
