package com.hhnz.impl.cfcrm.awardapi;

import com.hhnz.api.cfcrm.model.cfcrm.AwardItem;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;

public interface IAwardApi {
//    boolean sendAward(List<AwardVip> persons, AwardItem item);    

    boolean sendAward(VipPrimaryInfo vipPrimaryInfo, AwardItem awardItem);
}
