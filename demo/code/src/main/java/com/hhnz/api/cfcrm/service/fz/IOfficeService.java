package com.hhnz.api.cfcrm.service.fz;

import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.model.fz.Office;
import com.tuhanbao.web.service.IService;

public interface IOfficeService extends IService<Office> {

    void getTypeAndAreaById(VipPrimaryInfo vipInfo);
}