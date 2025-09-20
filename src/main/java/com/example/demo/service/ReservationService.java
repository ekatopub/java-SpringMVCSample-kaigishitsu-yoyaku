package com.example.demo.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<TReservation> getReservedListBetween(Timestamp start, Timestamp end) {
        return tReservationRepository.getReservedListBetween(start, end);
        
        
    }
    
    // 予約状況としてテンプレートに返す前にエラーチェックするメソッド
    public boolean isReserved(List<TReservation> reservedList, Integer roomId, Integer timeId) {
    	return reservedList.stream() .anyMatch(r -> roomId.equals(r.getRoomId()) && timeId.equals(r.getTimeId())); }
    
    
    
    // 予約または解除のロジックを処理するメソッド
    @Transactional
    public Map<String, Object> processReservation(Timestamp date, String userId, List<ReservationData> reservationData) {
    	
    	
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
    	
    	
        // 日付が過去のものでないかチェック
        LocalDateTime now = LocalDateTime.now();
        LocalDate reservationDate = date.toLocalDateTime().toLocalDate();
        if (reservationDate.isBefore(now.toLocalDate())) {
            response.put("message", "error.pastReservation");
            return response;
        }
    	
    	
    	
        // フォームから送信された「チェック済み」の予約キーをSetに格納
        Set<String> checkedKeys = reservationData.stream()
                .filter(ReservationData::isChecked)
                .map(data -> data.getRoomId() + "_" + data.getTimeId())
                .collect(Collectors.toSet());

        // ログイン中のユーザーの、指定日の全予約を取得
        // 日付の範囲検索に変更
        LocalDate targetDate = date.toLocalDateTime().toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.plus(1, ChronoUnit.DAYS).atStartOfDay().minus(1, ChronoUnit.SECONDS);

        Timestamp startDate = Timestamp.valueOf(startOfDay);
        Timestamp endDate = Timestamp.valueOf(endOfDay);

        // 新しいメソッドを呼び出す
        List<TReservation> existingReservations = tReservationRepository.findByResDateBetweenAndUserId(startDate, endDate, userId);

        // 既存の予約をループして、チェックが外れたものを削除（論理削除）する
        for (TReservation existingRes : existingReservations) {
            String resKey = existingRes.getRoomId() + "_" + existingRes.getTimeId();

            // フォームデータに存在しない（チェックが外れた）場合は論理削除
            if (!checkedKeys.contains(resKey)) {

                
                //過去の予約を削除できないようチェック
                Optional<MTime> optionalMTime = mTimeRepository.findById(existingRes.getTimeId());//Optionalは「nullかもしれない値」を上手に取り扱うためのクラス
                
                if (optionalMTime.isPresent()) {
                    MTime mTime = optionalMTime.get();
                    
                    String timeName = mTime.getTimeName();
                    int hour = Integer.parseInt(timeName.replace("時", ""));//"9時"などから変換
                    LocalTime reservationTime = LocalTime.of(hour, 0); 
               
                    LocalDate existingResDate = existingRes.getResDate().toLocalDateTime().toLocalDate();
                    LocalDateTime existingResDateTime = LocalDateTime.of(existingResDate, reservationTime);

                    if (existingResDateTime.isBefore(now)) {
                    	response.put("message", "error.pastCancellation");
                        return response;
                } else {  //過去でなければ削除
                existingRes.setDeleted(true);                                
                tReservationRepository.save(existingRes); // 論理削除
                response.put("success", true);
                response.put("message", "予約が正常に変更されました。");
                
                return response;
                }
                }
            }//if
        }//for
        
        // フォームから送信されたチェック済みの予約をループして、新規予約を追加する
        for (String key : checkedKeys) {
            String[] ids = key.split("_");
            Integer roomId = Integer.valueOf(ids[0]);
            Integer timeId = Integer.valueOf(ids[1]);

            // 既存の予約リストをチェックして、重複していなければ新規追加
            boolean isNewReservation = existingReservations.stream()
                    .noneMatch(r -> r.getRoomId().equals(roomId) && r.getTimeId().equals(timeId));

            if (isNewReservation) {
                TReservation newReservation = new TReservation();
                newReservation.setResDate(date);
                newReservation.setRoomId(roomId);
                newReservation.setTimeId(timeId);
                newReservation.setUserId(userId);
                newReservation.setDeleted(false);
                tReservationRepository.save(newReservation);
            }//if
        }//for
        response.put("success", true);
        response.put("message", "予約が正常に変更されました。");
        
        return response;
    }//processReservation

  //他人の予約のチェックを外せないようにするための準備　ログインユーザーの予約を取得し、キーのSetに変換
    public Set<String> getMyReservedKeys(Timestamp date, String userId) {
        // 対象日の日付範囲を計算
        LocalDate targetDate = date.toLocalDateTime().toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.plus(1, ChronoUnit.DAYS).atStartOfDay().minus(1, ChronoUnit.SECONDS);

        // ログインユーザーの予約をデータベースから取得
        List<TReservation> myReservations = tReservationRepository.findByResDateBetweenAndUserId(
            Timestamp.valueOf(startOfDay),
            Timestamp.valueOf(endOfDay),
            userId
        );

        // 予約がなくても空のSetを返すように変更
        if (myReservations == null || myReservations.isEmpty()) {
            return Collections.emptySet(); // 空のSetを返す
        }

        return myReservations.stream()
                .map(r -> r.getRoomId() + "_" + r.getTimeId())
                .collect(Collectors.toSet());
    }
    //他人の予約のチェックを外せないようにするための準備2　予約全体を取得し、キーのSetに変換し比較できるようにする
    public Set<String> getReservedKeys(Timestamp date) {
        // 対象日の日付範囲を計算
        LocalDate targetDate = date.toLocalDateTime().toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.plus(1, ChronoUnit.DAYS).atStartOfDay().minus(1, ChronoUnit.SECONDS);

        // データベースからすべての予約情報を取得（論理削除されたものを除く）
        List<TReservation> allReservedList = tReservationRepository.findByResDateBetweenAndIsDeleted(
            Timestamp.valueOf(startOfDay),
            Timestamp.valueOf(endOfDay),
            false
        );

        // 予約キーのセットを作成
        return allReservedList.stream()
                .map(r -> r.getRoomId() + "_" + r.getTimeId())
                .collect(Collectors.toSet());
    }
}
