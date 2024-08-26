package com.t1project.club_card.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.t1project.club_card.models.ClubMember;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;

@Service
public class QRCodeService {

    public Mono<byte[]> generateQRCodeForClubMember(ClubMember clubMember, int width, int height) {
        return Mono.fromCallable(() -> {
            final String textToEncode = String.format(
                    "Username: %s\nEmail: %s\nphone: %s\nIsLocked: %b\nRoles: %s\nPrivilege: %s",
                    clubMember.getUsername(),
                    clubMember.getEmail(),
                    clubMember.getPhoneNumber(),
                    clubMember.isLocked(),
                    String.join(", ", clubMember.getRoles()),
                    clubMember.getPrivilege()
            );
            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final BitMatrix bitMatrix = qrCodeWriter.encode(textToEncode, BarcodeFormat.QR_CODE, width, height);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }).onErrorResume(e -> Mono.error(new RuntimeException("Error generating qr")));
    }
}
