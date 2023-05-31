package com.test.tavia.mytestbe.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ChangePasswordRequest implements Serializable {
    private String newPassword;
    private String confirmPassword;
}
