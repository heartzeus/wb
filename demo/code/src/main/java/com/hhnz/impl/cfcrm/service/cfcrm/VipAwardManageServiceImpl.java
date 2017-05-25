package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.LanguageResource;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.Api;
import com.hhnz.api.cfcrm.constants.enums.AwardType;
import com.hhnz.api.cfcrm.constants.enums.SendStatus;
import com.hhnz.api.cfcrm.constants.enums.SendType;
import com.hhnz.api.cfcrm.model.cfcrm.AwardItem;
import com.hhnz.api.cfcrm.model.cfcrm.AwardItemInfo;
import com.hhnz.api.cfcrm.model.cfcrm.AwardVip;
import com.hhnz.api.cfcrm.model.cfcrm.VipAwardManage;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IAwardItemService;
import com.hhnz.api.cfcrm.service.cfcrm.IAwardVipService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipAwardManageService;
import com.hhnz.api.cfcrm.tool.HttpRequest;
import com.hhnz.impl.cfcrm.AutoSendAwardTask;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.io.MD5Util;
import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.objutil.Duration;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.thirdapi.cache.AbstractCacheKey;
import com.tuhanbao.thirdapi.cache.CacheManager;
import com.tuhanbao.thirdapi.cache.ICacheKey;
import com.tuhanbao.util.ResourceManager;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.util.log.LogManager;
import com.tuhanbao.util.thread.ScheduledThreadManager;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("vipAwardManageService")
@Transactional("cfcrmTransactionManager")
public class VipAwardManageServiceImpl extends ServiceImpl<VipAwardManage> implements IVipAwardManageService {

    private static MyBatisSelector VIP_AWARD_SELECTOR = new MyBatisSelector(TableConstants.T_VIP_AWARD_MANAGE.TABLE);    

    @Autowired
    private IAwardItemService iAwardItemService;

    @Autowired
    private IAwardVipService iAwardVipService;

    private static Map<AwardType, Map<Integer, AwardItemInfo>> AWARD_ITEM_MAP = null;
    
    private static final String ACTION_ID = "actionId";
    
    private static final String INTEGRAL_TYPE_NAME = "-1";

    private static final long COUPON_NUM = 1L;
    
    private static final String JUMOO = "jumu";
    
    private static ICacheKey CK = new AbstractCacheKey() {
        
        @Override
        public boolean isExpire() {
            return true;
        }
        
        @Override
        public String getName() {
            return "jumooAward";
        }
        
        @Override
        public int getExpireTime() {
            return 6 * 3600;
        }
    };


