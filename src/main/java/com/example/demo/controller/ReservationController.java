package com.example.demo.controller;



import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @GetMapping("/") //                .defaultSuccessUrl("/reservation/", true)の最後のスラッシュに対応
    public String getReservationPage(Model model) {
        // ログインユーザーのユーザー名を取得してモデルに追加
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        model.addAttribute("userName", currentUsername);
    	
    	
        // このメソッドが呼び出されると、予約画面（reservation.html）を表示する
        return "reservation";
    }
}