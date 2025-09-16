package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "m_room")
@Data
public class MRoom {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private Integer id;

    @Column(name = "name", length = 10, nullable = false)
    private String roomName;
    
    @Column(name = "is_deleted",  nullable = false)
    private boolean roomIsDeleted;
}