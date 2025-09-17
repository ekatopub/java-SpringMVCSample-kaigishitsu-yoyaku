package com.example.demo.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.TReservation;

public interface TReservationRepository extends JpaRepository<TReservation, Integer> {
    // 予約日の全予約を取得1
    List<TReservation> findByResDate(Timestamp resDate);
    
    // 予約日の全予約を取得2
    List<TReservation> findByResDateAndIsDeleted(Timestamp resDate, boolean isDeleted);
    
    // 予約日の全予約を取得3
    @Query("SELECT r FROM TReservation r WHERE FUNCTION('DATE', r.resDate) = :resDate AND r.isDeleted = false")
    List<TReservation> findByResDateOnly(@Param("resDate") LocalDate resDate);
    // 予約日の全予約を取得4
    @Query("SELECT r FROM TReservation r WHERE r.resDate BETWEEN :start AND :end AND r.isDeleted = false")
    List<TReservation> getReservedListBetween(@Param("start") Timestamp start, @Param("end") Timestamp end);

    
    // 特定のユーザーの、特定の日の予約を取得
    List<TReservation> findByResDateAndUserId(Timestamp resDate, String userId);
    
    // 特定のユーザーの、特定の日の予約を論理削除
    List<TReservation> findByResDateAndUserIdAndIsDeleted(Timestamp resDate, String userId, boolean isDeleted);
    
    // 指定された日付範囲とユーザーIDで予約を検索
    @Query("SELECT r FROM TReservation r WHERE r.resDate BETWEEN :startDate AND :endDate AND r.userId = :userId AND r.isDeleted = false")
    List<TReservation> findByResDateBetweenAndUserId(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, @Param("userId") String userId);

    // 日付範囲とisDeletedフラグで検索
    List<TReservation> findByResDateBetweenAndIsDeleted(Timestamp startDate, Timestamp endDate, boolean isDeleted);
    
}