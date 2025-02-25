package org.fab.booknetwork.handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;


@RestControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler(LockedException.class)
   public ResponseEntity<ExceptionResponse> handleException(LockedException e){
       return ResponseEntity
               .status(HttpStatus.UNAUTHORIZED)
               .body(
                       ExceptionResponse.builder()
                               .businessCode(ErrorsCode.ACCOUNT_LOCKED.getCode())
                               .errorDescription(ErrorsCode.ACCOUNT_LOCKED.getMessage())
                               .error(e.getMessage())
                               .build()
               );
   };


    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessCode(ErrorsCode.ACCOUNT_DISABLED.getCode())
                                .errorDescription(ErrorsCode.ACCOUNT_DISABLED.getMessage())
                                .error(e.getMessage())
                                .build()
                );
    };


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessCode(ErrorsCode.BAD_CREDENTIALS.getCode())
                                .errorDescription(ErrorsCode.BAD_CREDENTIALS.getMessage())
                                .error(e.getMessage())
                                .build()
                );
    };


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException e){
        Set<String> validationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(java.util.stream.Collectors.toSet());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(validationErrors)
                                .build()
                );
    };


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e){
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .errorDescription("An error occurred, please try again later or contact the administrator")
                                .error(e.getMessage())
                                .build()
                );
    };
}
