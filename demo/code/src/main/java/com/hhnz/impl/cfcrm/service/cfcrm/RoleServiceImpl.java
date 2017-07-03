package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.Role;
import com.hhnz.api.cfcrm.service.cfcrm.IPermissionService;
import com.hhnz.api.cfcrm.service.cfcrm.IRolePermissionService;
import com.hhnz.api.cfcrm.service.cfcrm.IRoleService;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.web.controller.authority.IPermission;
import com.tuhanbao.web.controller.authority.IPermissionManagerService;
import com.tuhanbao.web.controller.authority.IRole;
import com.tuhanbao.web.controller.authority.PermissionManager;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("roleService")
@Transactional("cfcrmTransactionManager")
public class RoleServiceImpl extends ServiceImpl<Role> implements IRoleService, IPermissionManagerService {
    
    @Autowired
    private IRolePermissionService rpService;

    @Autowired
    private IPermissionService permissionService;
    
    @Autowired
    private PermissionManager permissionManager;
    
    private static final MyBatisSelector SELECTOR = new MyBatisSelector(TableConstants.T_ROLE.TABLE);
    
    static {
        SELECTOR.joinTable(TableConstants.T_ROLE_PERMISSION.TABLE).joinTable(TableConstants.T_PERMISSION.TABLE);
    }
    
    @SuppressWarnings("unchecked")
    public List<IRole> getAllRoles() {
        List<?> roles = this.select(SELECTOR, null);
        return (List<IRole>)roles;
    }

    @SuppressWarnings("unchecked")
    public List<IPermission> getAllPermissions() {
        List<?> permissions = permissionService.select(null);
        return (List<IPermission>)permissions;
    }

    @Override
    public void addPermission(long roleId, IPermission permission) {
        rpService.addRolePermission(roleId, permission.getId());
    }

    @Override
    public void removePermission(long roleId, IPermission permission) {
        rpService.removeRolePermission(roleId, permission.getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createOrUpdateRole(Role role, List<Long> permissionIds) {        
        if (role.getId() == 0) {
            add(role);
        }
        else {
            //当含有roleId的时候表示为更新角色，此时添加权限的时候需要先删除之前保存的权限，重新添加所有的新的权限
            update(role);
        }
        //为null表示不更新权限
        if (permissionIds == null) {
            return;
        }
        else {
            List<?> list = null;
            if (permissionIds.isEmpty()) {
                list = new ArrayList<IPermission>();
            }
            else {
                Filter filter = new Filter().andFilter(TableConstants.T_PERMISSION.ID, permissionIds);
                list = permissionService.select(filter);
            }
            permissionManager.updatePermission(role.getId(), (List<IPermission>)list);
        }
    }

    @Override
    public void deleteRole(long roleId) {
        Role role = this.selectById(roleId);
        if (role == null) {
            throw new MyException(ErrorCode.NO_ROLE);
        }
        
        permissionManager.deleteRole(roleId);
        this.deleteById(roleId);
    }

    @Override
    public void deletePermission(IPermission permission) {
        permissionService.deletePermission(permission.getId());
    }
}