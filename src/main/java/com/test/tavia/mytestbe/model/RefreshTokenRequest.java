package com.test.tavia.mytestbe.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class RefreshTokenRequest implements Serializable {
    private String refreshToken;
}
