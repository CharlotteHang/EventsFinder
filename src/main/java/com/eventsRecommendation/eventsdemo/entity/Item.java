package com.eventsRecommendation.eventsdemo.entity;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="items")
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Item {
    @Id
    @EqualsAndHashCode.Include
    String itemId;

    String name;

    String address;

    String imageUrl;

    String url;

    String localDate;

    String time;

    String type;

    String status;

    String genres;

    double distance;

    double rating;

    @ManyToMany(fetch= FetchType.LAZY, cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="history",
            joinColumns = @JoinColumn(name="itemId"),
            inverseJoinColumns = @JoinColumn(name="userId")
    )
    @JsonIgnore
    Set<User> users;

    @OneToMany(fetch= FetchType.EAGER, mappedBy="item", cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
            @Setter
    Set<Category> categories;

    @Transient
    boolean favorite;
}


