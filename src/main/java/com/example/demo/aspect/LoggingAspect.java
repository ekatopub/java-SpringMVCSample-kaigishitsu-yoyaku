package com.example.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    /**
     * DBアクセス発生のログを出力するアスペクト
     *
     * ポイントカット: com.example.demo.service.impl.UserDetailsServiceImpl.loadUserByUsername()
     */
	/*	
    @Before("execution(* com.example.demo.service.impl.UserDetailsServiceImpl.loadUserByUsername(..))")
    public void logDbAccess(JoinPoint joinPoint) {
        System.out.println("DBアクセス発生: " + joinPoint.getSignature().toShortString());
    }
    */
	
    @Around("execution(* com.example.demo.repository.*.*(..))")
    public Object logSqlExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        // 呼び出し元のメソッド情報を取得
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String className = signature.getDeclaringTypeName();
        System.out.println("SQL呼び出しクラス.メソッド: " + className + "." + methodName + "()");
    	
    	
    	long startTime = System.currentTimeMillis();
        System.out.println("SQL実行開始: " + joinPoint.getSignature().toShortString());

        // ターゲットメソッドの実行
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("SQL実行完了: " + joinPoint.getSignature().toShortString() + " 実行時間: " + executionTime + "ms");

        return result;
    }
}
    