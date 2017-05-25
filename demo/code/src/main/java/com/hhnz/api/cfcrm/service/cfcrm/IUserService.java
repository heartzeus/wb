package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.User;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IUserService extends IService<User> {
    User login(String name, String password);

    List<User> getUserList(Filter filter);

    User getUserDetail(long userId);
}