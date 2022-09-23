package com.parakramaba.redispagination.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "address")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private Integer apartmentNo;

    @Column(nullable = false)
    private String City;

    @OneToMany(mappedBy = "address")
    @JsonIgnore
    private List<Person> persons;

}
