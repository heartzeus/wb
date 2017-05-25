package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestInfoService;
import com.hhnz.api.cfcrm.tool.TimeTool;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("vipInvestInfoService")
@Transactional("cfcrmTransactionManager")
public class VipInvestInfoServiceImpl extends ServiceImpl<VipInvestInfo> implements IVipInvestInfoService {

    private static final MyBatisSelector INFO_SELECTOR = new MyBatisSelector(TableConstants.T_VIP_INVEST_INFO.TABLE);

    static {
        INFO_SELECTOR.joinTable(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hhnz.api.cfcrm.service.cfcrm.IVipInvestInfoService#
     * getVipDailyDesUpSomePercent() 获取投资降幅大于某个百分比的客户总数
     */
    @Override
    public int getVipDailyDesUpSomePercent() {
        Filter filter4VipDaily = new Filter();
        filter4VipDaily.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_INFO.DAILY_DECREASEMENT, ConstantsConfig.REMIND_DROP_THRESHOLD);
        int[] currentTime = TimeTool.getCurentYMD();

        filter4VipDaily.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, currentTime[0]);
        filter4VipDaily.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, currentTime[1]);
        filter4VipDaily.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);

        List<VipInvestInfo> list4VipDaily = select(INFO_SELECTOR, filter4VipDaily);
        return list4VipDaily.size();

    }

    @Override
    public List<VipInvestInfo> selectInvestInfo(Filter filter) {
        List<VipInvestInfo> VipInvestInfos = this.select(INFO_SELECTOR, filter);
        return VipInvestInfos;
    }
}