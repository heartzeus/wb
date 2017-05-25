package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.SendToVip;
import com.hhnz.api.cfcrm.model.cfcrm.VipMsgSendingHistory;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IVipMsgSendingHistoryService extends IService<VipMsgSendingHistory> {
   
    public void recordHistory(Filter filter);

    public void saveHistory(MsgAutoSend mas, List<SendToVip> vips);
    
    public List<VipMsgSendingHistory> selectVipMsgHistory(Filter filter, boolean hasTelPhone);
    
    public VipMsgSendingHistory selectHistoryDetail(long id);
}