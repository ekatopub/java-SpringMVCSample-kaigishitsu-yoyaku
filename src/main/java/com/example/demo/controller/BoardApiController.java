package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;// CORS対策
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // CORS対策

import com.example.demo.dto.BoardDto;
import com.example.demo.model.Board;
import com.example.demo.service.BoardService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecuritySession;

//@Controller
//@RequestMapping("/board")
@RestController // このクラスをRestControllerに変更
@RequestMapping("/api/board") // エンドポイントを /api/board に変更
@CrossOrigin(origins = "http://localhost:8080") // CORS対策
public class BoardApiController {

    private final BoardService boardService;
    private final SecuritySession securitySession;
    private final UserService userService; 

    @Autowired //DI
    public BoardApiController(BoardService boardService, SecuritySession securitySession, UserService userService) {
        this.boardService = boardService;
        this.securitySession = securitySession;
        this.userService = userService;
    }

    /**
     * 新しい投稿を作成
     */
    @PostMapping("/post")
    public BoardDto postBoard(@RequestBody Board board) {
//    public Board postBoard(@RequestBody Board board) {//ajax化のため変更
        // ユーザーIDと日時をセットする
        String userId = securitySession.getUsername();
        board.setUserId(userId);
        //board.setUserId(securitySession.getUsername());　//ajax化のため変更
        board.setRegisterDate(LocalDateTime.now());
        boardService.postBoard(board);// ここでDBに保存する際、ポストIDが自動でセットされる
        //return board;// IDがセットされたオブジェクトを返す　ajax化のため変更
        
        // DTOを作成し、ID、テキスト、日時、ユーザー名をセットして返す
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setUserId(board.getUserId());
        dto.setText(board.getText());
        dto.setRegisterDate(board.getRegisterDate());
        dto.setUserName(userService.findUserNameByUserId(userId)); // ユーザー名を取得してセット

        return dto; // DTOを返す

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") int id) {//HTTPステータスコード（HttpStatus）とレスポンスボディを自由に設定できる
        String currentUserId = securitySession.getUsername();
        boardService.deleteBoardById(id, currentUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // SecurityExceptionが発生した場合にこのメソッドが呼び出される
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);//403 Forbiddenを返す
    }

}