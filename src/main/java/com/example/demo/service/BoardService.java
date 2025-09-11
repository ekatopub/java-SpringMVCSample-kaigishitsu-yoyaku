package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Board;

public interface BoardService {
	
    // 掲示板に投稿するメソッド
    void postBoard(Board board);

    // IDを指定して投稿を取得するメソッド
    Optional<Board> getBoardById(int id);
    
    // userIDを指定して投稿を取得するメソッド
    List<Board> getBoardsByUserId(String userId);
    
    // IDを指定して投稿を削除するメソッド
    void deleteBoardById(int id, String userId);
    
    // 全投稿を取得するメソッド
    List<Board> getAllBoards();
	
}
