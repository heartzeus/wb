package com.hhnz.controller.cfcrm.controller.vm;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.MoneyBackStatus;
import com.hhnz.api.cfcrm.constants.enums.SendType;
import com.hhnz.api.cfcrm.constants.enums.Sex;
import com.hhnz.api.cfcrm.model.cfcrm.PaymentCollection;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.IPaymentCollectionService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipAwardManageService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestInfoService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.api.cfcrm.tool.TimeTool;
import com.hhnz.controller.cfcrm.controller.BaseController;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.base.dataservice.filter.page.Page;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.base.util.objutil.StringUtil;
import com.tuhanbao.base.util.objutil.TimeUtil;
import com.tuhanbao.web.controller.authority.IUser;
import com.tuhanbao.web.controller.helper.Pagination;
import com.tuhanbao.web.filter.FilterUtil;

@RequestMapping(value = ("/reminder"), produces = "text/html;charset=UTF-8")
@Controller
public class CustomerReminderController extends BaseController {

    /**
     * 回款时间提醒
     */
    private static final String MONEY_BACK_REMINDER = "/moneyBackReminder";
    /**
     * 回款时间提醒发短信
     */
    private static final String MONEY_BACK_MSG = "/moneyBackMsg";
    /**
     * 回款时间提醒发奖励
     */
    private static final String MONEY_BACK_AWARD = "/moneyBackAward";
    /**
     * 回款计划
     */
    private static final String MONEY_BACK_PLAN = "/moneyBackPlan";
    /**
     * 回款计划发短信
     */
    private static final String MONEY_BACK_PLAN_MSG = "/moneyBackPlanMsg";
    /**
     * 回款计划发奖励
     */
    private static final String MONEY_BACK_PLAN_AWARD = "/moneyBackPlanAward";
    /**
     * 投资降幅提醒
     */
    private static final String INVESTMENT_REMINDER = "/investmentReminder";
    /**
     * 投资降幅发短信
     */
    private static final String INVESTMENT_MSG = "/investmentMsg";
    /**
     * 投资降幅发奖励
     */
    private static final String INVESTMENT_AWARD = "/investmentAward";
    /**
     * 30天未投资提醒
     */
    private static final String NOINVEST_REMINDER = "/noinvestReminder";
    /**
     * 30天未投资发短信
     */
    private static final String NOINVEST_MSG = "/noinvestMsg";
    /**
     * 30天未投资发奖励
     */
    private static final String NOINVEST_AWARD = "/noinvestAward";
    /**
     * 生日提醒
     */
    private static final String BIRTH_REMINDER = "/birthReminder";
    /**
     * 生日发短信
     */
    private static final String BIRTH_MSG = "/birthMsg";
    /**
     * 生日发奖励
     */
    private static final String BIRTH_AWARD = "/birthAward";

    @Autowired
    private IVipPrimaryInfoService vipPrimaryInfoService;

    @Autowired
    private IVipInvestInfoService vipInvestInfoService;

    @Autowired
    private IMsgAutoSendService iMsgAutoSendService;

    @Autowired
    private IVipAwardManageService iVipAwardManageService;

    @Autowired
    private IPaymentCollectionService iPaymentCollectionService;

    /**
     * 回款时间提醒
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = MONEY_BACK_REMINDER)
    @ResponseBody
    public Object moneyBackReminder(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate,
            String projectName, String status, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();
        // 如果不传日期，默认取所有记录
        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, end);
        }

        MoneyBackStatus moneyBackStatus = null;
        if (!StringUtil.isEmpty(status)) {
            moneyBackStatus = MoneyBackStatus.getMoneyBackStatus(Integer.valueOf(status));
            filter.andFilter(TableConstants.T_PAYMENT_COLLECTION.STATUS, moneyBackStatus);
        }
        Page page = new Page(pageNum, pageSize);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_PAYMENT_COLLECTION.PROJECT_NAME, filter, projectName);

        // 排序
        filter.addOrderField(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, true);
        filter.setPage(page);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectCustomReminder(filter);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 回款时间提醒界面发送短信
     */
    @RequestMapping(value = MONEY_BACK_MSG)
    @ResponseBody
    public Object moneyBackMsg(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate,
            String projectName, String status, String msgContent, @RequestParam("msgName") String msgName, int sendType, String date) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();

