package com.t1project.club_card.controllers;


import com.t1project.club_card.dto.ChangeUsernameFieldDTO;
import com.t1project.club_card.dto.ChangeUsernameIsLockedDTO;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.services.ClubAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
@RequestMapping(value = "/club_superuser")
public class ClubSuperUserController {

    @Autowired
    private ClubAdminService clubAdminService;

    @GetMapping("/get-all-club-members")
    public Flux<ClubMember> getAllClubMembers() {
        return clubAdminService.getAllClubMembers();
    }

    @PostMapping("/change-username-role")
    public Mono<ResponseEntity<String>> changeUsernameRole(@RequestBody ChangeUsernameFieldDTO changeUsernameFieldDTO) {
        return clubAdminService
                .changeUsernameRole(changeUsernameFieldDTO.getUsername(), changeUsernameFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK)
                        .body("Role of " + changeUsernameFieldDTO.getUsername()
                                + " now " + changeUsernameFieldDTO.getNewFieldValue()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Changing role failed\n" + e.getMessage())));
    }

    @PostMapping("/change-username-privilege")
    public Mono<ResponseEntity<String>> changeUsernamePrivilege(@RequestBody ChangeUsernameFieldDTO changeUsernameFieldDTO) {
        return clubAdminService
                .changeUsernamePrivilege(changeUsernameFieldDTO.getUsername(), changeUsernameFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK)
                        .body("Privilege of " + changeUsernameFieldDTO.getUsername()
                                + " now " + changeUsernameFieldDTO.getNewFieldValue()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Changing privilege failed\n" + e.getMessage())));
    }

    @PostMapping("/change-username-is-locked")
    public Mono<ResponseEntity<String>> changeUsernameIsLocked(
            @RequestBody ChangeUsernameIsLockedDTO changeUsernameIsLockedDTO) {
        return clubAdminService
                .changeUsernameIsLocked(
                        changeUsernameIsLockedDTO.getUsername(), changeUsernameIsLockedDTO.isNewIsLocked())
                .map(saved -> ResponseEntity.status(HttpStatus.OK)
                        .body("IsLocked of " + changeUsernameIsLockedDTO.getUsername()
                                + " now " + changeUsernameIsLockedDTO.isNewIsLocked()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Changing isLocked failed\n" + e.getMessage())));
    }
}
