package org.fab.booknetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class AuthenticationRequest {

    @NotEmpty(message = "email Name is required")
    @NotBlank(message = "email name is mandatory")
    @Email(message = "Email is not formatted")
    private String email;
    @NotEmpty(message = "password is required")
    @NotBlank(message = "password is mandatory")
    @Size(min=8, max = 12,message = "Password should be 8 and 12 characters long")
    private String password;
}
