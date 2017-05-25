package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.Role;
import com.tuhanbao.web.service.IService;

public interface IRoleService extends IService<Role> {
    void createOrUpdateRole(Role role, List<Long> permissionIds);

    void deleteRole(long roleId);
}