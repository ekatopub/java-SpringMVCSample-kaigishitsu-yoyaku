package com.example.demo.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.MRoom;
import com.example.demo.model.MTime;
import com.example.demo.model.TReservation;
import com.example.demo.service.ReservationData;
import com.example.demo.service.ReservationService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
	
    private final UserService userService;
    private final ReservationService reservationService;
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);


    public ReservationController(UserService userService, ReservationService reservationService) {
        this.userService = userService;
        this.reservationService = reservationService;

    }//Springは、コンストラクタの引数にUserServiceインターフェースが指定されていると、自動的にその唯一の実装クラス（UserServiceImpl）を探して注入してくれる
	

    @GetMapping("/") //  .defaultSuccessUrl("/reservation/", true)の最後のスラッシュに対応
    public String getReservationPage(
    	       Model model,
    	        @RequestParam("date") Optional<String> dateStr
    	    ) {
    	
        // ロガーを使用してデバッグログを出力       	
        logger.debug("getReservationPage is called");
    	
        // ログインユーザーのユーザー名を取得してモデルに追加
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        model.addAttribute("userName", userService.findUserNameByUserId(currentUsername));
    	
     // 日付の処理
        LocalDate today = LocalDate.now();
        LocalDate displayDate = dateStr.map(LocalDate::parse).orElse(today);
        
        model.addAttribute("displayDate", displayDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        model.addAttribute("previousDate", displayDate.minusDays(1));
        model.addAttribute("nextDate", displayDate.plusDays(1));

        // マスタデータを取得
        List<MTime> timeList = reservationService.getAllTimes();
        List<MRoom> roomList = reservationService.getAllRooms();
        

        
        // 取得したデータをモデルに追加
        model.addAttribute("timeList", timeList);
        model.addAttribute("roomList", roomList);
        
        // ロガーを使用してデバッグログを出力
        logger.debug("取得した部屋リスト: {}", roomList);
        logger.debug("取得した時間リスト: {}", timeList);
        
     // 予約済みの情報を取得
        // Date型のdisplayDateをTimestampに変換してリポジトリに渡す
        /*
        Timestamp timestamp = Timestamp.valueOf(displayDate.atStartOfDay());//時間がマッチしない
        List<TReservation> reservedList = reservationService.getReservedListByDate(timestamp);
        model.addAttribute("displayDateRaw", displayDate.toString());
        model.addAttribute("reservedList", reservedList);
        */
        LocalDate targetDate = dateStr.map(LocalDate::parse).orElse(LocalDate.now());
        model.addAttribute("displayDateRaw", targetDate.toString());

        // resDateの範囲指定用
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);

        // 予約取得（範囲指定）
        List<TReservation> reservedList = reservationService.getReservedListBetween(startTimestamp, endTimestamp);

        
        
        

        //logger.debug("取得した予約リストreservedList: {}", reservedList);
        for (TReservation r : reservedList) {
            logger.debug("Debug reserved: room_id={}, time_id={}", r.getRoomId(), r.getTimeId());
        }
        for (TReservation r : reservedList) {
            if (r.getRoomId() == null || r.getTimeId() == null) {
                logger.error("Reserved object with null ID found: {}", r);
            }
        }
        // ロガーを使用してデバッグログを出力
        logger.debug("取得した予約リストreservedList: {}", reservedList);

        
        
        //ラムダ式を使わない方法1
        /*
        Set<String> reservedKeys2 = reservedList.stream()
        	    .filter(r -> r.getRoomId() != null && r.getTimeId() != null)
        	    .map(r -> r.getRoomId() + "_" + r.getTimeId())
        	    .collect(Collectors.toSet());

        	model.addAttribute("reservedKeys2", reservedKeys2);
          */  
        //ラムダ式を使わない方法2	
        	Set<String> reservedKeys = new HashSet<>();
        	for (MRoom room : roomList) {
        	    for (MTime time : timeList) {
        	        if (reservationService.isReserved(reservedList, room.getId(), time.getId())) {
        	            reservedKeys.add(room.getId() + "_" + time.getId());
        	        }
        	    }
        	}
        	model.addAttribute("reservedKeys", reservedKeys);	
            // ロガーを使用してデバッグログを出力       	
            logger.debug("取得した予約リストreservedKeys: {}", reservedKeys);
            
            

            
            	
        // このメソッドが呼び出されると、予約画面（reservation.html）を表示する
        return "reservation";
    }
    
 
    
    @PostMapping("/process")
    public String processReservation(@RequestParam("date") String dateStr,
                                     @RequestParam Map<String, String> formData) {
        // ロガーを使用してデバッグログを出力       	
        logger.debug("processReservation is called");
    	
    	
        // ログインユーザーIDを取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        // 日付をTimestampに変換
        LocalDateTime localDateTime = LocalDate.parse(dateStr).atStartOfDay();
        Timestamp date = Timestamp.valueOf(localDateTime);
        
        //デバッグログ
        formData.forEach((key, value) -> {
            logger.debug("フォームデータ: key={}, value={}", key, value);
        });

        
        
        // フォームデータをReservationDataリストに変換
        List<ReservationData> reservationData = new ArrayList<>();
        formData.forEach((key, value) -> {
            if (key.startsWith("reservations[")) {
                // キーからroomIdとtimeIdを抽出
                Matcher matcher = Pattern.compile("reservations\\[([^\\]]+)\\]\\[([^\\]]+)\\]").matcher(key);
                if (matcher.find()) {
                    ReservationData data = new ReservationData();
                    data.setRoomId(matcher.group(1));
                    data.setTimeId(matcher.group(2));
                    data.setChecked(value.equals("true"));
                    logger.debug("予約データ: roomId={}, timeId={}, checked={}", data.getRoomId(), data.getTimeId(), data.isChecked());
                    reservationData.add(data);
                }
            }
        });
        
        // 予約サービスを呼び出して処理を実行
        reservationService.processReservation(date, userId, reservationData);
        
        // 処理後に元の画面にリダイレクト
        return "redirect:/reservation/?date=" + dateStr;
    }
    


}