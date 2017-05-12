package com.tuhanbao.web.log;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by dell on 2016/6/16.
 */
public class LoggerAspect {
    
    /**
     * 日志拦截器
     * @param point
     */
    public Object doAround(ProceedingJoinPoint point){

//        LogInfo info  = new LogInfo();
//        info.setLogClass(point.getTarget().getClass().getName());
//        info.setRemoteFrom("system");
//        info.setRomoteUser("controller");
//        info.setParams(point.getArgs().toString());
//        Object value=null;
//        try {
//            long startTime = System.currentTimeMillis();
//            value = point.proceed();
//            long endTime = System.currentTimeMillis();
//            info.setTimeConsuming(endTime-startTime);
//            info.setRetrunVal(value.toString());
//        } catch (Throwable e) {
//            info.setErrorMsg(e.getMessage());
//        }
//        if(this.service !=null){
//            this.service.logger(info);
//        }else{
//            System.err.println(info.toString());
//        }
//        return value;
    	System.out.println("test tubie!!");
    	return null;
    }
}
