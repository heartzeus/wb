package com.hhnz.controller.cfcrm.controller.vm;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.MsgType;
import com.hhnz.api.cfcrm.constants.enums.SendType;
import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IDiyFilterService;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.ISendToVipService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipAwardManageService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.controller.cfcrm.controller.BaseController;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.util.db.table.data.BooleanValue;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.controller.authority.IUser;

@RequestMapping(value = ("/send"), produces = "text/html;charset=UTF-8")
@Controller
public class MsgSendAndAwardManageController extends BaseController {

    private static final String SEND_MSG_ALL = "/sendMsgAll";

    private static final String SEND_MSG_SELECTED = "/sendMsgSelected";

    private static final String SEND_AWARD_SELECTED = "/sendAwardSelected";

    private static final String SEND_AWARD_ALL = "/sendAwardAll";

    private static final String GET_AWARD = "/getAward";

    @Autowired
    private IVipPrimaryInfoService iVipPrimaryInfoService;

    @Autowired
    private IDiyFilterService filterService;

    @Autowired
    private IVipAwardManageService iVipAwardManageService;

    @Autowired
    private ISendToVipService sendToVipService;

    @Autowired
    private IMsgAutoSendService msgAutoSendService;

    /**
     * 发送短信接口 管理员勾选客户
     * 
     * @param customIdArray
     * @param msgContent
     * @param msgName
     * @param sendType
     * @param date
     * @return
     */
    @RequestMapping(value = SEND_MSG_SELECTED)
    @ResponseBody
    public Object sendMsg(@RequestParam("customIdArray") List<Long> customIdArray, String msgContent, String msgName, int sendType, String date) {
        if (customIdArray == null || customIdArray.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        // 超出最大短信发送量，要抛出异常
        if (customIdArray.size() > ConstantsConfig.SEND_SMS_MAX_NUM) {
            throw new MyException(ErrorCode.VIP_MSG_OVERFLOW);
        }

        List<VipPrimaryInfo> persons = iVipPrimaryInfoService.select(new Filter().andFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, customIdArray));

        msgAutoSendService.sendMsg2Custom(persons, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 发送短信接口 管理员全选客户
     * 
     * @param filterId
     * @param msgContent
     * @param msgName
     * @param sendType
     * @param date
     * @return
     */
    @RequestMapping(value = SEND_MSG_ALL)
    @ResponseBody
    public Object sendMsg(Long filterId, String msgContent, @RequestParam("msgName") String msgName, int sendType, String date) {
        Filter filter = new Filter();
        if (filterId != null) {
            filter = filterService.getFilter(filterId);
        }
        else {
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        }
        List<VipPrimaryInfo> persons = iVipPrimaryInfoService.select(filter);

        msgAutoSendService.sendMsg2Custom(persons, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 奖励发放接口 管理员勾选客户
     * 
     * @param request
     * @param customIdArray
     * @param awardTypes
     * @param awardItemIndexs
     * @param awardNum
     * @param date
     * @param sendType
     * @param awardTime
     * @param awardName
     */
    @RequestMapping(value = SEND_AWARD_SELECTED)
    @ResponseBody
    public Object sendAwardSelected(HttpServletRequest request, @RequestParam("customIdArray") List<String> customIdArray,
            int sendType, String couponGroupId, int amount, String sendAwardDate, String awardName) {
        // TODO 以后操作员user
//        IUser user = super.getCurrentUser(request);
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();
        if (customIdArray == null || customIdArray.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        List<VipPrimaryInfo> persons = iVipPrimaryInfoService.select(new Filter().andFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, customIdArray));

        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
//            date = new Date(TimeUtil.getTime(sendAwardDate));
            date = new Date(TimeUtil.getTimeByDay(sendAwardDate));
        }
        // TODO NEED TO CHECK
        iVipAwardManageService.sendAward(persons, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }

    /**
     * 奖励发放接口 管理员全选客户
     * 
     * @param request
     * @param filterId
     * @param awardTypes
     * @param awardItemIndexs
     * @param awardNums
     * @param sendType
     * @param awardTime
     * @param awardName
     */
    @RequestMapping(value = SEND_AWARD_ALL)
    @ResponseBody
    public Object sendAwardAll(HttpServletRequest request, Long filterId, int sendType, String couponGroupId, int amount, String sendAwardDate, String awardName) {
        // TODO 以后操作员user
        // IUser user = super.getCurrentUser(request);
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();
        Filter filter = new Filter();
        if (filterId != null) {
            filter = filterService.getFilter(filterId);
        }
        else {
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        }
        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            date = new Date(TimeUtil.getTime(sendAwardDate));
        }
        // 根据过滤条件获取所有要发送的客户信息

        List<VipPrimaryInfo> persons = iVipPrimaryInfoService.selectByAutoFilter(filter);
        // TODO NEED TO CHECK
        iVipAwardManageService.sendAward(persons, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }

    /**
     * 生日自动祝福
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = GET)
    @ResponseBody
    public Object getBirthBless(HttpServletRequest request) {
        return msgAutoSendService.selectBirthSendMsg();
    }

    /**
     * 生日自动祝福保存
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = UPDATE)
    @ResponseBody
    public Object addBirthBless(HttpServletRequest request, int isOpen, String content, int time) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_MSG_AUTO_SEND.TYPE, MsgType.BIRTHDAY_MSG);
        MsgAutoSend item = new MsgAutoSend();
        if (StringUtil.isEmpty(content)) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        else {
            item.setContent(content);
        }
        item.setTime(time);
        item.setType(MsgType.BIRTHDAY_MSG);
        item.setIsOpen(BooleanValue.TRUE_VALUE == isOpen);
        msgAutoSendService.updateSelective(item, filter);
        return NULL;
    }

    /**
     * 获取巨木提供网信奖励
     * 
     * @author gzh
     * @return
     */
    @RequestMapping(value = GET_AWARD)
    @ResponseBody
    public Object getWangxinAward(HttpServletRequest request) {
        JSONObject responseJsonObj = new JSONObject();

        return responseJsonObj;
    }
    
    /**
     * 勾选客户发放奖励
     * @param request
     * @param phone
     * @param amount
     * @param userId
     * @param couponGroupId
     * @return
     */
//    @RequestMapping(value = SEND_AWARD_SELECTED)
//    @ResponseBody
//    public Object sendIntegralAndAward(HttpServletRequest request, @RequestParam(value = "customIdArray", required = false) String[] customIdArray, String amount,
//            String userId, String couponGroupId, int sendType, String awardName, String orderDate) {
//        // 检查一下人数，人数不能过多，不能超过100
//        if (customIdArray != null && customIdArray.length >= 1 && customIdArray.length < 100) {
////            IUser user = super.getCurrentUser(request);
////            long adminId = user.getUserId();
//            // TODO 测试用2，上线后要改回
//            long adminId = 2l;
//            // 当天时间作为唯一标识,作为交易流水号
//            Date date = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//            String tradeNo = sdf.format(date);
//            // 封装List<AwardItem>             
//
//        }        
//        return null;
//    }





}
