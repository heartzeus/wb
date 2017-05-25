package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.SendHistoryVip;
import com.tuhanbao.web.service.IService;

public interface ISendHistoryVipService extends IService<SendHistoryVip> {
    public void deleteAll(List<Long> ids);
}