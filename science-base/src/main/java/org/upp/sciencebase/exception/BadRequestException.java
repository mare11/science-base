package org.upp.sciencebase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 2346413247257548953L;

    public BadRequestException() {
        super("Invalid request data!");
    }
}
