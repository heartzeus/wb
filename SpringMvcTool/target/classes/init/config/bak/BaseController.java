/**
 * 
 */
package {projectHead}.controller.{projectName}.controller;

import javax.servlet.http.HttpServletRequest;

import com.tuhanbao.util.exception.BaseErrorCode;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.controller.authority.IUser;
import com.tuhanbao.web.controller.authority.TokenService;
import com.tuhanbao.web.controller.helper.JsonHttpMessageConverter;

/**
 * 2016年10月24日
 * @author liuhanhui
 */
public class BaseController {
    public static final String GET = "/get";
    
    public static final String UPDATE = "/update";
    
    public static final String ADD = "/add";
    
    public static final String delete = "/delete";
    
    protected static final Object NULL = JsonHttpMessageConverter.NULL;
    
    
    public IUser getCurrentUser(HttpServletRequest request){
        IUser user = TokenService.getInstance().getCurrentUser(request);
        if (user == null) throw new MyException(BaseErrorCode.PLEASE_LOGIN_FIRST);
        return user;
    }
    
}
