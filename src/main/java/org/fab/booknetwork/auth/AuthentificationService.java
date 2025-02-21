package org.fab.booknetwork.auth;

import jakarta.mail.MessagingException;

import org.fab.booknetwork.email.EmailService;
import org.fab.booknetwork.email.EmailTemplate;
import org.fab.booknetwork.role.RoleRepository;
import org.fab.booknetwork.security.JwtService;
import org.fab.booknetwork.user.Token;
import org.fab.booknetwork.user.TokenRepository;
import org.fab.booknetwork.user.User;
import org.fab.booknetwork.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class AuthentificationService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManagement;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Autowired
    public AuthentificationService(
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRepository userRepository,
            TokenRepository tokenRepository,
            EmailService emailService, JwtService jwtService, AuthenticationManager authenticationManagement) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.authenticationManagement = authenticationManagement;
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
        var newToken = generateAndSaveActivationToken(user);
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
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBluider = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBluider.append(characters.charAt(randomIndex));

        }
        return codeBluider.toString();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var auth = authenticationManagement.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims=new HashMap<String,Object>();
        var user=((User)auth.getPrincipal());
        claims.put("fullName", user.getFullName());
        var jwtToken=jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    //@Transactional
    public void activateAccount(String token) throws
            MessagingException {
        // Validate that token input is not null or empty

        String[] tokenParts = token.split(",");
        if (tokenParts.length > 0) {
            token = tokenParts[0];
            System.out.println("First part of the token: " + token);
           // serviceAuth.activateAccount(firstTokenPart); // Utiliser la première partie du token
        } else {
            System.out.println("Token is invalid, no comma found.");
        }

       // System.out.println(token);
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        Optional<Token> savedToken = tokenRepository.findByToken(token);

        // Vérifier si le token a été trouvé
        if (savedToken.isPresent()) {
            System.out.println("Token found: " + savedToken.get().getToken());
        } else {
            System.out.println(token+" Token not found");
        }



        Token savedTokenEntity =savedToken
                // todo -better exception
                .orElseThrow(()->new IllegalStateException("The provided activation token is invalid")
                );
        System.out.println("Token found in DB: " + savedTokenEntity);
            // Check if the token has expired
            if (LocalDateTime.now().isAfter(savedTokenEntity.getExpiresAt())) {
                // Regenerate and email a new activation token
                sendValidationEmail(savedTokenEntity.getUser());
                throw new RuntimeException("The activation token has expired. A new activation email has been sent.");
            }

            var user=userRepository.findById(savedTokenEntity.getUser().getId())
                .orElseThrow(()->new UsernameNotFoundException("user not found"));


            // Enable the user account and save changes
            if (!user.isEnabled()) {
                user.setEnabled(true);
                userRepository.save(user);
            }

            savedTokenEntity.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedTokenEntity);

    }
}
