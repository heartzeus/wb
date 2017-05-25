package com.hhnz.api.cfcrm.service.cfcrm;

import com.hhnz.api.cfcrm.constants.enums.FilterType;
import com.hhnz.api.cfcrm.model.cfcrm.DiyFilter;
import com.hhnz.api.cfcrm.model.cfcrm.DiyFilterItem;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.web.service.IService;

public interface IDiyFilterService extends IService<DiyFilter> {
    Filter getFilter(Long filterId);
    
    void addFilter(FilterType ft, int sort, String name, DiyFilterItem[] items);

    void updateFilter(DiyFilter filter, DiyFilterItem[] diyItems);

    void delFilter(long filterId);

    DiyFilter getDiyFilter(long filterId);
}