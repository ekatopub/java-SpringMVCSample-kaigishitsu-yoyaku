package com.example.demo.config;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    // application.propertiesから'fixed-time'の値を読み込む
    @Value("${fixed-time:}") // :はデフォルト値（空）を意味する
    private String fixedTime;

    @Bean
    public Clock clock() {
        // fixedTimeプロパティが設定されているかチェック
        if (!fixedTime.isEmpty()) {
            // プロパティが設定されていれば、その日時でClockを作成
            Instant instant = Instant.parse(fixedTime);
            return Clock.fixed(instant, ZoneId.systemDefault());
        }
        // 設定されていなければ、通常通りシステム時刻を使用
        return Clock.systemDefaultZone();
    }
}