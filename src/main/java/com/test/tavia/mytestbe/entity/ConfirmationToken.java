package com.test.tavia.mytestbe.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @OneToOne(targetEntity = Guest.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "guest_id")
    private Guest guest;

    public ConfirmationToken(Guest guest) {
        this.guest = guest;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

}