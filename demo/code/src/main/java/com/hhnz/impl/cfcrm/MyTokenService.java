package com.hhnz.impl.cfcrm;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.hhnz.api.cfcrm.model.cfcrm.User;
import com.tuhanbao.web.controller.authority.IUser;
import com.tuhanbao.web.controller.authority.TokenService;

/**
 * 增加了一些自定义实现
 * @author Administrator
 *
 */
@Service
public class MyTokenService extends TokenService {
    public IUser getUserByToken(String token) {
        return getUserByToken(token, User.class);
    }
    
    protected String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie item : cookies) {
                if (TokenService.REQ_KEY_TOKEN.equals(item.getName())) {
                    return item.getValue();
                }
            }
        }
        return null;
    }
}
