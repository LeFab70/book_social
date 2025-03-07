package org.fab.booknetwork.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name="Authentification")
public class AuthenticationController {
    private final AuthentificationService serviceAuth;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        serviceAuth.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    )
    {
        return ResponseEntity.ok(serviceAuth.authenticate(request));
    }

    @GetMapping  ("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        //System.out.println("recieve token "+token);
        serviceAuth.activateAccount(token);
    }
}
