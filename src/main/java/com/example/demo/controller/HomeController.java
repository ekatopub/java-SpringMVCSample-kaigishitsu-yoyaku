package com.example.demo.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;// Modelをインポート
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.GroupOrder;
import com.example.demo.model.MyModel;
import com.example.demo.util.SecuritySession; 

@Controller
@RequestMapping("/form") // 共通的なパス指定
public class HomeController {
	
	//@Autowiredを追加
	//@Autowired
	//@Qualifierを追加 Bean名を指定する　デフォルトでクラス名の最初を小文字にしたもの
	//@Qualifier("printServiceImplProduction")
	//PrintService printService;　//コンストラクタインジェクションでは使用しない

	//コンストラクタインジェクション
//	private final PrintService printService;



	@Autowired
	private SecuritySession securitySession;
	
	@Autowired //for Validation
	private MessageSource messageSource;
	

    // getメソッド（直接リンク含む）でアクセスされた場合の処理
    @GetMapping("") // 個別のパス指定
    public String readForm(Model model, @ModelAttribute MyModel myModel) {
    	
        //セッションからユーザーID取得しModel経由でViewに渡す
        model.addAttribute("loginUserId", securitySession.getUsername());
    	
        return "form"; // 表示するhtmlのパス指定（.html省略）。templatesの
                       // 下のパスを指定。
    
    
    }
 // postメソッドでアクセスされた場合の処理
    @PostMapping("/confirm") // 個別のパス指定
    public String confirm(Model model,
            @ModelAttribute
            @Validated(GroupOrder.class) MyModel myModel, // @Validatedでチェック対象
            BindingResult bindingResult, // バリデーションチェック結果を格納するオブジェクト
            Locale locale) // "en" (英語), "ja"　(日本語)等の地域情報を格納するオブジェクト
    {

    	//バリデーションエラーがある場合にtrue
    	//地域情報を格納するオブジェクト
    	if (bindingResult.hasErrors()) {
    		// Java側でエラーメッセージを取得する場合の記述方法
    		for (FieldError error : bindingResult.getFieldErrors()) {
 			String message = messageSource.getMessage(error,locale);
    		System.out.println("バリデーションエラー : " + message);
    		}

    		//Spring SecurityのセッションからのユーザーIDの再取得
    		model.addAttribute("loginUserId",securitySession.getUsername());

    		// バリデーションエラーの場合は"form"から先に遷移しない
    		return "form";
    	}

    	

    	
    	return "confirm"; // 表示するhtmlのパス指定。
    }
}