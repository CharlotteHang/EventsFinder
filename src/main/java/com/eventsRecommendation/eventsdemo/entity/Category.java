package com.eventsRecommendation.eventsdemo.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Entity
@Table(name = "categories")
@IdClass(JoinCategoryKey.class)
@Data
@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
public class Category {

  @Id
  @EqualsAndHashCode.Include
  @ManyToOne(fetch= FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "itemId")
  @JsonIgnore
  Item item;

  @Id @EqualsAndHashCode.Include String categoryType;
}
