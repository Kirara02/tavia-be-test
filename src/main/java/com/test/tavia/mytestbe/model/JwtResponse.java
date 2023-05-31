package com.test.tavia.mytestbe.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class JwtResponse implements Serializable {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String email;
    private String nohp;
    private String maleName;
    private String femaleName;
    private String password;
    private Boolean isActive;

    public JwtResponse(String token, String refreshToken,  String email, String nohp, String maleName,
            String femaleName, String password, Boolean isActive) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
        this.nohp = nohp;
        this.maleName = maleName;
        this.femaleName = femaleName;
        this.password = password;
        this.isActive = isActive;
    }

}
