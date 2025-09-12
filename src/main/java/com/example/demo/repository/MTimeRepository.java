package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.MTime;

public interface MTimeRepository extends JpaRepository<MTime, String> {
    List<MTime> findAllByOrderByTimeIdAsc();
}