package com.example.demo.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.TReservation;

public interface TReservationRepository extends JpaRepository<TReservation, Integer> {
    // 予約日の全予約を取得1
    List<TReservation> findByResDate(Timestamp resDate);
    
    // 予約日の全予約を取得2
    List<TReservation> findByResDateAndIsDeleted(Timestamp resDate, boolean isDeleted);
    
    // 特定のユーザーの、特定の日の予約を取得
    List<TReservation> findByResDateAndUserId(Timestamp resDate, String userId);
    
    // 特定のユーザーの、特定の日の予約を論理削除
    List<TReservation> findByResDateAndUserIdAndIsDeleted(Timestamp resDate, String userId, boolean isDeleted);
}