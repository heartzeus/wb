package com.hhnz.impl.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.constants.enums.SendStatus;
import com.hhnz.api.cfcrm.model.cfcrm.AwardItem;
import com.hhnz.api.cfcrm.model.cfcrm.AwardVip;
import com.hhnz.api.cfcrm.model.cfcrm.VipAwardManage;
import com.hhnz.api.cfcrm.service.cfcrm.IVipAwardManageService;
import com.tuhanbao.util.thread.ExcuteOnceTimerTask;

public class AutoSendAwardTask extends ExcuteOnceTimerTask {
    
    private VipAwardManage vam;
    
    private IVipAwardManageService vipAwardManageService;
    
    public AutoSendAwardTask(VipAwardManage vam, IVipAwardManageService serverManger) {
        this.vam = vam;
        this.vipAwardManageService = serverManger;
    }

    @Override
    protected void runTask() throws Exception {
        List<AwardVip> vips = vam.getAwardVips();
        List<AwardItem> items = vam.getAwardItems();
        
        if (items == null || items.isEmpty()) return;
        
        boolean sendSuccess = true;
        for (AwardItem item : items) {
            for (AwardVip vip : vips) {
                // TODO Need to rewrite.
                boolean result = true;
//                boolean result = AwardApiManager.sendAwardItem(vip.getCustom(), item);
            
                if (!result) {
                    sendSuccess = false;
                    vip.setSendStatus(SendStatus.SEND_FAIL);
                }
                else {
                    vip.setSendStatus(SendStatus.SEND_SUCCESS);
                }
            }
        }
        
        if (sendSuccess) {
            vam.setSendStatus(SendStatus.SEND_SUCCESS);
        }
        else {
            vam.setSendStatus(SendStatus.SEND_FAIL);
        }
        vipAwardManageService.updateManager(vam, vips);
    }
    
  

    
//  @Override
//private void sendAwardItem(List<VipPrimaryInfo> persons, AwardItem awardItem) {
//    AwardType awardType = awardItem.getAwardType();
//    if (awardType == AwardType.FOOD_STAMPS) {
//        //TODO
//    }
//    else if (awardType == AwardType.FOOD_STAMPS){
//        
//    }
//}
    
}
