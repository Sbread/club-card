package com.t1project.club_card.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.t1project.club_card.dto.ChangeFieldDTO;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.services.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/profile")
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("")
    public ClubMember getCurrentClubMember(@AuthenticationPrincipal ClubMember clubMember) {
        return clubMember;
    }

    @GetMapping("/qr")
    public Mono<ResponseEntity<byte[]>> getQR(@AuthenticationPrincipal ClubMember clubMember) {
        return qrCodeService.generateQRCodeForClubMember(clubMember, 200, 200)
                .map(qrCode -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.IMAGE_PNG);
                    headers.setContentLength(qrCode.length);
                    return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
                }).onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @PostMapping("/change-email")
    public Mono<ResponseEntity<String>> changeEmail(@RequestBody ChangeFieldDTO changeFieldDTO,
                                                    @AuthenticationPrincipal ClubMember clubMember) {
        return clubMemberService.changeEmail(clubMember.getUsername(), changeFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body("Email changed successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email changing failed\n" + e.getMessage())));
    }

    @PostMapping("/change-phone")
    public Mono<ResponseEntity<String>> changePhoneNumber(@RequestBody ChangeFieldDTO changeFieldDTO,
                                                          @AuthenticationPrincipal ClubMember clubMember) {
        return clubMemberService.changePhoneNumber(clubMember.getUsername(), changeFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body("PhoneNumber changed successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("PhoneNumber changing failed\n" + e.getMessage())));
    }

    @PostMapping("/change-password")
    public Mono<ResponseEntity<String>> changePassword(@RequestBody ChangeFieldDTO changeFieldDTO,
                                                          @AuthenticationPrincipal ClubMember clubMember) {
        return clubMemberService.changePassword(clubMember.getUsername(), changeFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body("Password changed successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Password changing failed\n" + e.getMessage())));
    }
}
