package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IVipInvestInfoService extends IService<VipInvestInfo> {
	int getVipDailyDesUpSomePercent();

    public List<VipInvestInfo> selectInvestInfo(Filter filter);
}