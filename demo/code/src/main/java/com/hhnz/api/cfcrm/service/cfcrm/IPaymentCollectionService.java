package com.hhnz.api.cfcrm.service.cfcrm;

import java.io.InputStream;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.hhnz.api.cfcrm.model.cfcrm.PaymentCollection;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IPaymentCollectionService extends IService<PaymentCollection> {

    public JSONObject getVipsAndPaymentBackAmount();

    public void importPaymentCollection(List<PaymentCollection> paymentCollections);

	void importBackMoneyInfo(InputStream is);
	
    public List<PaymentCollection> selectCustomReminder(Filter filter);


}