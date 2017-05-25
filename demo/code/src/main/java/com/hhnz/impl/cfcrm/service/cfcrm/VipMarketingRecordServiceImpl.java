package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.VipMarketingRecord;
import com.hhnz.api.cfcrm.service.cfcrm.IVipMarketingRecordService;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("vipMarketingRecordService")
@Transactional("cfcrmTransactionManager")
public class VipMarketingRecordServiceImpl extends ServiceImpl<VipMarketingRecord> implements IVipMarketingRecordService {

    private static final MyBatisSelector SELECTOR = new MyBatisSelector(TableConstants.T_VIP_MARKETING_RECORD.TABLE);

    static {
        SELECTOR.joinTable(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    }

    public List<VipMarketingRecord> selectVipMarketingRecord(Filter filter) {
        return this.select(SELECTOR, filter);
    }
}