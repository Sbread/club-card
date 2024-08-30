package com.t1project.club_card.controllers;

import com.t1project.club_card.dto.*;
import com.t1project.club_card.exceptions.InvalidFieldException;
import com.t1project.club_card.models.TemplatePrivilege;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.services.QRCodeService;
import com.t1project.club_card.services.TemplatePrivilegeService;
import com.t1project.club_card.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@CrossOrigin
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private TemplatePrivilegeService templatePrivilegeService;

    @GetMapping("/profile")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> getCurrentClubMember(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
        return clubMemberService.findByEmail(email)
                .map(Utils::mapToResponseDTO)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @PutMapping("/profile/card/select")
    public Mono<ResponseEntity<ProfileTemplateDTO>> selectTemplate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody ProfileTemplateDTO profileTemplateDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        return clubMemberService.findByEmail(email)
                .flatMap(clubMember -> {
                    clubMember.setTemplate(profileTemplateDTO.getTemplate());
                    return clubMemberService.save(clubMember);
                })
                .map(member -> ResponseEntity.ok().body(ProfileTemplateDTO.builder()
                        .template(member.getTemplate()).build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @GetMapping("/profile/card")
    public Mono<ResponseEntity<ProfileTemplateDTO>> getCard(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        return clubMemberService.findByEmail(email)
                .map(ClubMember::getTemplate)
                .map(template -> ResponseEntity.ok().body(ProfileTemplateDTO.builder().template(template).build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @GetMapping("/profile/lock")
    public Mono<ResponseEntity<SelfLockResponseDTO>> selfLock(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        return clubMemberService.findByEmail(email)
                .map(clubMember -> {
                    if (!clubMember.getRole().equals("ROLE_SUPERUSER")) {
                        clubMember.setLocked(true);
                        return SelfLockResponseDTO.builder().locked(clubMember.isLocked()).build();
                    }
                    throw new AccessDeniedException("Superuser cannot lock himself");
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping("/members")
    public Mono<ResponseEntity<MembersPageDTO>> getAllMembers(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        final String email = jwtService.extractUsername(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (role.equals("ROLE_USER")) {
            return Mono.error(new AccessDeniedException("User cannot do this"));
        }
        Mono<Long> countAll = clubMemberService.countAll();
        Flux<ClubMember> membersOnPage = clubMemberService.findAllPaged(page, size, email);
        return membersOnPage.collectList().zipWith(countAll)
                .map(tuple -> Utils.mapToPageResponseDTO(tuple.getT1(), tuple.getT2()))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @GetMapping("/members/{id}")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> getById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Integer id) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (role.equals("ROLE_USER")) {
            return Mono.error(new AccessDeniedException("User cannot do this"));
        }
        return clubMemberService.findById(id)
                .map(Utils::mapToResponseDTO)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @PutMapping("/members/{id}/change-role")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> changeRoleById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Integer id,
            @RequestBody ChangeFieldDTO changeFieldDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (role.equals("ROLE_USER")) {
            return Mono.error(new AccessDeniedException("User cannot do this"));
        }
        return clubMemberService.findById(id)
                .flatMap(clubMember -> {
                    final String rl = clubMember.getRole();
                    if (!changeFieldDTO.getValue().equals("ROLE_SUPERUSER")
                            && (rl.equals("ROLE_USER") || (rl.equals("ROLE_ADMIN") && role.equals("ROLE_SUPERUSER")))) {
                        clubMember.setRole(changeFieldDTO.getValue());
                        return clubMemberService.save(clubMember)
                                .map(Utils::mapToResponseDTO)
                                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build()));
                    } else {
                        return Mono.error(new AccessDeniedException("Cannot change role of equals and higher access"));
                    }
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("member not found")))
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @PutMapping("/members/{id}/change-privilege")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> changePrivilegeById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Integer id,
            @RequestBody ChangeFieldDTO changeFieldDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (role.equals("ROLE_USER")) {
            return Mono.error(new AccessDeniedException("User cannot do this"));
        }
        System.out.println(changeFieldDTO.getValue());
        return clubMemberService.findById(id)
                .flatMap(clubMember -> {
                    final String rl = clubMember.getRole();
                    if (!rl.equals("ROLE_SUPERUSER")) {
                        clubMember.setPrivilege(changeFieldDTO.getValue());
                        return clubMemberService.save(clubMember)
                                .map(Utils::mapToResponseDTO)
                                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build()));
                    } else {
                        return Mono.error(new AccessDeniedException("Cannot change privilege of same or higher access"));
                    }
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("member not found")));
    }

    @PutMapping("/members/{id}/change-locked")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> changeLockedById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Integer id,
            @RequestBody LockedRequestDTO lockedRequestDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (role.equals("ROLE_USER")) {
            return Mono.error(new AccessDeniedException("User cannot do this"));
        }
        return clubMemberService.findById(id)
                .flatMap(clubMember -> {
                    final String rl = clubMember.getRole();
                    if (rl.equals("ROLE_USER") || (rl.equals("ROLE_ADMIN") && role.equals("ROLE_SUPERUSER"))) {
                        clubMember.setLocked(lockedRequestDTO.isLocked());
                        return clubMemberService.save(clubMember)
                                .map(Utils::mapToResponseDTO)
                                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build()));
                    } else {
                        return Mono.error(new AccessDeniedException("Cannot lock/unlock same or higher access"));
                    }
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("member not found")));
    }

    @GetMapping("/template-privilege/all")
    public Mono<ResponseEntity<TemplatePrivilegeDTO>> allTemplatePrivilege(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (role.equals("ROLE_USER")) {
            return Mono.error(new AccessDeniedException("User cannot do this"));
        }
        return templatePrivilegeService.createTemplatePrivilegeMap()
                .map(templatePrivilegeMap ->
                        ResponseEntity.ok().body(TemplatePrivilegeDTO.builder()
                                .templatePrivilegesMap(templatePrivilegeMap).build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @PutMapping("/template-privilege/select")
    public Mono<ResponseEntity<TemplatePrivilegeDTO>> selectTemplatePrivilege(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody TemplatePrivilegeDTO templatePrivilegeDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (role.equals("ROLE_USER")) {
            return Mono.error(new AccessDeniedException("User cannot do this"));
        }
        if (templatePrivilegeDTO.getTemplatePrivilegesMap().values().stream().anyMatch(Set::isEmpty)) {
            throw new InvalidFieldException("Zero templates for privilege cannot be chosen");
        }
        return templatePrivilegeService.findAll()
                .flatMap(templatePrivilege -> {
                    String template = templatePrivilege.getTemplate();
                    templatePrivilege.setPrivileges(templatePrivilegeDTO.getTemplatePrivilegesMap().get(template));
                    return templatePrivilegeService.save(templatePrivilege);
                })
                .collectList()
                .map(updatedPrivileges -> {
                            TemplatePrivilegeDTO updatedDTO = TemplatePrivilegeDTO.builder()
                                    .templatePrivilegesMap(updatedPrivileges.stream().collect(Collectors.toMap(
                                            TemplatePrivilege::getTemplate,
                                            TemplatePrivilege::getPrivileges
                                    ))).build();
                            return ResponseEntity.ok().body(updatedDTO);
                        }
                )
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }


    @GetMapping("/profile/privilege-templates")
    public Mono<ResponseEntity<PrivilegeTemplatesDTO>> getTemplatesToPrivilege(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String privilege = jwtService.extractPrivilege(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        return templatePrivilegeService.getTemplatesByPrivilege(privilege)
                .map(set -> ResponseEntity.ok().body(PrivilegeTemplatesDTO.builder().templates(set).build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @PutMapping("/profile/update-fields")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> updateAllFields(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody ChangeAllUserFieldsDTO changeAllUserFieldsDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        if (!Utils.validateEmail(changeAllUserFieldsDTO.getEmail())) {
            return Mono.error(new InvalidFieldException("Invalid email"));
        }
        if (!Utils.validatePhone(changeAllUserFieldsDTO.getPhone())) {
            return Mono.error(new InvalidFieldException("Invalid phone"));
        }
        return clubMemberService.changeAllFields(changeAllUserFieldsDTO)
                .map(Utils::mapToResponseDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));

    }

    @GetMapping("profile/qr")
    public Mono<ResponseEntity<byte[]>> getQR(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
        final boolean locked = jwtService.extractLocked(token);
        if (locked) {
            return Mono.error(new AccessDeniedException("Account is locked"));
        }
        return clubMemberService.findByEmail(email).flatMap(
                member -> qrCodeService.generateQRCodeForClubMember(member, 200, 200)
                        .map(qrCode -> {
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.IMAGE_PNG);
                            headers.setContentLength(qrCode.length);
                            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
                        }).onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(("QR generate failed\n" + e.getMessage()).getBytes()))));
    }
}
