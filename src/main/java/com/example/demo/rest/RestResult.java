package com.example.demo.rest;

// RESTでは、int型の結果と任意の型の内容が組み合わさったオブジェクトを返す必要がある
// （そうしない場合は、.doneではなく.failへ制御が移る）
public class RestResult { //REST学習用

    // リターンコード
    private int resultCode;

    // 結果
    private String resultStr;

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

    public String getResultStr() {
        return this.resultStr;
    }
}