package com.test.tavia.mytestbe.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.tavia.mytestbe.entity.ConfirmationToken;
import com.test.tavia.mytestbe.entity.Guest;
import com.test.tavia.mytestbe.exception.BadRequestException;
import com.test.tavia.mytestbe.model.ChangePasswordRequest;
import com.test.tavia.mytestbe.model.ForgotPasswordRequest;
import com.test.tavia.mytestbe.model.JwtResponse;
import com.test.tavia.mytestbe.model.LoginRequest;
import com.test.tavia.mytestbe.model.RefreshTokenRequest;
import com.test.tavia.mytestbe.model.RegisterRequest;
import com.test.tavia.mytestbe.model.ResponseData;
import com.test.tavia.mytestbe.repository.ConfirmationTokenRepository;
import com.test.tavia.mytestbe.security.jwt.JwtUtils;
import com.test.tavia.mytestbe.security.service.UserDetailsImpl;
import com.test.tavia.mytestbe.security.service.UserDetailsImplService;
import com.test.tavia.mytestbe.service.EmailService;
import com.test.tavia.mytestbe.service.GuestService;

@RestController
@RequestMapping("auth/")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GuestService guestService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsImplService userDetailsImplService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("signin")
    public ResponseEntity<ResponseData<JwtResponse>> auth(@RequestBody LoginRequest request, Errors errors) {
        ResponseData<JwtResponse> responseData = new ResponseData<>();

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                responseData.getMessages().add(error.getDefaultMessage());
            }
            responseData.setStatus(false);
            responseData.setPayload(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        String token = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshJwtToken(authentication);
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        responseData.setStatus(true);
        responseData.setPayload(new JwtResponse(token, refreshToken,
                principal.getEmail(), principal.getNohp(), principal.getMaleName(), principal.getFemaleName(),
                principal.getPassword(), principal.getIsActive()));

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("signup")
    public ResponseEntity<ResponseData<Guest>> signup(@RequestBody RegisterRequest request, Errors errors) {

        ResponseData<Guest> responseData = new ResponseData<>();
        Guest guest = new Guest();
        guest.setId(UUID.randomUUID().toString());
        guest.setEmail(request.getEmail());
        guest.setNohp(request.getNohp());
        guest.setPassword(passwordEncoder.encode(request.getPassword()));
        guest.setMaleName(request.getMaleName());
        guest.setFemaleName(request.getFemaleName());
        guest.setIsActive(false);
        Guest created = guestService.create(guest);

        ConfirmationToken confirmationToken = new ConfirmationToken(created);
        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(request.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("kirara.verify@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/auth/confirm-account?token=" + confirmationToken.getConfirmationToken());

        emailService.sendEmail(mailMessage);
        List<String> message = responseData.getMessages();
        message.add("A verification email has been sent to: " + request.getEmail());

        responseData.setStatus(true);
        responseData.setMessages(message);
        responseData.setPayload(created);

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("confirm-account")
    public ResponseEntity<String> confirm(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if (token != null) {
            Guest guest = guestService.findByEmail(token.getGuest().getEmail());
            guest.setIsActive(true);
            guestService.update(guest);
        } else {
            throw new BadRequestException("The link is invalid or broken!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Account is verified");
    }

    @PostMapping("forgot-password")
    public ResponseEntity<String> forgotPass(@RequestBody ForgotPasswordRequest request, Errors errors) {
        Guest guest = guestService.findByEmail(request.getEmail());
        if (guest != null) {
            ConfirmationToken confirmationToken = new ConfirmationToken(guest);
            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(request.getEmail());
            mailMessage.setSubject("Complete Forgot Password Verification!");
            mailMessage.setFrom("kirara.verify@gmail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    + "http://localhost:8080/auth/change-password?token=" + confirmationToken.getConfirmationToken());

            emailService.sendEmail(mailMessage);
        } else {
            throw new BadRequestException("Email Tidak ditemukan");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Email forgot password verification success sender");
    }

    @PostMapping("change-password")
    public ResponseEntity<String> changePassword(@RequestParam("token") String confirmationToken,
            @RequestBody ChangePasswordRequest request) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if (token != null) {
            if (request.getNewPassword().equals(request.getConfirmPassword())) {
                Guest guest = guestService.findByEmail(token.getGuest().getEmail());
                guest.setPassword(passwordEncoder.encode(request.getNewPassword()));
                guestService.update(guest);
            } else {
                throw new BadRequestException("Password confirmation does not match!");
            }

        } else {
            throw new BadRequestException("The link is invalid or broken!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Account is verified");
    }

    @PostMapping("refreshToken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        boolean valid = jwtUtils.validateJwtToken(token);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsImplService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, null,
                userDetailsImpl.getAuthorities());
        String newToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(newToken, refreshToken, username,
                userDetailsImpl.getNohp(), userDetailsImpl.getMaleName(), userDetailsImpl.getFemaleName(),
                userDetailsImpl.getPassword(), userDetailsImpl.getIsActive()));
    }
}
