package com.test.tavia.mytestbe.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ForgotPasswordRequest implements Serializable {
    private String email;
}
