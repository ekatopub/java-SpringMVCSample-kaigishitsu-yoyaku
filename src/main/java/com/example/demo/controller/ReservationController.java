package com.example.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.MRoom;
import com.example.demo.model.MTime;
import com.example.demo.service.ReservationService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
	
    private final UserService userService;
    private final ReservationService reservationService;

    public ReservationController(UserService userService, ReservationService reservationService) {
        this.userService = userService;
        this.reservationService = reservationService;
    }//Springは、コンストラクタの引数にUserServiceインターフェースが指定されていると、自動的にその唯一の実装クラス（UserServiceImpl）を探して注入してくれる
	

    @GetMapping("/") //  .defaultSuccessUrl("/reservation/", true)の最後のスラッシュに対応
    public String getReservationPage(
    	       Model model,
    	        @RequestParam("date") Optional<String> dateStr
    	    ) {
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
    	
        // このメソッドが呼び出されると、予約画面（reservation.html）を表示する
        return "reservation";
    }
    


}