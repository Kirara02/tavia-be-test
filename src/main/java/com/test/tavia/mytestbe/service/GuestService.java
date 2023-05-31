package com.test.tavia.mytestbe.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.test.tavia.mytestbe.entity.Guest;
import com.test.tavia.mytestbe.exception.BadRequestException;
import com.test.tavia.mytestbe.exception.ResourceNotFoundException;
import com.test.tavia.mytestbe.repository.GuestRepository;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    public List<Guest> findALl() {
        return guestRepository.findAll();
    }

    public Guest create(Guest guest) {
        if (!StringUtils.hasText(guest.getId())) {
            throw new BadRequestException("Username harus diisi");
        }

        if (guestRepository.existsById(guest.getId())) {
            throw new BadRequestException("Username " + guest.getId() + " sudah terdaftar");
        }

        if (!StringUtils.hasText(guest.getEmail())) {
            throw new BadRequestException("Email harus diisi");
        }

        if (guestRepository.existsByEmail(guest.getEmail())) {
            throw new BadRequestException("Email " + guest.getEmail() + " sudah terdaftar");
        }

        guest.setIsActive(false);
        return guestRepository.save(guest);
    }

    public Guest findById(String id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest dengan ID " + id + " tidak ditemukan"));
    }

    public Guest findByEmail(String id) {
        return guestRepository.findByEmail(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest dengan Email " + id + " tidak ditemukan"));
    }

    public Guest update(Guest guest) {
        if (!StringUtils.hasText(guest.getId())) {
            throw new BadRequestException("Id harus diisi");
        }
        return guestRepository.save(guest);
    }

    public void deleteById(String id) {
        guestRepository.deleteById(id);
    }
}
