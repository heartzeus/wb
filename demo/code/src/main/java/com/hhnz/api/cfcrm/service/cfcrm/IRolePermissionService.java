package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.RolePermission;
import com.tuhanbao.web.service.IService;

public interface IRolePermissionService extends IService<RolePermission> {
    void addRolePermission(long roleId, long permissionId);

    void removeRolePermission(long roleId, long permissionId);
    
    List<RolePermission> getAllAuthByRoleId(long roleId);
}