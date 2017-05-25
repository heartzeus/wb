package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.constants.enums.SendType;
import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.PaymentCollection;
import com.hhnz.api.cfcrm.model.cfcrm.SendToVip;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IMsgAutoSendService extends IService<MsgAutoSend> {

    List<MsgAutoSend> selectAllNeedSendMsg(long time);

    MsgAutoSend selectBirthSendMsg();
    
    void updateBirthMsg(MsgAutoSend msgAutoSend);

    public MsgAutoSend selectReadyHistoryDetail(long id);
    
    void updateBirthSendMsgTimer();

    void saveHistory(MsgAutoSend mas, List<SendToVip> subList);

    void sendMsg(MsgAutoSend mas);

    List<MsgAutoSend> selectReadyHistory(Filter filter, boolean hasTelPhone);
    
    void sendMsg2Custom(List<VipPrimaryInfo> persons, String msgContent, String msgName, SendType sendType, String date);

    void sendMsg2CustomPayBack(List<PaymentCollection> persons, String msgContent, String msgName, SendType sendType, String date);
}