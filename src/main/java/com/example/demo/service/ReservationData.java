package com.example.demo.service;

import lombok.Data;

@Data //フォームデータをバインドするためのクラス　ユーザーがフォームで送信したデータを受け取るためのクラス
public class ReservationData {
    private String roomId;
    private String timeId;
    private boolean checked;
}