package com.example.demo.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.MRoom;
import com.example.demo.model.MTime;
import com.example.demo.model.TReservation;
import com.example.demo.repository.MRoomRepository;
import com.example.demo.repository.MTimeRepository;
import com.example.demo.repository.TReservationRepository;

@Service
public class ReservationService {
    private final MTimeRepository mTimeRepository;
    private final MRoomRepository mRoomRepository;
    private final TReservationRepository tReservationRepository;

    public ReservationService(MTimeRepository mTimeRepository, MRoomRepository mRoomRepository, TReservationRepository tReservationRepository) {
        this.mTimeRepository = mTimeRepository;
        this.mRoomRepository = mRoomRepository;
        this.tReservationRepository = tReservationRepository;
    }

    public List<MTime> getAllTimes() {
        return mTimeRepository.findAllByOrderByIdAsc();
    }

    public List<MRoom> getAllRooms() {
        return mRoomRepository.findAllByOrderByIdAsc();
    }
    public List<TReservation> getReservedListByDate(Timestamp date) {
        return tReservationRepository.findByResDateAndIsDeleted(date, false);
    }
    
    // 予約状況としてテンプレートに返す前にエラーチェックするメソッド
    public boolean isReserved(List<TReservation> reservedList, Integer roomId, Integer timeId) {
    	return reservedList.stream() .anyMatch(r -> roomId.equals(r.getRoomId()) && timeId.equals(r.getTimeId())); }
    
    
    
    // 予約または解除のロジックを処理するメソッド
    @Transactional
    public void processReservation(Timestamp date, String userId, List<ReservationData> reservationData) {
        // 現在のユーザーの、指定日の全予約を取得
        List<TReservation> existingReservations = tReservationRepository.findByResDateAndUserId(date, userId);

        for (ReservationData data : reservationData) {
            // 既存の予約をチェック　部屋IDと時間DIが両方一致しているものを探して、既存の予約と重複していないかをチェックする

            boolean isExisting = existingReservations.stream()
            		.anyMatch(r -> r.getRoomId().equals(Integer.valueOf(data.getRoomId())) && r.getTimeId().equals(Integer.valueOf(data.getTimeId())));
            		//指定された条件に一致する要素がリスト内に1つでも存在するかどうかをチェック　
            if (data.isChecked()) {
                // チェックされている（予約する）場合
                if (!isExisting) {
                    // 新規予約を登録
                    TReservation newReservation = new TReservation();
                    newReservation.setResDate(date);// TIMESTAMP型に変更した
                    newReservation.setRoomId(Integer.valueOf(data.getRoomId())); // StringからIntegerに変換
                    newReservation.setTimeId(Integer.valueOf(data.getTimeId())); // StringからIntegerに変換
                    newReservation.setUserId(userId);
                    newReservation.setDeleted(false);
                    tReservationRepository.save(newReservation);
                }
            } else {
                // チェックされていない（解除する）場合
                if (isExisting) {
                    // 既存の予約を削除
                    existingReservations.stream()
                    	.filter(r -> r.getRoomId().equals(Integer.valueOf(data.getRoomId())) && r.getTimeId().equals(Integer.valueOf(data.getTimeId())))
                    	.findFirst()
                        .ifPresent(tReservationRepository::delete);
                }
            }
        }
    }
}
