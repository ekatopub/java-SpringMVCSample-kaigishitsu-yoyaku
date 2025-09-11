package com.example.demo.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Board;

@Repository

public interface BoardRepository extends JpaRepository<Board, Integer> {



// ユーザーIDでユーザーを検索するメソッド

// Spring Data JPAがメソッド名から自動でSQLクエリを生成する

//Spring Data JPAは、findByに続くフィールド名（UserId）を解析し、WHERE user_id = ?のようなSQLクエリを自動で生成。

//Optionalを使用することで、検索結果が見つからない場合のNullPointerExceptionを防ぐ。
	

Optional<Board> findById(int Id);

//複数結果になるのでOptionalではなくList
List<Board> findByUserId(String userId);

}