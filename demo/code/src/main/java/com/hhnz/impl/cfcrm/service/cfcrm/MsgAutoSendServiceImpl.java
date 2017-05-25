package com.hhnz.impl.cfcrm.service.cfcrm;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.APIResponseArgs;
import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.MsgType;
import com.hhnz.api.cfcrm.constants.enums.SendStatus;
import com.hhnz.api.cfcrm.constants.enums.SendType;
import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.PaymentCollection;
import com.hhnz.api.cfcrm.model.cfcrm.SendToVip;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.ISendHistoryVipService;
import com.hhnz.api.cfcrm.service.cfcrm.ISendToVipService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipMsgSendingHistoryService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.impl.cfcrm.AutoSendMsgTask;
import com.hhnz.impl.cfcrm.DIYServerManager;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.objutil.Duration;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.thirdapi.cache.CacheManager;
import com.tuhanbao.util.config.ConfigManager;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.util.thread.ScheduledThreadManager;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("msgAutoSendService")
@Transactional("cfcrmTransactionManager")
public class MsgAutoSendServiceImpl extends ServiceImpl<MsgAutoSend> implements IMsgAutoSendService {
    
    private static final MyBatisSelector MSG_SELECTOR = new MyBatisSelector(TableConstants.T_MSG_AUTO_SEND.TABLE);
    
    private static MyBatisSelector SMS_READY_HISTORY_SELECTOR = new MyBatisSelector(TableConstants.T_SEND_TO_VIP.TABLE);
    
    private static Timer BIRTH_TIMER = null;
    
    @Autowired
    private IVipMsgSendingHistoryService vipMsgSendingHistoryService;
	
	@Autowired
    private ISendToVipService sendToVipService;
	@Autowired
	private ISendHistoryVipService sendHistoryVipService;
	@Autowired
    private IVipPrimaryInfoService vipPrimaryInfoService;
    
    static {
        SMS_READY_HISTORY_SELECTOR.joinTable(TableConstants.T_MSG_AUTO_SEND.TABLE);
        MSG_SELECTOR.joinTable(TableConstants.T_SEND_TO_VIP.TABLE);
    }
    
