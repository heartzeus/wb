package com.hhnz.controller.cfcrm.controller.vm;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.SendType;
import com.hhnz.api.cfcrm.model.cfcrm.VipAwardManage;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipAwardManageService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.controller.cfcrm.controller.BaseController;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.base.dataservice.filter.page.Page;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.controller.authority.IUser;
import com.tuhanbao.web.controller.helper.Pagination;
import com.tuhanbao.web.filter.FilterUtil;

@RequestMapping(value = ("/award"), produces = "text/html;charset=UTF-8")
@Controller
public class AwardController extends BaseController {

    /**
     * 奖励项目
     */
    private static final String GET_AWARD = "/getAward";
    /**
     * 客户奖励发短信
     */
    private static final String CUSTOM_AWARD_MSG = "/customAwardMsg";
    /**
     * 客户奖励界面发奖励
     */
    private static final String CUSTOM_AWARD = "/customAward";

    @Autowired
    private IVipAwardManageService vipAwardManageService;

    @Autowired
    private IVipPrimaryInfoService vipPrimaryInfoService;

    @Autowired
    private IMsgAutoSendService iMsgAutoSendService;

    @Autowired
    private IVipAwardManageService iVipAwardManageService;

    /**
     * 查询客户奖励
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = GET)
    @ResponseBody
    public Object getCustomAward(HttpServletRequest request, String customId, String customName, String awardName, String startDate, String endDate,
            @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();

        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, end);
        }
        Page page = new Page(pageNum, pageSize);
        FilterUtil.addLikeWordToFilter(TableConstants.T_AWARD_VIP.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_AWARD_MANAGE.AWARD_NAME, filter, awardName);
        filter.setPage(page);
        List<VipAwardManage> list = vipAwardManageService.selectVipAward(filter);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 客户奖励页面发奖励
     */
    @RequestMapping(value = CUSTOM_AWARD)
    @ResponseBody
    public Object customAward(HttpServletRequest request, String customId, String customName, String awardName, String startDate, String endDate, int sendType, String couponGroupId, int amount, String sendAwardDate) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();
        IUser user = super.getCurrentUser(request);
        long adminId = user.getUserId();

        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, end);
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_AWARD_MANAGE.AWARD_NAME, filter, awardName);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectAwardInfo(filter);
        SendType st = SendType.getSendType(sendType);
        Date date = null;
        if (st == SendType.RESERVATION_SEND) {
            if (StringUtil.isEmpty(sendAwardDate)) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            date = new Date(TimeUtil.getTime(sendAwardDate));
        }
        iVipAwardManageService.sendAward(list, amount, couponGroupId, st, awardName, date, adminId);
        return NULL;
    }

    /**
     * 客户奖励页面发短信
     */
    @RequestMapping(value = CUSTOM_AWARD_MSG)
    @ResponseBody
    public Object customAwardMsg(HttpServletRequest request, String customId, String customName, String awardName, String startDate, String endDate,
            String msgContent, @RequestParam("msgName") String msgName, int sendType, String date) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();

        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_VIP_AWARD_MANAGE.AWARD_DATE, end);
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_AWARD_MANAGE.AWARD_NAME, filter, awardName);
        List<VipPrimaryInfo> list = vipPrimaryInfoService.selectAwardInfo(filter);
        iMsgAutoSendService.sendMsg2Custom(list, msgContent, msgName, SendType.getSendType(sendType), date);
        return NULL;
    }

    /**
     * 奖励项目
     */
    @RequestMapping(value = GET_AWARD)
    @ResponseBody
    public Object getCustomAward(HttpServletRequest request) {
        return vipAwardManageService.getJumuAward();
    }
}
