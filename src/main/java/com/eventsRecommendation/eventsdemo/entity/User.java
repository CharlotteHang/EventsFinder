package com.eventsRecommendation.eventsdemo.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name="users")
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class User {
    @Id
    @EqualsAndHashCode.Include
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
            @Setter
    int userId;

    String email;

    String password;

    String firstName;

    String lastName;

    @ManyToMany(fetch= FetchType.EAGER,
            cascade= {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH},
            mappedBy = "users",
            targetEntity = Item.class)
    List<Item> items;

    @OneToMany(fetch= FetchType.LAZY, mappedBy="sessionUser")
            @Setter
    List<Session> sessions;

    public User (String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password; //hashPassword
        this.firstName = firstName;
        this.lastName = lastName;
    }

}

