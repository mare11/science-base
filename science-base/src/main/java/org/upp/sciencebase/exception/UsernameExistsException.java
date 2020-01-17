package org.upp.sciencebase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameExistsException extends RuntimeException {
    private static final long serialVersionUID = 4901843089677957162L;

    public UsernameExistsException(String username) {
        super("User with username: " + username + " already exists!");
    }
}
