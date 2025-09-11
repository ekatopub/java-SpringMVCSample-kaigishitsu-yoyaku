package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.BoardDto;
import com.example.demo.model.Board;
import com.example.demo.service.BoardService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecuritySession;

@Controller // ビューを返すため、@Controllerを使用
@RequestMapping("/board")
public class BoardViewController {
    private static final Logger logger = LoggerFactory.getLogger(BoardViewController.class);


	
    private final BoardService boardService;
    private final UserService userService; 
    private final SecuritySession securitySession;

    @Autowired
    public BoardViewController(BoardService boardService, UserService userService, SecuritySession securitySession) {
        this.boardService = boardService;
        this.userService = userService;
        this.securitySession = securitySession;
    }

    @GetMapping("/")
    public String getBoardList(Model model) {
        logger.debug("--- getBoardListメソッドが呼び出されました ---");
        
        List<Board> boardList = boardService.getAllBoards();
        logger.debug("--- データベースから取得したボードの数: " + boardList.size() + " ---");


        // BoardエンティティのリストをBoardDtoのリストに変換
        List<BoardDto> boardDtoList = boardList.stream().map(board -> {
            BoardDto dto = new BoardDto();
            dto.setId(board.getId());
            dto.setUserId(board.getUserId());
            dto.setUserName(userService.findUserNameByUserId(board.getUserId())); // ユーザーIDからユーザー名を取得
            dto.setRegisterDate(board.getRegisterDate());
            dto.setText(board.getText());
            logger.debug("--- ボードID: " + board.getId() + ", 登録日: " + board.getRegisterDate() + " ---");

            return dto;
        }).collect(Collectors.toList());
        
        // 現在のユーザーIDを取得 ログインユーザー表示用
        String currentUserId = securitySession.getUsername();
        
        // ユーザーIDからユーザー名を取得 ログインユーザー表示用
        String userName = userService.findUserNameByUserId(currentUserId);
        
        // ユーザー名をモデルに追加 ログインユーザー表示用
        model.addAttribute("userName", userName);
        model.addAttribute("currentUserId", currentUserId);


        model.addAttribute("boardList", boardDtoList); // DTOのリストをビューに渡す
        model.addAttribute("newPost", new Board());
        model.addAttribute("board", new Board());
        model.addAttribute("currentUserId", securitySession.getUsername());
        return "board";
        
    }//getBoardList
    @PostMapping("/post")
    public String postBoard(@ModelAttribute Board board, Model model) {
        // ここに投稿内容をデータベースに保存するロジックを実装します。
        // 例: boardService.postBoard(board);
        
        // 投稿が成功したら、ボード一覧ページにリダイレクトします。
        return "redirect:/board/";
    }    
    
}
