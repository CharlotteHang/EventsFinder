package com.eventsRecommendation.eventsdemo.entity;

import java.security.SecureRandom;
import java.util.Calendar;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="sessions")
@Data
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Session {
    private static final int LENGTH = 64;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();;

    @Id
    @EqualsAndHashCode.Include
    String token;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="userId", nullable=false)
    User sessionUser;

    Calendar dateAuthenticated;
    Calendar dateOnlineSince;

    public static String getRandom() {
        StringBuilder randomString = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            randomString.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return randomString.toString();
    }

    public Boolean isAuthenticated() {
        return dateAuthenticated != null;
    }
}
