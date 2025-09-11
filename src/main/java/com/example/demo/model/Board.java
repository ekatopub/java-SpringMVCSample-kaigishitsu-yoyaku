package com.example.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "board")//テーブル名


public class Board {
	

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY) //MySQLAUTO_INCREMENT機能を利用して、idを自動的に生成
	    @Column(name = "id", length = 10, nullable = false)
	    private int id;

	    @Column(name = "user_id", length = 4, nullable = false)
	    private String userId;

  
	//    @NotBlank(message = "{message.1}") // 空文字や空白のみの場合はエラー
	//    @Size(min = 1, max = 50, message = "{message.2}") // 文字数チェック
	//    @Column(name = "text", length = 25500, nullable = false)// 文字数チェック
	    @NotBlank(message = "{message.1}", groups = ValidGroup1.class)
	    @Size(min = 1, max = 50, message = "{message.2}", groups = ValidGroup2.class)
	    private String text;

	    @Column(name = "register_date",  nullable = false)
	    //@Column(name = "register_date", columnDefinition = "TIMESTAMP")
	    //private java.time.LocalDateTime registerDate;
	    private LocalDateTime registerDate;
	    
	    public void setId(int id) {
	        this.id= id;
	    }
	    
	    public void setUserId(String userId) {
	        this.userId = userId;
	    }

	    public void setText(String text) {
	        this.text = text;
	    }
	    
	    public void setRegisterDate(java.time.LocalDateTime registerDate) {
	        this.registerDate = registerDate;
	    }

	    public int getId() {
	        return this.id;
	    }
	    public String getUserId() {
	        return this.userId;
	    }
	    public String getText() {
	        return this.text;
	    }
	    
	    public java.time.LocalDateTime getRegisterDate() {
	        return this.registerDate;
	    }
	    
	}


