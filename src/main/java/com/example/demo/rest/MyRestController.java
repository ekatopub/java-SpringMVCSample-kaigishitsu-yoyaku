package com.example.demo.rest;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.MyModel;

@RestController
@RequestMapping("/form")
public class MyRestController {  //REST学習用　ただ表示するだけ

    @PostMapping("/rest")
    public RestResult restForm(@ModelAttribute MyModel myModel) {

        // REST用の型であるRestResult型で結果を返却
        RestResult restResult = new RestResult();
        restResult.setResultCode(0);
        restResult.setResultStr("入力された文字: " + myModel.getMessage());
        return restResult;
    }
}