        // 如果不传日期，只取当天
        if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
            long time = TimeUtil.getTime(TimeTool.getCurentYMD());
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, time);
            filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, time + TimeUtil.DAY2MILLS);
        }
        else {
            if (!StringUtil.isEmpty(startDate)) {
                start = new Date(TimeUtil.getTimeByDay(startDate));
                filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, start);
            }
            if (!StringUtil.isEmpty(endDate)) {
                end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
                filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, end);
            }
        }
        MoneyBackStatus moneyBackStatus = null;
        if (!StringUtil.isEmpty(status)) {
            moneyBackStatus = MoneyBackStatus.getMoneyBackStatus(Integer.valueOf(status));
            filter.andFilter(TableConstants.T_PAYMENT_COLLECTION.STATUS, moneyBackStatus);
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_PAYMENT_COLLECTION.PROJECT_NAME, filter, projectName);

        // 排序
        filter.addOrderField(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, true);
        List<PaymentCollection> list = iPaymentCollectionService.selectCustomReminder(filter);
        iMsgAutoSendService.sendMsg2CustomPayBack(list, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 回款时间提醒界面发送奖励
     */
    @RequestMapping(value = MONEY_BACK_AWARD)
    @ResponseBody
    public Object moneyBackAward(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate,
            String projectName, String status, int sendType, String couponGroupId, int amount, String sendAwardDate, String awardName) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();

        // 如果不传日期，只取当天
        if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
            long time = TimeUtil.getTime(TimeTool.getCurentYMD());
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, time);
            filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, time + TimeUtil.DAY2MILLS);
        }
        else {
            if (!StringUtil.isEmpty(startDate)) {
                start = new Date(TimeUtil.getTimeByDay(startDate));
                filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, start);
            }
            if (!StringUtil.isEmpty(endDate)) {
                end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
                filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, end);
            }
        }
        MoneyBackStatus moneyBackStatus = null;
        if (!StringUtil.isEmpty(status)) {
            moneyBackStatus = MoneyBackStatus.getMoneyBackStatus(Integer.valueOf(status));
            filter.andFilter(TableConstants.T_PAYMENT_COLLECTION.STATUS, moneyBackStatus);
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_PAYMENT_COLLECTION.PROJECT_NAME, filter, projectName);

        // 排序
        filter.addOrderField(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, true);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectCustomReminder(filter);
        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            date = new Date(TimeUtil.getTime(sendAwardDate));
        }
        // iVipAwardManageService.sendAward(list, awardTypes, awardItemIndexs,
        // awardNum, st, date, awardName, 1L);
        iVipAwardManageService.sendAward(list, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }

    /**
     * 回款计划
     * 
     * @author gzh
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = MONEY_BACK_PLAN)
    @ResponseBody
    public Object moneyBackPlan(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate,
            String projectName, String status, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Date start = null;
        Date end = new Date();
        Filter filter = new Filter();
        // 如果起始时间和终止时间没传，默认取所有记录
        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, end);
        }
        MoneyBackStatus moneyBackStatus = null;
        if (!StringUtil.isEmpty(status)) {
            moneyBackStatus = MoneyBackStatus.getMoneyBackStatus(Integer.valueOf(status));
            filter.andFilter(TableConstants.T_PAYMENT_COLLECTION.STATUS, moneyBackStatus);
        }
        Page page = new Page(pageNum, pageSize);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_PAYMENT_COLLECTION.PROJECT_NAME, filter, projectName);

        // 排序
        filter.addOrderField(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, true);
        filter.setPage(page);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectCustomReminder(filter);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 回款计划发送短信
     */
    @RequestMapping(value = MONEY_BACK_PLAN_MSG)
    @ResponseBody
    public Object moneyBackPlanMsg(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate,
            String projectName, String status, String msgContent, @RequestParam("msgName") String msgName, int sendType, String date) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();
        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, end);
        }
        MoneyBackStatus moneyBackStatus = null;
        if (!StringUtil.isEmpty(status)) {
            moneyBackStatus = MoneyBackStatus.getMoneyBackStatus(Integer.valueOf(status));
            filter.andFilter(TableConstants.T_PAYMENT_COLLECTION.STATUS, moneyBackStatus);
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_PAYMENT_COLLECTION.PROJECT_NAME, filter, projectName);

        // 排序
        filter.addOrderField(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, true);
        List<PaymentCollection> list = iPaymentCollectionService.selectCustomReminder(filter);
        iMsgAutoSendService.sendMsg2CustomPayBack(list, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 回款时间计划发送奖励
     */
    @RequestMapping(value = MONEY_BACK_PLAN_AWARD)
    @ResponseBody
    public Object moneyBackPlanAward(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate,
            String projectName, String status, String awardName, int sendType, String couponGroupId, int amount, String sendAwardDate) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();

        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, end);
        }
        MoneyBackStatus moneyBackStatus = null;
        if (!StringUtil.isEmpty(status)) {
            moneyBackStatus = MoneyBackStatus.getMoneyBackStatus(Integer.valueOf(status));
            filter.andFilter(TableConstants.T_PAYMENT_COLLECTION.STATUS, moneyBackStatus);
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_PAYMENT_COLLECTION.PROJECT_NAME, filter, projectName);

        // 排序
        filter.addOrderField(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, true);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectCustomReminder(filter);
        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            date = new Date(TimeUtil.getTime(sendAwardDate));
        }
        // TODO NEED TO CHECK
        iVipAwardManageService.sendAward(list, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }

    /**
     * 投资降幅提醒
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = INVESTMENT_REMINDER)
    @ResponseBody
    public Object investmentReminder(HttpServletRequest request, String customId, String customName, String tel, Integer dropStart, Integer dropEnd,
            @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Page page = new Page(pageNum, pageSize);
        Filter filter = new Filter();

        int[] ymd = TimeTool.getCurentYMD();

        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0]);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ymd[1]);
        filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, 0);
        if (dropStart != null) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, dropStart);
        }
        if (dropEnd != null) {
            filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, dropEnd);
        }
        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, true);
        filter.setPage(page);
        List<VipInvestInfo> list = vipInvestInfoService.selectInvestInfo(filter);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 投资降幅发短信
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = INVESTMENT_MSG)
    @ResponseBody
    public Object investmentMsg(HttpServletRequest request, String customId, String customName, String tel, Integer dropStart, Integer dropEnd,
            String msgContent, @RequestParam("msgName") String msgName, int sendType, String date) {
        Filter filter = new Filter();

        int[] ymd = TimeTool.getCurentYMD();

        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, 0);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0]);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ymd[1]);
        if (dropStart != null) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, dropStart);
        }
        if (dropEnd != null) {
            filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, dropEnd);
        }
        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, true);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectInvestInfo(filter);
        iMsgAutoSendService.sendMsg2Custom(list, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 投资降幅发奖励
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = INVESTMENT_AWARD)
    @ResponseBody
    public Object investmentAward(HttpServletRequest request, String customId, String customName, String tel, Integer dropStart, Integer dropEnd,
            int sendType, String couponGroupId, int amount, String sendAwardDate, String awardName) {
        Filter filter = new Filter();

        int[] ymd = TimeTool.getCurentYMD();
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();

        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0]);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ymd[1]);
        filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, 0);
        if (dropStart != null) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, dropStart);
        }
        if (dropEnd != null) {
            filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, dropEnd);
        }
        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, true);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectInvestInfo(filter);
        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            date = new Date(TimeUtil.getTime(sendAwardDate));
        }
        // TODO NEED TO CHECK
        iVipAwardManageService.sendAward(list, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }

    /**
     * 30天未投资提醒
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = NOINVEST_REMINDER)
    @ResponseBody
    public Object noinvestReminder(HttpServletRequest request, String customId, String customName, String tel, Integer noinvestDayStart,
            Integer noinvestDayEnd, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Page page = new Page(pageNum, pageSize);
        Filter filter = new Filter();

        int[] ymd = TimeTool.getCurentYMD();

        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0]);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ymd[1]);

        // 如果不传未投资天数，过滤所有大于30天的
        // if (noinvestDayStart == null && noinvestDayEnd == null) {
        // filter.andFilter(Operator.GREATER_EQUAL,
        // TableConstants.T_VIP_INVEST_INFO.OFF_DAY,
        // ConstantsConfig.DEFAULT_OFF_DAY);
        // }
        if (noinvestDayStart != null) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, noinvestDayStart);
        }
        if (noinvestDayEnd != null) {
            filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, noinvestDayEnd);
        }

        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.OFF_DAY, true);
        filter.setPage(page);
        List<VipInvestInfo> list = vipInvestInfoService.selectInvestInfo(filter);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 30天未投资发短信
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = NOINVEST_MSG)
    @ResponseBody
    public Object noinvestMsg(HttpServletRequest request, String customId, String customName, String tel, Integer noinvestDayStart,
            Integer noinvestDayEnd, String msgContent, @RequestParam("msgName") String msgName, int sendType, String date) {
        Filter filter = new Filter();

        int[] ymd = TimeTool.getCurentYMD();

        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0]);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ymd[1]);

        // 如果不传未投资天数，过滤所有大于30天的
        if (noinvestDayStart == null && noinvestDayEnd == null) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, ConstantsConfig.DEFAULT_OFF_DAY);
        }
        else {
            if (noinvestDayStart != null) {
                filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, noinvestDayStart);
            }
            if (noinvestDayEnd != null) {
                filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, noinvestDayEnd);
            }
        }

        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.OFF_DAY, true);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectInvestInfo(filter);
        iMsgAutoSendService.sendMsg2Custom(list, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 30天未投资发奖励
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = NOINVEST_AWARD)
    @ResponseBody
    public Object noinvestAward(HttpServletRequest request, String customId, String customName, String tel, Integer noinvestDayStart,
            Integer noinvestDayEnd, int sendType, String couponGroupId, int amount, String sendAwardDate, String awardName) {
        Filter filter = new Filter();
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();

        int[] ymd = TimeTool.getCurentYMD();

        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0]);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ymd[1]);

        // 如果不传未投资天数，过滤所有大于30天的
        if (noinvestDayStart == null && noinvestDayEnd == null) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, ConstantsConfig.DEFAULT_OFF_DAY);
        }
        else {
            if (noinvestDayStart != null) {
                filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, noinvestDayStart);
            }
            if (noinvestDayEnd != null) {
                filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_VIP_INVEST_INFO.OFF_DAY, noinvestDayEnd);
            }
        }

        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.OFF_DAY, true);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectInvestInfo(filter);
        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            date = new Date(TimeUtil.getTime(sendAwardDate));
        }
        // TODO NEED TO CHECK
        iVipAwardManageService.sendAward(list, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }

    /**
     * 生日提醒
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = BIRTH_REMINDER)
    @ResponseBody
    public Object birthReminder(HttpServletRequest request, String customId, String customName, String tel, String month, String day, Integer sex,
            @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Filter filter = new Filter();
        Page page = new Page(pageNum, pageSize);
        filter.setPage(page);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        // 不传日期，取所有的记录
        if (!StringUtil.isEmpty(month)) {
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.MONTH, month);
        }
        if (!StringUtil.isEmpty(day)) {
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.DAY, day);
        }
        if (sex != null) {
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.SEX, Sex.getSex(sex));
        }

        List<VipPrimaryInfo> list = vipPrimaryInfoService.select(filter);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 生日发短信
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = BIRTH_MSG)
    @ResponseBody
    public Object birthMsg(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate, Integer sex,
            String msgContent, @RequestParam("msgName") String msgName, int sendType, String date) {
        Filter filter = new Filter();
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        // 不传日期，只取今天生日的
        if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
            int[] ymd = TimeUtil.getTodayYearMonthDayHour();
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.MONTH, ymd[1]);
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.DAY, ymd[2]);
        }
        else {
            if (!StringUtil.isEmpty(startDate)) {
                Date start = new Date(TimeUtil.getTimeByDay(startDate));
                filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_PRIMARY_INFO.BIRTHDAY, start);
            }
            if (!StringUtil.isEmpty(endDate)) {
                Date end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
                filter.andFilter(Operator.LESS, TableConstants.T_VIP_PRIMARY_INFO.BIRTHDAY, end);
            }
        }

        if (sex != null) {
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.SEX, Sex.getSex(sex));
        }

        List<VipPrimaryInfo> list = vipPrimaryInfoService.select(filter);
        iMsgAutoSendService.sendMsg2Custom(list, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 生日发奖励
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = BIRTH_AWARD)
    @ResponseBody
    public Object birthAward(HttpServletRequest request, String customId, String customName, String tel, String startDate, String endDate,
            Integer sex, int sendType, String couponGroupId, int amount, String sendAwardDate, String awardName) {
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();
        Filter filter = new Filter();
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.PHONE, filter, tel);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        // 不传日期，只取今天生日的
        if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
            int[] ymd = TimeUtil.getTodayYearMonthDayHour();
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.MONTH, ymd[1]);
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.DAY, ymd[2]);
        }
        else {
            if (!StringUtil.isEmpty(startDate)) {
                Date start = new Date(TimeUtil.getTimeByDay(startDate));
                filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_PRIMARY_INFO.BIRTHDAY, start);
            }
            if (!StringUtil.isEmpty(endDate)) {
                Date end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
                filter.andFilter(Operator.LESS, TableConstants.T_VIP_PRIMARY_INFO.BIRTHDAY, end);
            }
        }

        if (sex != null) {
            filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.SEX, Sex.getSex(sex));
        }

        List<VipPrimaryInfo> list = vipPrimaryInfoService.select(filter);
        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            date = new Date(TimeUtil.getTime(sendAwardDate));
        }
        // TODO NEED TO CHECK
        iVipAwardManageService.sendAward(list, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }
}
