package org.upp.sciencebase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MagazineNameExistsException extends RuntimeException {

    private static final long serialVersionUID = 9017067739784724912L;

    public MagazineNameExistsException(String name, String issn) {
        super("Magazine with name: " + name + " or issn: " + issn + " already exists!");
    }

}
