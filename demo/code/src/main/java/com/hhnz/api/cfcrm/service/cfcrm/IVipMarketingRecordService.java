package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.VipMarketingRecord;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IVipMarketingRecordService extends IService<VipMarketingRecord> {
    
    List<VipMarketingRecord> selectVipMarketingRecord(Filter filter);
}