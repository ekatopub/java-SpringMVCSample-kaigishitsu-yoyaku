package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.MRoom;

public interface MRoomRepository extends JpaRepository<MRoom, Integer> {
    List<MRoom> findAllByOrderByIdAsc();
}