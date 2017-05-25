package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.User;
import com.hhnz.api.cfcrm.service.cfcrm.IUserService;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.io.MD5Util;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.controller.authority.TokenService;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("userService")
@Transactional("cfcrmTransactionManager")
public class UserServiceImpl extends ServiceImpl<User> implements IUserService {
    private static final MyBatisSelector USER_ROLE_SELECTOR = new MyBatisSelector(TableConstants.T_USER.TABLE);
    
    static {
        USER_ROLE_SELECTOR.joinTable(TableConstants.T_ROLE.TABLE).joinTable(TableConstants.T_ROLE_PERMISSION.TABLE)
            .joinTable(TableConstants.T_PERMISSION.TABLE);
    }

    @Override
    public User login(String name, String password) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_USER.LOGIN_NAME, name);
        List<User> userLi = select(USER_ROLE_SELECTOR, filter);
        if (userLi.isEmpty()) {
            throw new MyException(ErrorCode.USER_ISNOT_EXIST);
        }
        User user = userLi.get(0);
        if (!MD5Util.getMD5String(password).equals(user.getPassword())) {
            throw new MyException(ErrorCode.PASSWORD_WRONG);
        }
        
        TokenService.getInstance().add(user);
        return user;
    }

    @Override
    public List<User> getUserList(Filter filter) {
        return select(USER_ROLE_SELECTOR, filter);
    }

    @Override
    public User getUserDetail(long userId) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_USER.ID, userId);
        List<User> userLi = select(USER_ROLE_SELECTOR, filter);
        if (userLi.isEmpty()) {
            throw new MyException(ErrorCode.USER_ISNOT_EXIST);
        }
        return userLi.get(0);
    }
}