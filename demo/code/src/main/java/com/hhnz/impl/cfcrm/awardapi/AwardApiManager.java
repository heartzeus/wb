package com.hhnz.impl.cfcrm.awardapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.enums.AwardType;
import com.hhnz.api.cfcrm.model.cfcrm.AwardItem;
import com.tuhanbao.io.MD5Util;
import com.tuhanbao.util.exception.MyException;

public class AwardApiManager {
    private static final Map<AwardType, IAwardApi> APIS = new HashMap<AwardType, IAwardApi>();
    static {
        register(AwardType.NONGZHUANG_INTEGRAL, new IntegralApi());
        register(AwardType.COUPON, new CouponApi());
    }
    
    public static void register(AwardType at, IAwardApi api) {
        APIS.put(at, api);
    }
    
    public static IAwardApi getAwardApi(AwardType at) {
        IAwardApi api = APIS.get(at);
        if (api == null) {
        	throw new MyException("dont support this award type : " + at.name());
        }
        return api;
    }

    // TODO Need to rewrite.
//    public static boolean sendAwardItem(VipPrimaryInfo vipPrimaryInfo, AwardItem item) {
//        AwardType at = item.getAwardType();
//        IAwardApi api = getAwardApi(at);
//        if (api == null) {
//            throw new MyException("have not impl api for " + at.name());
//        }
//        
//        return api.sendAward(vipPrimaryInfo, item);
//    }
    
//    /**
//     * 生成签名方法 根据规则
//     * 如果是积分，则返回小写
//     * 如果是理财券，则返回32位大写
//     * @author gzh
//     * @param params
//     * @param awardItem
//     * @return
//     */
//    public static final String getSign(Map<String, String> params, AwardItem awardItem) {
//    	StringBuilder sb = new StringBuilder();
//    	sb.append(ConstantsConfig.CLIENT_SECRET);
//    	TreeMap<String, String> treeMap = new TreeMap<String, String>(params);
//    	for (Entry<String, String> entry : treeMap.entrySet()) {
//    		sb.append(entry.getKey()).append(entry.getValue());
//    	}
//    	sb.append(ConstantsConfig.CLIENT_SECRET);
//    	if (awardItem.getAwardType() == AwardType.NONGZHUANG_INTEGRAL) { 
//    		return MD5Util.getMD5String(sb.toString());
//		}else {}
//	    	return MD5Util.getMD5String(sb.toString()).toUpperCase();
//		
//    }
    
    /**
     * 根据巨木所给接口，
     * 生成sign方法
     * @param params
     * @param awardItem
     * @return
     */
    public static final String getSing4Jumoo(TreeMap<String, String> params, AwardItem awardItem) {
        StringBuilder sign = new StringBuilder();
        String str = "";
        // 暂时用这种判断条件来区分是积分奖励还是理财券奖励
        // 判断不为空，为理财券接口
        if (params.get("couponGroupId") != null && params.get("actionId") != null) {
            sign.append(ConstantsConfig.SECRET);
            for (Entry<String, String> entry : params.entrySet()) {
                sign.append(entry.getKey()).append(entry.getValue());
            }
            sign.append(ConstantsConfig.SECRET);
            str = MD5Util.getMD5String(sign.toString()).toUpperCase();
        }else {
            sign.append(ConstantsConfig.CLIENT_SECRET);
            for (Entry<String, String> entry : params.entrySet()) {
                sign.append(entry.getKey()).append(entry.getValue());
            }
            sign.append(ConstantsConfig.CLIENT_SECRET);
            str = MD5Util.getMD5String(sign.toString());
        }
        return str;
        
    }
    
}
