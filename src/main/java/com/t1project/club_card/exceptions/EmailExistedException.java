package com.t1project.club_card.exceptions;

public class EmailExistedException extends RuntimeException {
    public EmailExistedException(String message) {
        super(message);
    }
}
