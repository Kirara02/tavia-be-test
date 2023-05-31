package com.test.tavia.mytestbe.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.test.tavia.mytestbe.entity.Guest;
import com.test.tavia.mytestbe.exception.BadRequestException;
import com.test.tavia.mytestbe.repository.GuestRepository;

@Service
public class UserDetailsImplService implements UserDetailsService {
    @Autowired

    private GuestRepository guestRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + email + " tidak ditemukan"));

        boolean enabled = guest.getIsActive();
        UserDetailsImpl user = UserDetailsImpl.build(guest);

        if (!enabled) {
            throw new BadRequestException("Account is not verified");
        }

        return user;
    }

}
