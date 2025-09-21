package com.example.demo;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach; // BeforeEachをインポート
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.model.MTime;
import com.example.demo.repository.MTimeRepository;
import com.example.demo.repository.TReservationRepository;
import com.example.demo.service.ReservationData;
import com.example.demo.service.ReservationService;
// 他の必要なインポート

public class MyServiceTest {

    @InjectMocks
    private ReservationService reservationService; // テスト対象のサービス

    @Mock
    private TReservationRepository tReservationRepository; // 依存関係のリポジトリ
    @Mock
    private MTimeRepository mTimeRepository; // 依存関係のリポジトリ
    @Mock
    private Clock clock; // モック化したClock

    @BeforeEach // 各テストメソッドの前に実行
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReservationForPastTimeFails() {
        // Arrange
        // --- ここでClockの振る舞いを定義 ---
        // 2025-09-21 16:00 (Asia/Tokyo)を現在の時刻として設定
        when(clock.instant()).thenReturn(Instant.parse("2025-09-21T07:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("Asia/Tokyo"));

        // サービスメソッド内で'LocalDateTime.now(clock)'が呼び出されると、
        // 2025-09-21 16:00:00 (Asia/Tokyo)が返される
        
        // テスト用の予約データ（過去の時間帯）
     // Not recommended for new code, but works on older Java versions

        Timestamp date = Timestamp.valueOf(LocalDate.of(2025, 9, 21).atStartOfDay());

        String userId = "0001";
        List<ReservationData> reservationData = new ArrayList<>();

     // Create a new ReservationData object using the no-argument constructor
     ReservationData data = new ReservationData();
     data.setRoomId("1");
     data.setTimeId("1");
     data.setChecked(true);

     // Add the object to the list
     reservationData.add(data);

        // MTimeリポジトリのモック設定
        //when(mTimeRepository.findById(1)).thenReturn(Optional.of(new MTime(1, "9時")));
     when(mTimeRepository.findById(1)).thenReturn(Optional.of(new MTime(1, "9時", false)));
        when(tReservationRepository.findByResDateBetweenAndUserId(any(), any(), any())).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = reservationService.processReservation(date, userId, reservationData);

        // Assert
        assertThat(result.get("success")).isEqualTo(false);
        List<String> errorMessages = (List<String>) result.get("messages");
        assertThat(errorMessages).contains("error.attemptToMakePastReservation");
    }
}