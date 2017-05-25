package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.SendHistoryVip;
import com.hhnz.api.cfcrm.service.cfcrm.ISendHistoryVipService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipMsgSendingHistoryService;
import com.tuhanbao.base.dataservice.filter.Filter;

@Service("sendHistoryVipService")
@Transactional("cfcrmTransactionManager")
public class SendHistoryVipServiceImpl extends ServiceImpl<SendHistoryVip> implements ISendHistoryVipService {
    
    @Autowired
    private IVipMsgSendingHistoryService msgSendingHistoryService;

    @Override
    public void deleteAll(List<Long> ids) {
        Filter sendHistory = new Filter().andFilter(TableConstants.T_SEND_HISTORY_VIP.MSG_ID, ids);
        Filter vipMsgSending = new Filter().andFilter(TableConstants.T_VIP_MSG_SENDING_HISTORY.ID, ids);
        this.delete(sendHistory);
        msgSendingHistoryService.delete(vipMsgSending);
    }
}