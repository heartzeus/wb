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
import com.hhnz.api.cfcrm.constants.enums.MsgType;
import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.VipMsgSendingHistory;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.ISendHistoryVipService;
import com.hhnz.api.cfcrm.service.cfcrm.ISendToVipService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipMsgSendingHistoryService;
import com.hhnz.controller.cfcrm.controller.BaseController;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.base.dataservice.filter.page.Page;
import com.tuhanbao.io.objutil.Duration;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.controller.helper.Pagination;
import com.tuhanbao.web.filter.FilterUtil;

@RequestMapping(value = ("/smsManage"), produces = "text/html;charset=UTF-8")
@Controller
public class SmsManageController extends BaseController {

    /**
     * 发送短信历史
     */
    private static final String GET_SMS_HISTORY = "/getSmsHistory";

    /**
     * 预约发送短信历史
     */
    private static final String GET_READY_SMS_HISTORY = "/getReadySmsHistory";

    /**
     * 查看短信详情
     */
    private static final String GET_SMS_DETAIL = "/getSmsDetail";

    /**
     * 查看预约短信详情
     */
    private static final String GET_READY_SMS_DETAIL = "/getReadySmsDetail";

    /**
     * 删除预约短信
     */
    private static final String DELETE_READY_SMS = "/deleteReadySms";

    @Autowired
    private ISendHistoryVipService sendHistoryVipService;

    @Autowired
    private ISendToVipService sendToVipService;

    @Autowired
    private IMsgAutoSendService msgAutoSendService;

    @Autowired
    private IVipMsgSendingHistoryService msgSendingHistoryService;

    /**
     * 发送短信历史
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = GET_SMS_HISTORY)
    @ResponseBody
    public Object getSmsHistory(HttpServletRequest request, String smsName, String sendVipName, String startDate, String endDate,
            @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Date start = null;
        Date end = null;
        Filter filter = new Filter();
        if (!StringUtil.isEmpty(startDate)) {
            start = new Date(TimeUtil.getTimeByDay(startDate));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_MSG_SENDING_HISTORY.SEND_DATE, start);
        }
        if (!StringUtil.isEmpty(endDate)) {
            end = new Date(TimeUtil.getTimeByDay(endDate) + TimeUtil.DAY2MILLS);
            filter.andFilter(Operator.LESS, TableConstants.T_VIP_MSG_SENDING_HISTORY.SEND_DATE, end);
        }
        Page page = new Page(pageNum, pageSize);
        boolean hasTelPhone = false;
        if (!StringUtil.isEmpty(sendVipName)) {
            hasTelPhone = true;
            filter.andFilter(TableConstants.T_SEND_HISTORY_VIP.VIP_PHONE, sendVipName);
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_MSG_SENDING_HISTORY.MSG_NAME, filter, smsName);
        filter.setPage(page);
        List<VipMsgSendingHistory> list = msgSendingHistoryService.selectVipMsgHistory(filter, hasTelPhone);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 预发送短信历史
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = GET_READY_SMS_HISTORY)
    @ResponseBody
    public Object getReadySmsHistory(HttpServletRequest request, String smsName, String sendVipName, String dateStr, boolean isOpen,
            @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_MSG_AUTO_SEND.TYPE, MsgType.TIMER_MSG);
        if (!StringUtil.isEmpty(dateStr)) {
            Duration duration = Duration.getDay(TimeUtil.getTime(dateStr));
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_MSG_AUTO_SEND.SEND_DATE, duration.getStartTime());
            filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_MSG_AUTO_SEND.SEND_DATE, duration.getEndTime());
        }
        Page page = new Page(pageNum, pageSize);
        FilterUtil.addLikeWordToFilter(TableConstants.T_MSG_AUTO_SEND.NAME, filter, smsName);
        filter.andFilter(TableConstants.T_MSG_AUTO_SEND.IS_OPEN, isOpen);
        filter.addOrderField(TableConstants.T_MSG_AUTO_SEND.SEND_DATE, true);
        filter.setPage(page);
        boolean hasTelPhone = false;
        if (!StringUtil.isEmpty(sendVipName)) {
            hasTelPhone = true;
            filter.andFilter(TableConstants.T_SEND_TO_VIP.VIP_PHONE, sendVipName);
        }
        List<MsgAutoSend> list = msgAutoSendService.selectReadyHistory(filter, hasTelPhone);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 查看短信详情
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = GET_SMS_DETAIL)
    @ResponseBody
    public Object getSmsDetail(HttpServletRequest request, @RequestParam("id") long id) {
        return msgSendingHistoryService.selectHistoryDetail(id);
    }

    /**
     * 查看预约短信详情
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = GET_READY_SMS_DETAIL)
    @ResponseBody
    public Object getReadySmsDetail(HttpServletRequest request, @RequestParam("id") long id) {
        return msgAutoSendService.selectReadyHistoryDetail(id);
    }

    /**
     * 发送短信历史删除
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = DELETE)
    @ResponseBody
    public Object delHistorySms(HttpServletRequest request, @RequestParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        sendHistoryVipService.deleteAll(ids);
        return NULL;
    }

    /**
     * 预约短信历史删除
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = DELETE_READY_SMS)
    @ResponseBody
    public Object delReadyHistorySms(HttpServletRequest request, @RequestParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        sendToVipService.deleteAll(ids);
        return NULL;
    }
}
