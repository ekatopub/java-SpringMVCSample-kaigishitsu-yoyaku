package com.example.demo.controller;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Board;
import com.example.demo.model.GroupOrder;
import com.example.demo.service.BoardService;

@RestController
@RequestMapping("/api/board")
public class BoardRestController {//入力内容を受け取ってバリデーションを実行し、結果をJSON形式で返す
	
    private final BoardService boardService;
    private final MessageSource messageSource; // MessageSourceをフィールドとして定義
    
    // コンストラクタを追加してBoardServiceを注入
    @Autowired
    public BoardRestController(BoardService boardService, MessageSource messageSource) {
        this.boardService = boardService;
        this.messageSource = messageSource;
    }


    @PostMapping("/validate")
    public ResponseEntity<?> validateBoard(
    		@RequestBody//戻り値を直接HTTPレスポンスボディとして扱い、JSONに自動変換
    		//@Validated Board board,//GroupOderは使わなくなったので削除
    		@Validated(GroupOrder.class) Board board,//グループ順を使う
    		BindingResult bindingResult)
    	{ //エラーが含まれている場合、エラー情報をJSON形式でクライアントに返す
    	System.out.println("validateBoard is called");
    	
        // デバッグログの追加
        System.out.println("validateBoard is called");
        System.out.println("受信したBoardオブジェクトのtextフィールド: '" + board.getText() + "'");
    	
    	if (bindingResult.hasErrors()) {
        	System.out.println("bindingResult.hasErrors");
        	
        	Locale currentLocale = LocaleContextHolder.getLocale(); // 現在のロケールを取得
        	
        	
        	
        	// エラー情報をMapにまとめてJSONとして返す
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                    		FieldError::getField,
                    //		FieldError::getDefaultMessage,
                    		// messageSourceを使用してメッセージを解決
                            error -> messageSource.getMessage(error, currentLocale), 
                    		(exstingValue, newValue) -> exstingValue + "," + newValue // 重複した値を結合する
                    		));
            return ResponseEntity.badRequest().body(errors);
        }
        
        return ResponseEntity.ok().build();
    }
}