    static {
        // VIP_AWARD_SELECTOR.joinTable(TableConstants.T_AWARD_ITEM.TABLE);
        VIP_AWARD_SELECTOR.joinTable(TableConstants.T_AWARD_VIP.TABLE).joinTable(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    }

    @Override
    public List<VipAwardManage> selectVipAward(Filter filter) {
        List<VipAwardManage> list = this.select(VIP_AWARD_SELECTOR, filter);

        for (VipAwardManage vipAwardManage : list) {
            List<AwardItem> lItems = iAwardItemService.select(new Filter().andFilter(TableConstants.T_AWARD_ITEM.AWARD_ID, vipAwardManage.getId()));
            StringBuilder awardItemContent = new StringBuilder();
            for (AwardItem awardItem : lItems) {
                // 领取   20积分
                if (INTEGRAL_TYPE_NAME.equals(awardItem.getAwardTypeName())) {
                    awardItemContent.append(awardItem.getAwardNum()).append(awardItem.getAwardContent()).append(Constants.COMMA);
                }
                else {
                    awardItemContent.append(awardItem.getAwardContent()).append(Constants.COMMA);
                }
            }
            if (awardItemContent.length() > 0) {
                awardItemContent.deleteCharAt(awardItemContent.length() - 1);
            }
            vipAwardManage.setAwardItemContent(awardItemContent.toString());
        }

        return list;
    }


    @Override
    public void sendAward(List<VipPrimaryInfo> persons, int amount, String couponGroupId, SendType sendType, String awardName, Date sendAwardDate, long userId) {
        // 存数据库start
        VipAwardManage vipAwardManage = new VipAwardManage();

        Map awardMap = (Map)getJumuAward();
        
        Date currentDate = new Date();
        if (sendType == SendType.IMMEDIATE_SEND) {
            vipAwardManage.setAwardDate(currentDate);
        }
        else {
            vipAwardManage.setAwardDate(sendAwardDate);
        }
        vipAwardManage.setAwardName(awardName);
        vipAwardManage.setUserId(userId);
        vipAwardManage.setCreatingTime(currentDate);
        vipAwardManage.setSendStatus(SendStatus.TO_SEND);
        vipAwardManage.setSendType(sendType);
        this.add(vipAwardManage);

        // 客户奖励发放记录
        for (VipPrimaryInfo vipPrimaryInfo : persons) {
            AwardVip awardVip = new AwardVip();
            awardVip.setAwardId(vipAwardManage.getId());
            awardVip.setCustomId(vipPrimaryInfo.getId());
            awardVip.setSendStatus(SendStatus.TO_SEND);
            awardVip.setSendType(sendType);
            iAwardVipService.add(awardVip);
        }
        AwardItem awardItemIntegral = new AwardItem();
        awardItemIntegral.setAwardId(vipAwardManage.getId());
        awardItemIntegral.setAwardTypeName(INTEGRAL_TYPE_NAME);
        awardItemIntegral.setAwardNum((long)amount); 
        awardItemIntegral.setAwardContent(ResourceManager.getResource(LanguageResource.INTEGRAL));
        iAwardItemService.add(awardItemIntegral);
        
        AwardItem awardItemCoupon = new AwardItem();
        awardItemCoupon.setAwardId(vipAwardManage.getId());
        awardItemCoupon.setAwardTypeName(couponGroupId);
        awardItemCoupon.setAwardNum(COUPON_NUM);        
        // TODO 检查是否有bug
        awardItemCoupon.setAwardContent(awardMap.get(couponGroupId).toString());
        iAwardItemService.add(awardItemCoupon);

        List<AwardItem> items = new ArrayList<AwardItem>();
        items.add(awardItemIntegral);
        items.add(awardItemCoupon);
        // 立即发送和当天预约发送的sendDate都为今天的0点0分0秒
        int[] ymd = TimeUtil.getYearMonthDayHour(vipAwardManage.getAwardDate().getTime());
        long sendDate = TimeUtil.getTime(ymd[0], ymd[1], ymd[2], 0, 0, 0);
        if (sendDate == TimeUtil.getTodayTime(0, 0, 0)) {
            vipAwardManage.setAwardItems(items);
            sendAward(vipAwardManage);
        }
    }
    
    private Object getJumuAwardInitImpl() {
        JSONObject responseJsonObj = new JSONObject();
        String jsonResponse = "";        
        String url = ConstantsConfig.TEST_COUPON_URL + Api.GET_COUPON.value;
        LogManager.info("获取奖励券请求地址: " + url);
        // 调用巨木获取奖励接口，获得奖励项
        try {
            jsonResponse = HttpRequest.sendPost(url, new HashMap<String, Object>());
            LogManager.info("请求返回字符串: " + jsonResponse);
            responseJsonObj = JSONObject.parseObject(jsonResponse);
        }
        catch (Exception e) {
            throw new MyException(ErrorCode.NETWORK_LINK_ERROR);
        }
        CacheManager.set(CK, JUMOO, responseJsonObj.get("info"));

        return responseJsonObj;
    }
    
    @Override
    public Object getJumuAward() {
        // 从缓存取，去到了直接返回
        Object object = CacheManager.get(CK, JUMOO);
        // 缓存没有 ,从jumu掉接口获取，然后放到缓存，返回结果本身
        if (object == null) {
            object = getJumuAwardInitImpl();
        }        
        return object;
    }
    
    private static class awardBean{
        public String getCouponGroupName() {
            return couponGroupName;
        }
        public void setCouponGroupName(String couponGroupName) {
            this.couponGroupName = couponGroupName;
        }
        public String getCouponGroupId() {
            return couponGroupId;
        }
        public void setCouponGroupId(String couponGroupId) {
            this.couponGroupId = couponGroupId;
        }
        String couponGroupName;
        String couponGroupId;
    }

    @Override
    public void sendAward(VipAwardManage award) {
        long time = TimeUtil.now();
        long sendTime = award.getAwardDate().getTime();

        long delay = sendTime > time ? sendTime - time : 0;
        AutoSendAwardTask task = new AutoSendAwardTask(award, this);
        if (delay == 0) {
            ScheduledThreadManager.execute(task);
        }
        else {
            ScheduledThreadManager.executeAfterTime(delay, task);
        }
    }

    /**
     * @param time
     * @return
     */
    @Override
    public List<VipAwardManage> selectAllNeedSendAward(long time) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_AWARD_MANAGE.SEND_STATUS, SendStatus.TO_SEND);
        Duration duration = Duration.getDay(time);
        filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, duration.getStartTime());
        filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, duration.getEndTime());
        List<VipAwardManage> list = this.select(VIP_AWARD_SELECTOR, filter);
        return list;
    }

    @Override
    public void updateManager(VipAwardManage vam, List<AwardVip> vips) {
        this.update(vam);

        for (AwardVip vip : vips) {
            iAwardVipService.update(vip);
        }
    }
    
