package com.test.tavia.mytestbe.security.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.test.tavia.mytestbe.entity.Guest;

import lombok.Data;

@Data
public class UserDetailsImpl implements UserDetails {

    private String id;
    private String email;
    private String nohp;
    private String maleName;
    private String femaleName;
    private String password;
    private Boolean isActive;

    public UserDetailsImpl(String id, String email, String nohp, String maleName, String femaleName, String password,
            Boolean isActive) {
        this.id = id;
        this.email = email;
        this.nohp = nohp;
        this.maleName = maleName;
        this.femaleName = femaleName;
        this.password = password;
        this.isActive = isActive;
    }

    public static UserDetailsImpl build(Guest guest) {
        return new UserDetailsImpl(
                guest.getId(),
                guest.getEmail(),
                guest.getNohp(),
                guest.getMaleName(),
                guest.getFemaleName(),
                guest.getPassword(),
                guest.getIsActive());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }
    
}
