package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.SendHistoryVip;
import com.hhnz.api.cfcrm.model.cfcrm.SendToVip;
import com.hhnz.api.cfcrm.model.cfcrm.VipMsgSendingHistory;
import com.hhnz.api.cfcrm.service.cfcrm.ISendHistoryVipService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipMsgSendingHistoryService;
import com.tuhanbao.base.Constants;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("vipMsgSendingHistoryService")
@Transactional("cfcrmTransactionManager")
public class VipMsgSendingHistoryServiceImpl extends ServiceImpl<VipMsgSendingHistory> implements IVipMsgSendingHistoryService {

    private static MyBatisSelector SMS_HISTORY_SELECTOR = new MyBatisSelector(TableConstants.T_VIP_MSG_SENDING_HISTORY.TABLE);

    @Autowired
    private ISendHistoryVipService sendHistoryVipService;

    static {
        SMS_HISTORY_SELECTOR.joinTable(TableConstants.T_SEND_HISTORY_VIP.TABLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hhnz.api.cfcrm.service.cfcrm.IVipMsgSendingHistoryService#
     * recordHistory(com.tuhanbao.web.filter.Filter) 记录短信发送历史
     */
    @Override
    public void recordHistory(Filter filter) {
        VipMsgSendingHistory vipMsgSendingHistory = new VipMsgSendingHistory();
        this.updateSelective(vipMsgSendingHistory, filter);
    }

    @Override
    public void saveHistory(MsgAutoSend mas, List<SendToVip> vips) {
        Filter filter = new Filter().andFilter(TableConstants.T_VIP_MSG_SENDING_HISTORY.MSG_ID, mas.getId());
        List<VipMsgSendingHistory> list = this.select(filter);
        VipMsgSendingHistory record = null;
        if (list.isEmpty()) {
            record = new VipMsgSendingHistory();
            record.setMsgId(mas.getId());
            record.setMsgName(mas.getName());
            record.setSendDate(new Date());
            record.setSendContent(mas.getContent());
            this.add(record);
        }
        else {
            record = list.get(0);
        }

        for (SendToVip item : vips) {
            SendHistoryVip vip = new SendHistoryVip();
            vip.setCustomId(item.getCustomId());
            vip.setMsgId(record.getId());
            vip.setVipPhone(item.getVipPhone());
            sendHistoryVipService.add(vip);
        }
    }

    @Override
    public List<VipMsgSendingHistory> selectVipMsgHistory(Filter filter, boolean hasTelPhone) {
        List<VipMsgSendingHistory> list = null;
        // 前端是否传联系电话
        if (!hasTelPhone) {
            list = this.select(filter);
        }
        else {
            list = this.select(SMS_HISTORY_SELECTOR, filter);
        }
        for (VipMsgSendingHistory vipMsgSendingHistory : list) {
            List<SendHistoryVip> sendHistoryVips = sendHistoryVipService
                    .select(new Filter().andFilter(TableConstants.T_SEND_HISTORY_VIP.MSG_ID, vipMsgSendingHistory.getId()));
            StringBuilder sendHistory = new StringBuilder();
            if (sendHistoryVips.size() > ConstantsConfig.MAX_SHOW_PHONE_NUM) {
                for (int i = 0; i < ConstantsConfig.MAX_SHOW_PHONE_NUM; i++) {
                    sendHistory.append(sendHistoryVips.get(i).getVipPhone()).append(Constants.COMMA);
                }
                if (sendHistory.length() > 0) {
                    sendHistory.deleteCharAt(sendHistory.length() - 1);
                }
                sendHistory.append(Constants.ELLIPSIS);
            }
            else {
                for (SendHistoryVip sendHistoryVip : sendHistoryVips) {
                    sendHistory.append(sendHistoryVip.getVipPhone()).append(Constants.COMMA);
                }
                if (sendHistory.length() > 0) {
                    sendHistory.deleteCharAt(sendHistory.length() - 1);
                }
            }

            vipMsgSendingHistory.setShowPhone(sendHistory.toString());
            StringBuilder content = new StringBuilder(vipMsgSendingHistory.getSendContent());
            if (content.length() > ConstantsConfig.MAX_SHOW_SEND_CONTENT) {
                content = content.delete(ConstantsConfig.MAX_SHOW_SEND_CONTENT, content.length());
                content.append(Constants.ELLIPSIS);
                vipMsgSendingHistory.setSendContent(content.toString());
            }
        }
        return list;
    }

    @Override
    public VipMsgSendingHistory selectHistoryDetail(long id) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_MSG_SENDING_HISTORY.ID, id);
        List<VipMsgSendingHistory> list = this.select(SMS_HISTORY_SELECTOR, filter);
        
        if (list == null || list.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }

        VipMsgSendingHistory vipMsgSendingHistory = list.get(0);
        // TODO 这里出现了空指针异常，不知道是否和remove方法有关。
        List<SendHistoryVip> sendHistoryVips = vipMsgSendingHistory.removeSendHistoryVips();
        StringBuilder sendHistory = new StringBuilder();
        if (sendHistoryVips != null && sendHistoryVips.size() > 0) {
            for (SendHistoryVip sendHistoryVip : sendHistoryVips) {
                sendHistory.append(sendHistoryVip.getVipPhone()).append(Constants.COMMA);
            }
            if (sendHistory.length() > 0) {
                sendHistory.deleteCharAt(sendHistory.length() - 1);
            }
        }
        vipMsgSendingHistory.setShowPhone(sendHistory.toString());
        return vipMsgSendingHistory;
    }
}