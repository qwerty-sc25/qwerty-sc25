package qwerty.chaekit.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.group.*;
import qwerty.chaekit.global.security.resolver.Login;
import qwerty.chaekit.global.security.resolver.LoginMember;
import qwerty.chaekit.service.GroupService;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public GroupPostResponse createGroup(@Login LoginMember loginMember,
                                         @RequestBody GroupPostRequest groupPostRequest) {
        return groupService.createGroup(loginMember, groupPostRequest);
    }

    @GetMapping
    public GroupListResponse getAllGroups(@ParameterObject Pageable pageable) {
        return groupService.fetchGroupList(pageable);
    }

    @GetMapping("/{groupId}/info")
    public GroupFetchResponse getGroup(@PathVariable long groupId) {
        return groupService.fetchGroup(groupId);
    }

    @PutMapping("/{groupId}")
    public GroupPostResponse updateGroup(@Login LoginMember loginMember,
                                         @PathVariable long groupId,
                                         @RequestBody GroupPutRequest request) {
        return groupService.updateGroup(loginMember, groupId, request);
    }


}