//    @Override
//    public boolean sendAwardGzhWritten(AwardItem awardItem, VipPrimaryInfo vipPrimaryInfo){
//        // 将网信奖励发放记录存入到数据库 T_VIP_AWARD_MANAGE 表中 其他表还待确认
//        Date currentDate = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
//        Date orderSendDate = new Date();
//        try {
//            orderSendDate = sdf.parse(awardItem.getOrderDate());
//        }
//        catch (ParseException e) {
//            throw new MyException();
//        }  
//        SendType st = SendType.getSendType(awardItem.getSendType());
//        VipAwardManage vipAwardManageWangXn = new VipAwardManage();
//        if (st == SendType.IMMEDIATE_SEND) {
//            vipAwardManageWangXn.setAwardDate(currentDate);
//        }
//        else {
//            // 预约发送
//            vipAwardManageWangXn.setAwardDate(orderSendDate);
//        }
//        vipAwardManageWangXn.setAwardName(awardItem.getAwardName()); // 奖励名称
//        vipAwardManageWangXn.setUserId(awardItem.getAdminId()); // 创建人id
//        vipAwardManageWangXn.setCreatingTime(currentDate); // 记录创建时间
//        vipAwardManageWangXn.setSendStatus(SendStatus.TO_SEND); // 发送状态
//        vipAwardManageWangXn.setSendType(st); // 发送类型
//        this.add(vipAwardManageWangXn);
//        
//        // 记录到表T_AWARD_VIP
//        AwardVip awardVipWngXn = new AwardVip();
//        awardVipWngXn.setCustomId(vipPrimaryInfo.getId());
//        awardVipWngXn.setAwardId(vipAwardManageWangXn.getId());
//        awardVipWngXn.setSendStatus(vipAwardManageWangXn.getSendStatus());
//        awardVipWngXn.setSendType(vipAwardManageWangXn.getSendType());
//        iAwardVipService.add(awardVipWngXn);
//        
//        // 记录到T_AWARD_ITEM 奖励数量暂时写死为1
//        AwardItem awardItemWngXn = new AwardItem();
//        awardItemWngXn.setAwardId(vipAwardManageWangXn.getId());
//        awardItemWngXn.setAwardNum(1);
//        iAwardItemService.add(awardItemWngXn);
//        
//        // 将巨木积分奖励发放记录存入到数据库 T_VIP_AWARD_MANAGE 表中 
//        VipAwardManage vipAwardManageJumu = new VipAwardManage();
//        if (st == SendType.IMMEDIATE_SEND) {
//            vipAwardManageJumu.setAwardDate(currentDate);
//        }
//        else {
//            // 预约发送
//            vipAwardManageJumu.setAwardDate(orderSendDate);
//        }
//        vipAwardManageJumu.setAwardName(awardItem.getAwardName()); // 奖励名称
//        vipAwardManageJumu.setUserId(awardItem.getAdminId()); // 创建人id
//        vipAwardManageJumu.setCreatingTime(currentDate); // 记录创建时间
//        vipAwardManageJumu.setSendStatus(SendStatus.TO_SEND); // 发送状态
//        vipAwardManageJumu.setSendType(st); // 发送类型
//        this.add(vipAwardManageJumu);
//        
//        // 记录到表T_AWARD_VIP
//        AwardVip awardVipJumu = new AwardVip();
//        awardVipJumu.setCustomId(vipPrimaryInfo.getId());
//        awardVipJumu.setAwardId(vipAwardManageJumu.getId());
//        awardVipJumu.setSendStatus(vipAwardManageJumu.getSendStatus());
//        awardVipJumu.setSendType(vipAwardManageJumu.getSendType());
//        iAwardVipService.add(awardVipJumu);
//        
//        // 记录到T_AWARD_ITEM 奖励数量暂时写死为1
//        AwardItem awardItemJumu = new AwardItem();
//        awardItemJumu.setAwardId(vipAwardManageJumu.getId());
//        awardItemJumu.setAwardNum(1);
//        iAwardItemService.add(awardItemJumu);
//        
//        
//        // TODO 启用定时任务
//        
//        // TODO 调用发放奖励方法
////        sendJuMuIntegral(orderId, phone, amount, awardName, adminId, sendType, orderDate, id);
////        sendWangxinAward(userId, mobile, couponGroupId, actionId, awardName, adminId, sendType, orderDate, id);
//        return true;
//    }
    
    private void sendWangxinAward(String userId, String mobile, String couponGroupId, String actionId, String awardName, long adminId, int sendType, String orderDate, String id) {
        String jsonResponse = "";
        JSONObject responseJsonObj = new JSONObject();        
        if (userId == null) {
            userId = "";
        }
        if (mobile.isEmpty() || couponGroupId.isEmpty()) {
            throw new MyException("参数不足!");
        }
        // 根据所传参数，生成md5加密结果，并和前端结果进行比较，如果不同，则直接返回并抛出异常。
        // StringBuilder 已经在工厂模式里面修改了
        String str = ConstantsConfig.SECRET + ACTION_ID + actionId + "couponGroupId" + couponGroupId + "mobile" + mobile + "siteId" + ConstantsConfig.SITE_ID
                + "userId" + userId + ConstantsConfig.SECRET;
        String stringCompare = MD5Util.getMD5String(str);
        String signAutoGeneration = stringCompare.toUpperCase();
        LogManager.info("未加密字符串: " + str + " MD5加密后字符串(32位大写): " + signAutoGeneration);
        // 根据前端所传信息，组装jsonObject。
        String url = ConstantsConfig.TEST_COUPON_URL + Api.COUPON_SEND.value;
        JSONObject param = new JSONObject();
        param.put("sign", signAutoGeneration);
        param.put("userId", userId);
        param.put("mobile", mobile);
        param.put(ACTION_ID, actionId);
        param.put("couponGroupId", couponGroupId);
        param.put("siteId", ConstantsConfig.SITE_ID);
        // 调用巨木发放奖励接口，发放奖励。
        try {
            LogManager.info("请求地址: " + url + " 请求参数: " + param);
            jsonResponse = HttpRequest.sendPost(url, param);
            LogManager.info("请求返回字符串: " + jsonResponse);
            responseJsonObj = JSONObject.parseObject(jsonResponse);

        }
        catch (Exception e) {
            throw new MyException(ErrorCode.NETWORK_LINK_ERROR);
        }
    }
    
    private void sendJuMuIntegral(String orderId, String phone, String amount, String awardName, long adminId, int sendType, String orderDate, String id) {
        String jsonResponse = "";
        JSONObject responseJsonObj = new JSONObject();
        if (orderId.isEmpty() || phone.isEmpty() || amount.isEmpty()) {
            throw new MyException("参数不足!");
        }
        
        // 根据所传参数，生成md5加密结果，并和前端结果进行比较，如果不同，则直接返回并抛出异常
        // StringBuilder
        String str = ConstantsConfig.CLIENT_SECRET + "amount" + amount + "clientId" + ConstantsConfig.CLIENT_ID + "merchantId" + ConstantsConfig.MERCHANT_ID + "orderId" + orderId
                + "phone" + phone + ConstantsConfig.CLIENT_SECRET;
        String signAutoGeneration = MD5Util.getMD5String(str);
        LogManager.info("未加密字符串: " + str + " MD5加密后字符串(32位大写): " + signAutoGeneration);
        // 根据前端所传信息，组装jsonObject。调用巨木API，发送http请求，发放积分奖励。
        String url = ConstantsConfig.TEST_INTEGRAL_URL + Api.INTEGRAL_SEND.value;
        JSONObject param = new JSONObject();
        param.put("sign", signAutoGeneration);
        param.put("amount", amount);
        param.put("clientId", ConstantsConfig.CLIENT_ID);
        param.put("merchantId", ConstantsConfig.MERCHANT_ID);
        param.put("orderId", orderId);
        param.put("phone", phone);
        // 调用巨木发放积分接口，发放积分。
        try {
            LogManager.info("请求地址: " + url + " 请求参数: " + param);
            jsonResponse = HttpRequest.sendPost(url, param);
//            logger.info("请求返回字符串: " + jsonResponse);
            responseJsonObj = JSONObject.parseObject(jsonResponse);

        }
        catch (Exception e) {
            throw new MyException(ErrorCode.NETWORK_LINK_ERROR);
        }

    }

}