	@Override
    public void add(MsgAutoSend msgAutoSend) {
	    if (msgAutoSend.getType() == MsgType.TIMER_MSG) {
    	    Filter filter = new Filter();
    	    Duration duration = Duration.getDay(msgAutoSend.getSendDate().getTime());
    	    filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_MSG_AUTO_SEND.SEND_DATE, duration.getStartTime());
    	    filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_MSG_AUTO_SEND.SEND_DATE, duration.getEndTime());
    	    filter.andFilter(TableConstants.T_MSG_AUTO_SEND.IS_OPEN, false);
    	    filter.andFilter(TableConstants.T_MSG_AUTO_SEND.TYPE, MsgType.TIMER_MSG.value);
    	    List<MsgAutoSend> mas = this.select(filter);
    	    if (mas.size() > ConstantsConfig.AUTO_MSG_MAX_NUM_PERDAY) {
    	        throw new MyException(ErrorCode.AUTO_SEND_MSG_TOO_MUCH);
    	    }
	    }
	    
	    super.add(msgAutoSend);
	}
	
	@Override
    public void updateBirthMsg(MsgAutoSend msgAutoSend) {
        this.update(msgAutoSend);
        updateBirthSendMsgTimer();
    }

    @Override
    public void sendMsg(MsgAutoSend mas) {
        // 调试模式
        if (ConfigManager.isDebug()) {
            return;
        }
        long time = TimeUtil.now();
        long sendTime = mas.getSendDate().getTime();

        long delay = sendTime > time ? sendTime - time : 0;
        AutoSendMsgTask task = new AutoSendMsgTask(mas, this, sendHistoryVipService);
        if (delay == 0) {
            ScheduledThreadManager.execute(task);
        }
        else {
            ScheduledThreadManager.executeAfterTime(delay, task);
        }

    }
    
    /**
     * 更新生日消息推送
     */
    public void updateBirthSendMsgTimer() {
        MsgAutoSend newValue = selectBirthSendMsg();
        //检查时间，如果自动发送时间在半小时以内，不准修改
        MsgAutoSend oldValue = CacheManager.get(DIYServerManager.WORK_BENCH, APIResponseArgs.BIRTH_MSG_SEND_TIME, MsgAutoSend.class);
        checkBirthUpdateTime(oldValue);
        checkBirthUpdateTime(newValue);
        
        if (newValue == null) return;
        if (oldValue == null || oldValue.isOpen() != newValue.isOpen() 
                || oldValue.getTime() != newValue.getTime() || !oldValue.getContent().equals(newValue.getContent())) {
            if (BIRTH_TIMER != null) {
                BIRTH_TIMER.cancel();
                BIRTH_TIMER = null;
            }
            CacheManager.set(DIYServerManager.WORK_BENCH, APIResponseArgs.BIRTH_MSG_SEND_TIME, newValue);
            startBirthMsgSend();
        }
    }

    private void startBirthMsgSend() {
        MsgAutoSend birthMsg = CacheManager.get(DIYServerManager.WORK_BENCH, APIResponseArgs.BIRTH_MSG_SEND_TIME, MsgAutoSend.class);
        if (birthMsg != null && birthMsg.isOpen()) {
            AutoSendMsgTask task = new AutoSendMsgTask(birthMsg, this, sendHistoryVipService);
            BIRTH_TIMER = ScheduledThreadManager.executeOnTimer(birthMsg.getTime(), 0, 0, task);
        }
    }
    
    private static void checkBirthUpdateTime(MsgAutoSend msg) {
        if (msg != null) {
            long time = TimeUtil.getTodayTime(msg.getTime(), 0, 0);
            long now = TimeUtil.now();
            if (Math.abs(time - now) < TimeUtil.MIN2MILL * ConstantsConfig.MAX_MSG_UPDATE_TIME) {
                throw new MyException(ErrorCode.CANNOT_UPDATE_BIRTH_MSG);
            }
        }
    }

	@Override
    public List<MsgAutoSend> selectAllNeedSendMsg(long time) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_MSG_AUTO_SEND.SEND_STATUS, SendStatus.TO_SEND);
        Duration duration = Duration.getDay(time);
        filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_MSG_AUTO_SEND.SEND_DATE, duration.getStartTime());
        filter.andFilter(Operator.LESS_EQUAL, TableConstants.T_MSG_AUTO_SEND.SEND_DATE, duration.getEndTime());
        filter.andFilter(TableConstants.T_MSG_AUTO_SEND.TYPE, MsgType.TIMER_MSG);
        List<MsgAutoSend> list = this.select(MSG_SELECTOR, filter);
        return list;
    }

    @Override
    public MsgAutoSend selectBirthSendMsg() {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_MSG_AUTO_SEND.TYPE, MsgType.BIRTHDAY_MSG);
        List<MsgAutoSend> list = this.select(filter);
        if (list.isEmpty()) {
            MsgAutoSend msgAutoSend = new MsgAutoSend();
            msgAutoSend.setIsOpen(false);
            msgAutoSend.setType(MsgType.BIRTHDAY_MSG);
            msgAutoSend.setTime(0);
            this.add(msgAutoSend);
            return msgAutoSend;
        }
        return list.get(0);
    }

    @Override
    public void saveHistory(MsgAutoSend mas, List<SendToVip> vips) {
        vipMsgSendingHistoryService.saveHistory(mas, vips);
    }

    @Override
    public List<MsgAutoSend> selectReadyHistory(Filter filter, boolean hasTelPhone) {
        List<MsgAutoSend> msgAutoSends = null;
        if (!hasTelPhone) {
            msgAutoSends = this.select(filter);
        }
        else {
            msgAutoSends = this.select(MSG_SELECTOR, filter);
        }
        for (MsgAutoSend msgAutoSend : msgAutoSends) {
            List<SendToVip> list = sendToVipService.select(new Filter().andFilter(TableConstants.T_SEND_TO_VIP.MSG_ID, msgAutoSend.getId()));
            StringBuilder sendReadyHistory = new StringBuilder();
            if (list.size() > ConstantsConfig.MAX_SHOW_PHONE_NUM) {
                for (int i = 0; i < ConstantsConfig.MAX_SHOW_PHONE_NUM; i++) {
                    sendReadyHistory.append(list.get(i).getVipPhone()).append(Constants.COMMA);
                }
                if (sendReadyHistory.length() > 0) {
                    sendReadyHistory.deleteCharAt(sendReadyHistory.length() - 1);
                }
                sendReadyHistory.append(Constants.ELLIPSIS);
            }
            else {
                for (SendToVip sendToVip : list) {
                    sendReadyHistory.append(sendToVip.getVipPhone()).append(Constants.COMMA);
                }
                if (sendReadyHistory.length() > 0) {
                    sendReadyHistory.deleteCharAt(sendReadyHistory.length() - 1);
                }
            }
            msgAutoSend.setShowPhone(sendReadyHistory.toString());
            StringBuilder content = new StringBuilder(msgAutoSend.getContent());
            if (content.length() > ConstantsConfig.MAX_SHOW_SEND_CONTENT) {
                content = content.delete(ConstantsConfig.MAX_SHOW_SEND_CONTENT, content.length());
                content.append(Constants.ELLIPSIS);
                msgAutoSend.setContent(content.toString());
            }
            else {
                msgAutoSend.setContent(content.toString());
            }
        }
        return msgAutoSends;
    }

    @Override
    public MsgAutoSend selectReadyHistoryDetail(long id) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_MSG_AUTO_SEND.ID, id);
        List<MsgAutoSend> list = this.select(MSG_SELECTOR, filter);
        MsgAutoSend msgAutoSend = new MsgAutoSend();
        if (list == null || list.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        msgAutoSend = list.get(0);
        List<SendToVip> sendToVips = msgAutoSend.removeSendToVips();
        StringBuilder sendReadyHistory = new StringBuilder();
        for (SendToVip sendToVip : sendToVips) {
            sendReadyHistory.append(sendToVip.getVipPhone()).append(Constants.COMMA);
        }
        if (sendReadyHistory.length() > 0) {
            sendReadyHistory.deleteCharAt(sendReadyHistory.length() - 1);
        }
        msgAutoSend.setShowPhone(sendReadyHistory.toString());
        return msgAutoSend;
    }
    
    /**
     * 发送短信方法
     * 
     * @param persons
     * @param msgContent
     * @param msgName
     * @param sendType
     * @param date
     */
    @Override
    public void sendMsg2Custom(List<VipPrimaryInfo> persons, String msgContent, String msgName, SendType sendType, String date) {       
        MsgAutoSend msgAutoSend = new MsgAutoSend();
        msgAutoSend.setContent(msgContent);
        msgAutoSend.setName(msgName);
        msgAutoSend.setIsOpen(false);
        msgAutoSend.setType(MsgType.TIMER_MSG);

        long time = 0;
        if (sendType != SendType.IMMEDIATE_SEND) {
            time = TimeUtil.getTime(date);
            if (time < TimeUtil.now()) {
                throw new MyException(ErrorCode.CANNOT_LESS_CURRENT_TIME);
            }
        }
        else {
            time = TimeUtil.now();
        }
        msgAutoSend.setSendType(sendType);
        msgAutoSend.setSendDate(new Date(time));
        this.add(msgAutoSend);

        long msgId = msgAutoSend.getId();
        List<SendToVip> children = new ArrayList<SendToVip>();
        for (VipPrimaryInfo personInfo : persons) {
            SendToVip vip = new SendToVip();
            vip.setMsgId(msgId);
            vip.setCustomId(personInfo.getId());
            vip.setVipPhone(personInfo.getPhone());
            sendToVipService.add(vip);
            children.add(vip);
        }
        msgAutoSend.setSendToVips(children);

        int[] ymd = TimeUtil.getYearMonthDayHour(msgAutoSend.getSendDate().getTime());
        long sendDate = TimeUtil.getTime(ymd[0], ymd[1], ymd[2], 0, 0, 0);
        if (sendDate == TimeUtil.getTodayTime(0, 0, 0)) {
            this.sendMsg(msgAutoSend);
        }
    }
    
    /**
     * 发送短信方法(专为回款改造)
     * 
     * @param persons
     * @param msgContent
     * @param msgName
     * @param sendType
     * @param date
     */
    @Override
    public void sendMsg2CustomPayBack(List<PaymentCollection> paymentCollections, String msgContent, String msgName, SendType sendType, String date) {       
        MsgAutoSend msgAutoSend = new MsgAutoSend();
        msgAutoSend.setContent(msgContent);
        msgAutoSend.setName(msgName);
        msgAutoSend.setIsOpen(false);
        msgAutoSend.setType(MsgType.TIMER_MSG);

        long time = 0;
        if (sendType != SendType.IMMEDIATE_SEND) {
            time = TimeUtil.getTime(date);
            if (time < TimeUtil.now()) {
                throw new MyException(ErrorCode.CANNOT_LESS_CURRENT_TIME);
            }
        }
        else {
            time = TimeUtil.now();
        }
        msgAutoSend.setSendType(sendType);
        msgAutoSend.setSendDate(new Date(time));
        this.add(msgAutoSend);

        long msgId = msgAutoSend.getId();
        List<SendToVip> children = new ArrayList<SendToVip>();
        for (PaymentCollection paymentCollection : paymentCollections) {
            VipPrimaryInfo vipPrimaryInfo = vipPrimaryInfoService.selectById(paymentCollection.getCustomId());
            SendToVip vip = new SendToVip();
            vip.setMsgId(msgId);
            vip.setCustomId(paymentCollection.getCustomId());
            vip.setVipPhone(vipPrimaryInfo.getPhone());
            sendToVipService.add(vip);
            children.add(vip);
        }
        msgAutoSend.setSendToVips(children);

        int[] ymd = TimeUtil.getYearMonthDayHour(msgAutoSend.getSendDate().getTime());
        long sendDate = TimeUtil.getTime(ymd[0], ymd[1], ymd[2], 0, 0, 0);
        if (sendDate == TimeUtil.getTodayTime(0, 0, 0)) {
            this.sendMsg(msgAutoSend);
        }
    }
}