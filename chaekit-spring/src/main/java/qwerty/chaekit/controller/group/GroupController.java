package qwerty.chaekit.controller.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.group.request.GroupPatchRequest;
import qwerty.chaekit.dto.group.request.GroupPostRequest;
import qwerty.chaekit.dto.group.response.GroupFetchResponse;
import qwerty.chaekit.dto.group.response.GroupJoinResponse;
import qwerty.chaekit.dto.group.response.GroupMemberResponse;
import qwerty.chaekit.dto.group.response.GroupPostResponse;
import qwerty.chaekit.dto.page.PageResponse;
import qwerty.chaekit.global.response.ApiSuccessResponse;
import qwerty.chaekit.global.security.resolver.Login;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.service.group.GroupMemberService;
import qwerty.chaekit.service.group.GroupService;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiSuccessResponse<GroupPostResponse> createGroup(
            @Parameter(hidden = true) @Login UserToken userToken,
            @ModelAttribute @Valid GroupPostRequest groupPostRequest
    ) {
        return ApiSuccessResponse.of(groupService.createGroup(userToken, groupPostRequest));
    }

    @GetMapping
    public ApiSuccessResponse<PageResponse<GroupFetchResponse>> getAllGroups(
            @Parameter(hidden = true) @Login(required = false) UserToken userToken,
            @ParameterObject Pageable pageable) {
        return ApiSuccessResponse.of(groupService.getAllGroups(userToken, pageable));
    }

    @GetMapping("/my/joined")
    @Operation(
            summary = "내가 가입한 그룹 목록 조회",
            description = "내가 가입한 그룹 목록을 조회합니다."
    )
    public ApiSuccessResponse<PageResponse<GroupFetchResponse>> getJoinedGroups(
            @Parameter(hidden = true) @Login UserToken userToken,
            @ParameterObject Pageable pageable) {
        return ApiSuccessResponse.of(groupService.getJoinedGroups(userToken, pageable));
    }

    @Operation(
            summary = "내가 생성한 그룹 목록 조회",
            description = "내가 생성한 그룹 목록을 조회합니다."
    )
    @GetMapping("/my/created")
    public ApiSuccessResponse<PageResponse<GroupFetchResponse>> getCreatedGroups(
            @Parameter(hidden = true) @Login UserToken userToken,
            @ParameterObject Pageable pageable) {
        return ApiSuccessResponse.of(groupService.getCreatedGroups(userToken, pageable));
    }

    @GetMapping("/{groupId}/info")
    public ApiSuccessResponse<GroupFetchResponse> getGroup(
            @Parameter(hidden = true) @Login(required = false) UserToken userToken,
            @PathVariable long groupId) {
        return ApiSuccessResponse.of(groupService.fetchGroup(userToken, groupId));
    }

    @PatchMapping(value = "/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiSuccessResponse<GroupPostResponse> updateGroup(
            @Parameter(hidden = true) @Login UserToken userToken,
            @PathVariable long groupId,
            @ModelAttribute @Valid GroupPatchRequest request) {
        return ApiSuccessResponse.of(groupService.updateGroup(userToken, groupId, request));
    }

    @PostMapping("/{groupId}/join")
    public ApiSuccessResponse<GroupJoinResponse> requestJoinGroup(
            @Parameter(hidden = true) @Login UserToken userToken,
            @PathVariable long groupId) {
        return ApiSuccessResponse.of(groupMemberService.requestGroupJoin(userToken, groupId));
    }

    @Operation(
            summary = "특정 그룹의 멤버 목록 조회",
            description = "특정 그룹에서 가입된 + 대기중인 멤버 목록을 조회합니다."
    )
    @GetMapping("/{groupId}/members")
    public ApiSuccessResponse<PageResponse<GroupMemberResponse>> getGroupMembers(
            @PathVariable long groupId,
            @ParameterObject Pageable pageable
    ) {
        return ApiSuccessResponse.of(groupMemberService.getGroupMembers(groupId, pageable));
    }
    
    @PatchMapping("/{groupId}/members/{userId}/approve")
    public ApiSuccessResponse<GroupJoinResponse> approveJoinRequest(
            @Parameter(hidden = true) @Login UserToken userToken,
            @PathVariable long groupId,
            @PathVariable long userId) {
        return ApiSuccessResponse.of(groupMemberService.approveJoinRequest(userToken, groupId, userId));
    }

    @PatchMapping("/{groupId}/members/{userId}/reject")
    public ApiSuccessResponse<Void> rejectJoinRequest(
            @Parameter(hidden = true) @Login UserToken userToken,
            @PathVariable long groupId,
            @PathVariable long userId) {
        groupMemberService.rejectJoinRequest(userToken, groupId, userId);
        return ApiSuccessResponse.emptyResponse();
    }

    @DeleteMapping("/{groupId}/members/leave")
    public ApiSuccessResponse<Void> leaveGroup(
            @Parameter(hidden = true) @Login UserToken userToken,
            @PathVariable long groupId) {
        groupMemberService.leaveGroup(userToken, groupId);
        return ApiSuccessResponse.emptyResponse();
    }

    @GetMapping("/{groupId}/members/pending")
    public ApiSuccessResponse<PageResponse<GroupMemberResponse>> getPendingList(
            @Parameter(hidden = true) @Login UserToken userToken,
            @ParameterObject Pageable pageable,
            @PathVariable long groupId
    ) {
        return ApiSuccessResponse.of(groupMemberService.fetchPendingList(pageable, userToken, groupId));
    }
    
    @PostMapping("/{groupId}/members/{userId}/kick")
    public ApiSuccessResponse<Void> kickGroupMember(
            @Parameter(hidden = true) @Login UserToken userToken,
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        groupMemberService.kickGroupMember(userToken, groupId, userId);
        return ApiSuccessResponse.emptyResponse();
    }
}
