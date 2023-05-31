package com.test.tavia.mytestbe.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class GuestRequest implements Serializable {
    private String id;
    private String email;
    private String nohp;
    private String maleName;
    private String femaleName;
    private String password;
}
