package com.t1project.club_card.controllers;

import com.t1project.club_card.dto.ChangeAllUserFieldsDTO;
import com.t1project.club_card.dto.ChangeFieldDTO;
import com.t1project.club_card.dto.ResponseClubMemberDTO;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.services.QRCodeService;
import com.t1project.club_card.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/profile")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> getCurrentClubMember(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
        return clubMemberService.findByEmail(email)
                .map(Utils::mapToResponseDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping("/members")
    public Flux<ResponseClubMemberDTO> getAllMembers(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        if (role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPERUSER")) {
            return clubMemberService.findAllPaged(page, size)
                    .map(Utils::mapToResponseDTO)
                    .onErrorResume(e -> Flux.error(new RuntimeException(e)));
        }
        return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "ROLE_USER unable to do this op"));
    }

    @GetMapping("/members/{id}")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> getById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Integer id) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        if (role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPERUSER")) {
            return clubMemberService.findById(id)
                    .map(Utils::mapToResponseDTO)
                    .map(responseClubMemberDTO -> ResponseEntity.status(HttpStatus.OK).body(responseClubMemberDTO))
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
        }
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "ROLE_USER unable to do this op"));
    }

    @PutMapping("/members/{id}/change-role")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> changeRoleById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Integer id,
            @RequestBody ChangeFieldDTO changeFieldDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        return clubMemberService.findById(id)
                .flatMap(clubMember -> {
                    final String rl = clubMember.getRole();
                    if (!changeFieldDTO.getValue().equals("ROLE_SUPERUSER") &&
                            (rl.equals("ROLE_USER") || (rl.equals("ROLE_ADMIN") && role.equals("ROLE_SUPERUSER")))) {
                        clubMember.setRole(changeFieldDTO.getValue());
                        return clubMemberService.save(clubMember)
                                .map(Utils::mapToResponseDTO)
                                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build()));
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Cannot change role of same or higher access"));
                    }
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "member not found")));
    }

    @PutMapping("/members/{id}/change-privilege")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> changePrivilegeById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Integer id,
            @RequestBody ChangeFieldDTO changeFieldDTO) {
        final String token = Utils.extractBearerToken(authHeader);
        final String role = jwtService.extractRole(token);
        return clubMemberService.findById(id)
                .flatMap(clubMember -> {
                    final String rl = clubMember.getRole();
                    if (rl.equals("ROLE_USER") || (rl.equals("ROLE_ADMIN") && role.equals("ROLE_SUPERUSER"))) {
                        clubMember.setPrivilege(changeFieldDTO.getValue());
                        return clubMemberService.save(clubMember)
                                .map(Utils::mapToResponseDTO)
                                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build()));
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Cannot change role of same or higher access"));
                    }
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "member not found")));
    }

    @PutMapping("/update-fields")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> updateAllFields(
            @RequestBody ChangeAllUserFieldsDTO changeAllUserFieldsDTO,
            @AuthenticationPrincipal ClubMember clubMember) {

        return clubMemberService.changeAllFields(clubMember.getId(), changeAllUserFieldsDTO)
                .map(Utils::mapToResponseDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));

    }

    @GetMapping("profile/qr")
    public Mono<ResponseEntity<byte[]>> getQR(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        final String token = Utils.extractBearerToken(authHeader);
        final String email = jwtService.extractUsername(token);
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
