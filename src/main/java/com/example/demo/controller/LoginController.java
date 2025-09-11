package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.HistoryDto;
import com.example.demo.model.LoginModel;

@Controller
public class LoginController {

	/** メッセージID「1」を指定して、messages.propertiesからメッセージを取得1 */
    private final MessageSource messageSource;

    public LoginController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
	
    // ログインボタン押下時のPOSTマッピング
    @PostMapping("/login")
    public String postLogin() {
        // 基本的にここに直接ロジックを書く必要なし。
        // Spring SecurityがこのURLをインターセプトして認証処理を行う。
        // 認証成功後は、SecurityConfigで設定したデフォルトのURLにリダイレクトされる。            
        // 認証失敗時は、LoginControllerのGETマッピングにリダイレクトされる。
        
        return "redirect:/reservation"; // 認証成功後にリダイレクトするページ
    }
    

    /** ログイン画面を表示 */
    @GetMapping("/login") // RequestMappingアノテーション無しで単独でパス指定可能
    public String getLogin(
    		Model model, 
    		@ModelAttribute LoginModel loginModel,
    		@RequestParam(value = "error", required = false) String error
    		) {
    	
    	//debug
    	System.out.println("error is" + error);
    	
    	// エラーパラメータが存在する場合にのみ、エラーメッセージをModel格納
        if (error != null) {
        	if ("customError".equals(error)) {
                String errorMessage = messageSource.getMessage("1", null, Locale.JAPAN);
                model.addAttribute("errorMessage", errorMessage);
            	//debug
            	System.out.println("errorMessage is" + errorMessage);
                
            }
        }
   	
  	
    	
    	// 画面に表示する履歴の作成
    	List<HistoryDto> historyDtoList = new ArrayList<HistoryDto>();
    	HistoryDto historyDto = new HistoryDto();
    	historyDto.setDateStr("2025/09/01");
    	historyDto.setHistoryText("新規作成");
    	historyDtoList.add(historyDto);
    	historyDto = new HistoryDto();
    	historyDto.setDateStr("2025/09/05");
    	historyDto.setHistoryText("削除ボタン作成");
    	historyDtoList.add(historyDto);
    	historyDto = new HistoryDto();
    	historyDto.setDateStr("2025/09/10");
    	historyDto.setHistoryText("変更ボタン作成");
    	historyDtoList.add(historyDto);
    	model.addAttribute("historyDtoList", historyDtoList);
        return "login";
    }

}   
  
