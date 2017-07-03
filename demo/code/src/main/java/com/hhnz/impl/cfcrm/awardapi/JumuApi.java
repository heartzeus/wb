package com.hhnz.impl.cfcrm.awardapi;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.hhnz.api.cfcrm.model.cfcrm.AwardItem;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.tuhanbao.base.Constants;
import com.tuhanbao.base.util.http.HttpSendUtil;
import com.tuhanbao.base.util.log.LogManager;

public abstract class JumuApi implements IAwardApi {

//	@Override
//	public boolean sendAward(List<AwardVip> persons, AwardItem item) {
//		String url = getUrl();
//		
//		boolean sendResult = true;
//		for (AwardVip vip : persons) {
//			try {
//				Map<String, String> params = initParameters(item, vip.getCustom());
//				params.put("sign", AwardApiManager.getSign(params, item));
//				LogManager.info(map2String(params));
//				HttpSendUtil.send(url, "POST", params, null);
//			} catch (IOException e) {
//				LogManager.info("POST请求 请求路径:" + url + "请求失败");
//				sendResult = false;
//			}
//		}
//		return sendResult;
//	}
	
	@Override
	public boolean sendAward(VipPrimaryInfo vipPrimaryInfo, AwardItem awardItem) {
	    String url = getUrl();
	    
	    boolean sendResult = true;
        try {
            TreeMap<String, String> params = initParams(awardItem, vipPrimaryInfo);
            // getSing方法rewrite
            params.put("sign", AwardApiManager.getSing4Jumoo(params, awardItem));
            LogManager.info(map2String(params));
            HttpSendUtil.send(url, "POST", params, null);
        }
        catch (IOException e) {
            LogManager.info("POST请求 请求路径:" + url + "请求失败");
            sendResult = false;
        }
	    
	    return sendResult;
	    
	}

	protected abstract String getUrl();
	
//	protected abstract Map<String, String> initParameters(AwardItem item, VipPrimaryInfo person);
	
	protected abstract TreeMap<String, String> initParams(AwardItem awardItem, VipPrimaryInfo vipPrimaryInfo);
	
//	protected String getOrderId(AwardItem item, VipPrimaryInfo person) {
//		return item.getId() + "_" + person.getId();
//	}
	
	protected String map2String(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append(Constants.COLON).append(entry.getValue()).append(Constants.COMMA);
		}
		return sb.toString();
	}

}
