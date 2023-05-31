package com.test.tavia.mytestbe.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.tavia.mytestbe.entity.ConfirmationToken;
import com.test.tavia.mytestbe.entity.Guest;
import com.test.tavia.mytestbe.model.GuestRequest;
import com.test.tavia.mytestbe.model.ResponseData;
import com.test.tavia.mytestbe.repository.ConfirmationTokenRepository;
import com.test.tavia.mytestbe.service.EmailService;
import com.test.tavia.mytestbe.service.GuestService;

@RestController
@RequestMapping("/api/")
public class GuestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GuestService guestService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @GetMapping("guests")
    public List<Guest> findAll() {
        return guestService.findALl();
    }

    @GetMapping("guests/{id}")
    public Guest findById(@PathVariable("id") String id) {
        return guestService.findById(id);
    }

    @PostMapping("guests")
    public ResponseEntity<ResponseData<Guest>> create(@RequestBody GuestRequest request) {
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
        responseData.setPayload(created);

        return ResponseEntity.ok(responseData);
    }

    @PutMapping("guests")
    public ResponseEntity<ResponseData<Guest>> update(@RequestBody GuestRequest request) {
        ResponseData<Guest> responseData = new ResponseData<>();
        
        Guest guest = guestService.findById(request.getId());
        if (guest != null) {
            guest.setEmail(request.getEmail());
            guest.setNohp(request.getNohp());
            guest.setPassword(passwordEncoder.encode(request.getPassword()));
            guest.setMaleName(request.getMaleName());
            guest.setFemaleName(request.getFemaleName());
            Guest created = guestService.update(guest);

            List<String> message = responseData.getMessages();
            message.add("Updated Guest Data Success.!");

            responseData.setStatus(true);
            responseData.setPayload(created);
        }

        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("guests/{id}")
    public void deleteById(@PathVariable("id") String id) {
        guestService.deleteById(id);
    }

}
