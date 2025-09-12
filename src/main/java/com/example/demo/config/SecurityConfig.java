package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler; // カスタムハンドラ


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/login").permitAll()
                .antMatchers("/webjars/**", "/css/**", "/js/**", "custom.css").permitAll()
                .antMatchers("/h2-console/**").permitAll() // H2コンソール用
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/login")
                .loginPage("/login")
                .failureUrl("/login?error")
                .usernameParameter("userId")
                .passwordParameter("password")
                .defaultSuccessUrl("/reservation/", true)
               // .permitAll()
                .failureHandler(customAuthenticationFailureHandler) // カスタムハンドラ
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
            );

        // H2コンソールを使用可能にするための設定（開発時のみ）
        http.headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler failureHandler =
            new SimpleUrlAuthenticationFailureHandler("/login?error=customError");
        
        // フラッシュ属性からデフォルトのエラーメッセージを削除
        failureHandler.setUseForward(false);
        failureHandler.setAllowSessionCreation(false);
        
        // エラーメッセージの属性名をカスタマイズ（任意）
        // ここで独自のメッセージキーを設定できる
        //return new SimpleUrlAuthenticationFailureHandler("/login?error=customError");
        return failureHandler;
    }

 
}