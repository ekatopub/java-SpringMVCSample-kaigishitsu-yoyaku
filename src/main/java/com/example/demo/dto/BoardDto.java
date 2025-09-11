package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDto {
    private Integer id;
    private String userId; // ログインID
    private String userName; // 表示用ユーザー名
    private LocalDateTime registerDate;
    private String text;
}