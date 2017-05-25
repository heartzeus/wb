package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.SendToVip;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.ISendToVipService;
import com.tuhanbao.base.dataservice.filter.Filter;

@Service("sendToVipService")
@Transactional("cfcrmTransactionManager")
public class SendToVipServiceImpl extends ServiceImpl<SendToVip> implements ISendToVipService {

    @Autowired
    private IMsgAutoSendService msgAutoSendService;

    @Override
    public void deleteAll(List<Long> ids) {
        Filter sendReadyHistory = new Filter().andFilter(TableConstants.T_SEND_TO_VIP.MSG_ID, ids);
        Filter vipMsgSending = new Filter().andFilter(TableConstants.T_MSG_AUTO_SEND.ID, ids);
        this.delete(sendReadyHistory);
        msgAutoSendService.delete(vipMsgSending);
    }
}