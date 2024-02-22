package org.hibernate.bugs;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Car {

    @Id
    @SequenceGenerator(name = "seq_car", sequenceName = "id_seq_car", allocationSize = 1)
    @GeneratedValue(generator = "seq_car", strategy = GenerationType.SEQUENCE)
    protected Long id;

    protected String brand;


    protected String model;

    @OneToMany(mappedBy = "car")
    protected Set<CarsClients> clients;
}
