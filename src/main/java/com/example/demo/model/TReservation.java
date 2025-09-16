package com.example.demo.model;

import java.sql.Timestamp;

//@Entity, @Id, @Table, @Columnを使えるようにする
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "t_reservation")
@Data
public class TReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//IDを自動採番
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", nullable = false, length = 4)
    private String userId;

    @Column(name = "res_date", nullable = false)
    private Timestamp resDate;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "time_id", nullable = false)
    private Integer timeId;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}