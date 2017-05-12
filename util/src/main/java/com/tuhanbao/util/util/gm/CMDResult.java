package com.tuhanbao.util.util.gm;

////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //  
//                         o8888888o                              //      
//                         88" . "88                              //      
//                         (| ^_^ |)                              //      
//                         O\  =  /O                              //  
//                      ____/`---'\____                           //                          
//                    .'  \\|     |//  `.                         //  
//                   /  \\|||  :  |||//  \                        //      
//                  /  _||||| -:- |||||-  \                       //  
//                  |   | \\\  -  /// |   |                       //  
//                  | \_|  ''\---/''  |   |                       //          
//                  \  .-\__  `-`  ___/-. /                       //          
//                ___`. .'  /--.--\  `. . ___                     //      
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //  
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //      
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //  
//      ========`-.____`-.___\_____/___.-`____.-'========         //      
//                           `=---='                              //  
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //  
//                  佛祖保佑            永无BUG   永不修改                                                        //  
////////////////////////////////////////////////////////////////////
import com.tuhanbao.io.base.Constants;

public class CMDResult
{
    private int code;
    
    private String errorMsg;
    
    private String result;
    
    public static final String EXIT_CODE = "exitCode";
    
    public static final String ERROR = "error";
    
    public static final String RESULT = "result";

    public CMDResult(int code)
    {
        super();
        this.code = code;
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(EXIT_CODE).append(Constants.COLON).append(code).append(Constants.ENTER);
        sb.append(RESULT).append(Constants.COLON).append(result).append(Constants.ENTER);
        sb.append(ERROR).append(Constants.COLON).append(errorMsg).append(Constants.ENTER);
        return sb.toString();
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }
}
