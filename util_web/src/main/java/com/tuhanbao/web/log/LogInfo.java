package com.tuhanbao.web.log;

/**
 * 日志对象
 * Created by dell on 2016/6/16.
 */
public class LogInfo {
    /**
     * 日志记录所属类
     */
    private String logClass;
    /**
     * 调用方法
     */
    private String logMethod;
    /**
     * 执行时间
     */
    private long timeConsuming;
    /**
     * 请求参数
     */
    private String params;
    /**
     * 返回信息
     */
    private String retrunVal;
    /**
     * 调用来源
     */
    private String remoteFrom;
    /**
     * 调用用户
     */
    private String romoteUser;
    /**
     * 错误信息
     */
    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getLogClass() {
        return logClass;
    }

    public void setLogClass(String logClass) {
        this.logClass = logClass;
    }

    public String getLogMethod() {
        return logMethod;
    }

    public void setLogMethod(String logMethod) {
        this.logMethod = logMethod;
    }

    public long getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(long timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRetrunVal() {
        return retrunVal;
    }

    public void setRetrunVal(String retrunVal) {
        this.retrunVal = retrunVal;
    }

    public String getRemoteFrom() {
        return remoteFrom;
    }

    public void setRemoteFrom(String remoteFrom) {
        this.remoteFrom = remoteFrom;
    }

    public String getRomoteUser() {
        return romoteUser;
    }

    public void setRomoteUser(String romoteUser) {
        this.romoteUser = romoteUser;
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "logClass='" + logClass + '\'' +
                ", logMethod='" + logMethod + '\'' +
                ", timeConsuming=" + timeConsuming +
                ", params='" + params + '\'' +
                ", retrunVal='" + retrunVal + '\'' +
                ", remoteFrom='" + remoteFrom + '\'' +
                ", romoteUser='" + romoteUser + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
