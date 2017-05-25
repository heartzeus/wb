package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.SendToVip;
import com.tuhanbao.web.service.IService;

public interface ISendToVipService extends IService<SendToVip> {
    public void deleteAll(List<Long> ids);
}