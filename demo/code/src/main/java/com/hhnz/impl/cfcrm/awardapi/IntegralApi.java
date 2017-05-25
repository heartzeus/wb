package com.hhnz.impl.cfcrm.awardapi;

import java.util.TreeMap;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.enums.Api;
import com.hhnz.api.cfcrm.model.cfcrm.AwardItem;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;

public class IntegralApi extends JumuApi {
    private static final String AMOUNT = "amount";
    private static final String CLIENT_ID = "clientId";
    private static final String MERCHANT_ID = "merchantId";
    private static final String ORDER_ID = "orderId";
    private static final String PHONE = "phone";
    
//	protected Map<String, String> initParameters(AwardItem item, VipPrimaryInfo person){
//    	Map<String, String> map = new HashMap<>();
//		map.put(MERCHANT_ID, ConstantsConfig.MERCHANT_ID);
//		map.put(ORDER_ID, getOrderId(item, person));
//		map.put(CLIENT_ID, ConstantsConfig.CLIENT_ID);
//		map.put(PHONE, person.getPhone());
//		map.put(AMOUNT, item.getAwardNum() + "");
//		return map;
//    }

	@Override
	protected String getUrl() {
		return ConstantsConfig.TEST_INTEGRAL_URL + Api.INTEGRAL_SEND;
	}

    @Override
    protected TreeMap<String, String> initParams(AwardItem awardItem, VipPrimaryInfo vipPrimaryInfo) {
        TreeMap<String, String> map = new TreeMap<>();
//        map.put(AMOUNT, awardItem.getAmount());
//        map.put(CLIENT_ID, ConstantsConfig.CLIENT_ID);
//        map.put(MERCHANT_ID, ConstantsConfig.MERCHANT_ID);
//        map.put(ORDER_ID, awardItem.getTradeNo());
//        map.put(PHONE, vipPrimaryInfo.getPhone());
        return map;
    }
}
