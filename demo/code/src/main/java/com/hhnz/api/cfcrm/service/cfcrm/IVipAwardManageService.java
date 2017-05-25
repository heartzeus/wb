package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.Date;
import java.util.List;

import com.hhnz.api.cfcrm.constants.enums.SendType;
import com.hhnz.api.cfcrm.model.cfcrm.AwardVip;
import com.hhnz.api.cfcrm.model.cfcrm.VipAwardManage;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IVipAwardManageService extends IService<VipAwardManage> {
    
    List<VipAwardManage> selectVipAward(Filter filter);
    
    List<VipAwardManage> selectAllNeedSendAward(long time);

    void updateManager(VipAwardManage vam, List<AwardVip> vips);
    
    void sendAward(VipAwardManage vipAwardManage);

    void sendAward(List<VipPrimaryInfo> persons, int amount, String couponGroupId, SendType sendType, String awardName, Date sendAwardDate,
            long userId);

    Object getJumuAward();


}