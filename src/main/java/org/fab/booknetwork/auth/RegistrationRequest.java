package org.fab.booknetwork.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "First Name is required")
    @NotBlank(message = "First name is mandatory")
    private String firstname;
    @NotEmpty(message = "Last Name is required")
    @NotBlank(message = "Last name is mandatory")
    private String lastname;
    @NotEmpty(message = "email Name is required")
    @NotBlank(message = "email name is mandatory")
    @Email(message = "Email is not formatted")
    private String email;
    @NotEmpty(message = "password is required")
    @NotBlank(message = "password is mandatory")
    @Size(min=8, max = 12,message = "Password should be 8 and 12 characters long")
    private String password;
}
