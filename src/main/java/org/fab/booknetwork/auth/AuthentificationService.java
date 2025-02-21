package org.fab.booknetwork.auth;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;
import org.fab.booknetwork.email.EmailService;
import org.fab.booknetwork.email.EmailTemplate;
import org.fab.booknetwork.role.RoleRepository;
import org.fab.booknetwork.user.Token;
import org.fab.booknetwork.user.TokenRepository;
import org.fab.booknetwork.user.User;
import org.fab.booknetwork.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class AuthentificationService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Autowired
    public AuthentificationService(
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRepository userRepository,
            TokenRepository tokenRepository,
            EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }


    public void register(RegistrationRequest request) throws MessagingException {

        var userRole = roleRepository.findByName("USER")
                //todo -better exception
                .orElseThrow(() -> new IllegalStateException("Role user was not initialized"));
        System.out.println(userRole);
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roleList(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken=generateAndSaveActivationToken(user);
        // todo- send email

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplate.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken=generateActivationCode(6);
        var token= Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters="0123456789";
        StringBuilder codeBluider=new StringBuilder();
        SecureRandom secureRandom=new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex=secureRandom.nextInt(characters.length());
            codeBluider.append(characters.charAt(randomIndex));

        }
        return codeBluider.toString();
    }
}
