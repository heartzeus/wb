package com.hhnz.impl.cfcrm.awardapi;

import java.util.TreeMap;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.enums.Api;
import com.hhnz.api.cfcrm.model.cfcrm.AwardItem;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;

public class CouponApi extends JumuApi {
    private static final String USER_ID = "userId";
    private static final String MOBILE = "mobile";
    private static final String ACTION_ID = "actionId";
    private static final String COUPON_GROUP_ID = "couponGroupId";
    private static final String SITE_ID = "siteId";
	
//	protected Map<String, String> initParameters(AwardItem item, VipPrimaryInfo person){
//    	Map<String, String> map = new HashMap<>();
//    	String userId = person.getNongzhuangId() + "";
//		map.put(USER_ID, userId);
//		map.put(MOBILE, person.getPhone());
//		map.put(ACTION_ID, getOrderId(item, person));
//		map.put(COUPON_GROUP_ID, getCouponGroupId(item));
//		map.put(SITE_ID, ConstantsConfig.SITE_ID);
//		return map;
//    }
	
//	private String getCouponGroupId(AwardItem item) {
//		 couponGroupId信息暂时不知道
//		return ConstantsConfig.COUPON_GROUP_ID;
//	}

	@Override
	protected String getUrl() {
		return ConstantsConfig.TEST_COUPON_URL + Api.COUPON_SEND;
	}

    @Override
    protected TreeMap<String, String> initParams(AwardItem awardItem, VipPrimaryInfo vipPrimaryInfo) {
        TreeMap<String, String> map = new TreeMap<>();
//        map.put(ACTION_ID, awardItem.getTradeNo());
//        map.put(COUPON_GROUP_ID, awardItem.getCouponGroupId());
//        map.put(MOBILE, vipPrimaryInfo.getPhone());
//        map.put(SITE_ID, ConstantsConfig.SITE_ID);
//        map.put(USER_ID, awardItem.getUserId());
        return map;
    }

}
