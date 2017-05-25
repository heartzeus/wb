package com.hhnz.api.cfcrm.constants;

import java.util.HashMap;
import java.util.Map;

import com.hhnz.api.cfcrm.constants.enums.FilterType;
import com.tuhanbao.base.dataservice.filter.JoinType;
import com.tuhanbao.web.filter.MyBatisSelector;

public class SelectorConstants {
    
    public static final MyBatisSelector VIP_INVEST_SELECTOR = new MyBatisSelector(TableConstants.T_VIP_PRIMARY_INFO.TABLE);

    public static final Map<FilterType, MyBatisSelector> CACHE = new HashMap<FilterType, MyBatisSelector>();

    static {
        VIP_INVEST_SELECTOR.joinTable(TableConstants.T_VIP_INVEST_INFO.TABLE, JoinType.INNER_JOIN);
        
        CACHE.put(FilterType.CUSTOM, VIP_INVEST_SELECTOR);
        CACHE.put(FilterType.INVEST, VIP_INVEST_SELECTOR);
    }

    private SelectorConstants() {
        
    }
    
    public static MyBatisSelector getSelector(FilterType filterType) {
        return CACHE.get(filterType);
    }
}
