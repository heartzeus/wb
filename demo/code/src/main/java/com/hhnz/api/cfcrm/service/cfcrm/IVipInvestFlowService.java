package com.hhnz.api.cfcrm.service.cfcrm;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.VipInvestFlow;
import com.tuhanbao.web.service.IService;

public interface IVipInvestFlowService extends IService<VipInvestFlow> {
    public void importInvestFlow(List<VipInvestFlow> flows);

	void importInvestInfo(InputStream is);

    int calculateDailyDescreasement(int customId, long dayInvestBalance, long dayAccountBalance, int[] ymd) throws ParseException;


}