package com.hhnz.impl.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.constants.enums.SendStatus;
import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.SendHistoryVip;
import com.hhnz.api.cfcrm.model.cfcrm.SendToVip;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.ISendHistoryVipService;
import com.tuhanbao.base.util.thread.ExcuteOnceTimerTask;
import com.tuhanbao.thirdapi.sms.SmsUtil;

public class AutoSendMsgTask extends ExcuteOnceTimerTask {

    private MsgAutoSend mas;
    
    private IMsgAutoSendService msgAutoSendService;

    private ISendHistoryVipService sendHistoryVipService;

//    public AutoSendMsgTask(MsgAutoSend mas, IMsgAutoSendService serverManger) {
//        this.mas = mas;
//        this.msgAutoSendService = serverManger;
//    }
    public AutoSendMsgTask(MsgAutoSend mas, IMsgAutoSendService serverManger, ISendHistoryVipService sendHistoryVipService) {
        this.mas = mas;
        this.msgAutoSendService = serverManger;
        this.sendHistoryVipService = sendHistoryVipService;
    }

    @Override
    protected void runTask() throws Exception {
        // List<SendToVip> children = mas.getSendToVips();
        //
        // int size = children.size();
        // int index = 0;
        // String content = mas.getContent();
        //
        // boolean sendSuccess = true;
        // while (index < size) {
        // int end = index + ConstantsConfig.SEND_SMS_MAX_NUM;
        // if (end > size) end = size;
        // List<SendToVip> subList = children.subList(index, end);
        // String tels = getTels(subList);
        // if (!SmsUtil.sendMsg(tels, content)) {
        // sendSuccess = false;
        // }
        // else {
//         msgAutoSendService.saveHistory(mas, subList);
        // }
        // index += ConstantsConfig.SEND_SMS_MAX_NUM;
        // }
        // if (sendSuccess) {
        // mas.setSendStatus(SendStatus.SEND_SUCCESS);
        // }
        // else {
        // mas.setSendStatus(SendStatus.SEND_FAIL);
        // }
        // msgAutoSendService.update(mas);
        List<SendToVip> children = mas.getSendToVips();
        boolean sendSuccess = true;
        String content = mas.getContent();
        for (SendToVip sendToVip : children) {
            SendHistoryVip sendHistoryVip = new SendHistoryVip();
            if (!SmsUtil.sendMsg(sendToVip.getVipPhone(), content)) {
                sendHistoryVip.setCustomId(sendToVip.getCustomId());
                sendHistoryVip.setSendStatus(SendStatus.SEND_FAIL);
                sendHistoryVip.setVipPhone(sendToVip.getVipPhone());
                sendHistoryVip.setMsgId(sendToVip.getMsgId());
                sendHistoryVipService.add(sendHistoryVip);
                sendSuccess = false;
            }
            else {
                sendHistoryVip.setCustomId(sendToVip.getCustomId());
                sendHistoryVip.setVipPhone(sendToVip.getVipPhone());
                sendHistoryVip.setSendStatus(SendStatus.SEND_SUCCESS);
                sendHistoryVip.setMsgId(sendToVip.getMsgId());
                sendHistoryVipService.add(sendHistoryVip);
            }
        }
        if (sendSuccess) {
            mas.setSendStatus(SendStatus.SEND_SUCCESS);
        }
        else {
            mas.setSendStatus(SendStatus.SEND_FAIL);
        }
        msgAutoSendService.update(mas);
    }

    // private String getTels(List<SendToVip> children) {
    // StringBuilder sb = new StringBuilder();
    // for (SendToVip item : children) {
    // sb.append(item.getVipPhone()).append(Constants.COMMA);
    // }
    //
    // if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
    // return sb.toString();
    // }
}
