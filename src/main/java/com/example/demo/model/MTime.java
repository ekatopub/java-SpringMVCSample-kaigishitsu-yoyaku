package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "m_time")
@Data
public class MTime {

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private Integer id;

    @Column(name = "name", length = 10, nullable = false)
    private String timeName;//9時,10時,,,17時
    
    @Column(name = "is_deleted",  nullable = false)
    private boolean timeIsDeleted;
    
    public String getTimeName() {
        return timeName;
    }
}

//@Dataアノテーション =Lombokがコンパイル時にゲッター、セッター、toString()、equals()、hashCode()、およびrequiredArgsConstructorを自動的に生成する