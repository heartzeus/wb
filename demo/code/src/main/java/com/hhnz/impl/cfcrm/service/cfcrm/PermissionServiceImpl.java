package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.Permission;
import com.hhnz.api.cfcrm.service.cfcrm.IPermissionService;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("permissionService")
@Transactional("cfcrmTransactionManager")
public class PermissionServiceImpl extends ServiceImpl<Permission> implements IPermissionService {

    private static MyBatisSelector SELECTOR = new MyBatisSelector(TableConstants.T_PERMISSION.TABLE, "T1");
    
    static {
        SELECTOR.joinTable(TableConstants.T_PERMISSION.TABLE, "T2");
    }
    
    @Override
    public void deletePermission(long permissionId) {
        //先检查权限本身和是否存在下级权限
        List<Permission> permissions= this.select(SELECTOR, new Filter().andFilter(SELECTOR.getTable().getColumn(TableConstants.T_PERMISSION.ID), permissionId));
        if (permissions.isEmpty()) {
            throw new MyException(ErrorCode.PERMISSION_NOT_EXIST);
        }
        if (permissions.get(0).getPermissions() != null && !permissions.get(0).getPermissions().isEmpty()) {
            throw new MyException(ErrorCode.EXIST_CHILD_PERMISSION);
        }
        
        this.deleteById(permissionId);
    }
}