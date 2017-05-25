package com.hhnz.api.cfcrm.service.cfcrm;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.hhnz.api.cfcrm.model.cfcrm.VipInvestFlow;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IVipPrimaryInfoService extends IService<VipPrimaryInfo> {
    public List<VipPrimaryInfo> selectCustomReminder(Filter filter);

    public List<VipPrimaryInfo> selectInvestInfo(Filter filter);
    
    public List<VipPrimaryInfo> selectAwardInfo(Filter filter);

    int getVipAmountOnBirthday();
    
    List<VipPrimaryInfo> selectByAutoFilter(Filter filter);

    public void createNewCustom(VipPrimaryInfo vipPrimaryInfo);
    
    public void deleteCustom(Long id);
    
    public void importCustomData(List<VipPrimaryInfo> customs);
    
    public VipInvestInfo getVipInvestInfo(int year, int month, long customId);
    
	Map<String, Object> getCtPrMapByDsId(String dsId);
	
    public void updateInvestInfo(List<VipInvestFlow> vipInvestFlows);

	void importCustomInfo(InputStream is);

}