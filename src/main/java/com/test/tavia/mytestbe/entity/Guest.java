package com.test.tavia.mytestbe.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Guest implements Serializable {

    @Id
    private String id;

    private String email;

    private String nohp;

    private String maleName;

    private String femaleName;

    private String password;

    private Boolean isActive;

}
