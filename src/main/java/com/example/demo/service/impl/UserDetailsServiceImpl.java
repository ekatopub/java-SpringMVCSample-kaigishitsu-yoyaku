package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;
    private final MessageSource messageSource;

    //コンストラクタインジェクション
    public UserDetailsServiceImpl(UserRepository userRepository,MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // データベースからユーザー情報を取得
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(//ユーザーが見つからない場合
                        messageSource.getMessage("1", null, Locale.JAPAN)));
        //あとはSpring SecurityのExceptionTranslationFilterがこの例外をキャッチして
        //ログインページにリダイレクトしてくれるのでそれに任せればOK
        

        // --- debug ---
        // データベースから取得したハッシュ化済みパスワードをコンソールに出力
        //System.out.println("DBから取得したハッシュ化済みパスワード: " + user.getPassword());
        // ------------------------

        // 権限リスト作成
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("GENERAL"));

        // Spring SecurityのUserクラスを使用してUserDetailsを作成
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                authorities
        );
    }
}