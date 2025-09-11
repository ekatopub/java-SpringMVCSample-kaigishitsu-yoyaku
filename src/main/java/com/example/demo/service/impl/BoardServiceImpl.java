package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Board;
import com.example.demo.repository.BoardRepository;
import com.example.demo.service.BoardService;

@Service
public class BoardServiceImpl implements BoardService{
	
    private final BoardRepository boardRepository;
    private final MessageSource messageSource;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository, MessageSource messageSource) {
        this.boardRepository = boardRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public void postBoard(Board board) {
        // 投稿テキストの改行を<br>タグに変換
        String convertedText = board.getText().replace("\n", "<br>");
        board.setText(convertedText);

        boardRepository.save(board);
    }

    @Override
    @Transactional(readOnly = true)//データの変更を伴わない読み取り操作
    public Optional<Board> getBoardById(int id) {
        // IDで投稿を検索
        return boardRepository.findById(id);
    }
    
    @Override
    @Transactional
    public void deleteBoardById(int id, String userId) {
        // IDを指定して投稿を取得
        Optional<Board> boardOptional = boardRepository.findById(id);
        
        // 投稿が存在し、ユーザーに権限がある場合のみ削除
        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            if (board.getUserId().equals(userId)) {
                boardRepository.deleteById(id);
            } else {
                // 権限がない場合、メッセージを取得して例外をスロー
                String errorMessage = messageSource.getMessage("board.delete.unauthorized", null, null);
                throw new SecurityException(errorMessage);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)//データの変更を伴わない読み取り操作
    public List<Board> getBoardsByUserId(String userId) {
        // userIDで投稿を検索
        return boardRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)//データの変更を伴わない読み取り操作
    public List<Board> getAllBoards() {
        // 全投稿を取得
        return boardRepository.findAll();

    }
}