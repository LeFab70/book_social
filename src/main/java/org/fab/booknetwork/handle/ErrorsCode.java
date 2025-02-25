package org.fab.booknetwork.handle;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorsCode {
    NO_CODE(0, "No code", HttpStatus.NOT_IMPLEMENTED),
    INCORRECT_CREDENTIALS(300, "Incorrect credentials", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(301, "New password does not match", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(302, "Account locked", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED(303, "Account disabled", HttpStatus.FORBIDDEN),
    ACCOUNT_EXPIRED(304, "Account expired", HttpStatus.FORBIDDEN),
    BAD_CREDENTIALS(305, "Bad credentials", HttpStatus.BAD_REQUEST),
    ;
    @Getter
    private final int code;
    @Getter
    private final String message;
    @Getter
    private final HttpStatus httpStatus;

    ErrorsCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
