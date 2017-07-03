package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.FilterType;
import com.hhnz.api.cfcrm.model.cfcrm.DiyFilter;
import com.hhnz.api.cfcrm.model.cfcrm.DiyFilterItem;
import com.hhnz.api.cfcrm.service.cfcrm.IDiyFilterItemService;
import com.hhnz.api.cfcrm.service.cfcrm.IDiyFilterService;
import com.tuhanbao.base.Constants;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.LogicType;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.base.util.db.table.Column;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.base.util.objutil.StringUtil;
import com.tuhanbao.web.filter.FilterField;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("diyFilterService")
@Transactional("cfcrmTransactionManager")
public class DiyFilterServiceImpl extends ServiceImpl<DiyFilter> implements IDiyFilterService {
    
    private static final MyBatisSelector SELECTOR = new MyBatisSelector(TableConstants.T_DIY_FILTER.TABLE);
    
    @Autowired
    private IDiyFilterItemService itemService;
    
    static {
        SELECTOR.joinTable(TableConstants.T_DIY_FILTER_ITEM.TABLE);
    }
    
    public Filter getFilter(Long filterId) {
        if (filterId == null) return new Filter();
        
        DiyFilter filter = getDiyFilter(filterId);
        Filter result = new Filter();
        if (filter == null) return result;
        
        List<DiyFilterItem> diyFilterItems = filter.getDiyFilterItems();
        if (diyFilterItems == null || diyFilterItems.isEmpty()) return result;
        
        for (DiyFilterItem item : diyFilterItems) {
            String name = item.getName();
            Operator operator = item.getOperator();
            String value = item.getValue();
            
            String[] tableAndCol = StringUtil.string2Array(name);
            Column col = TableConstants.getTableByClassName(tableAndCol[0]).getColumn(tableAndCol[1]);
            if (item.getLogicType() == LogicType.AND) {
                if (operator == Operator.LIKE) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Constants.PERCENT_SIGN).append(value).append(Constants.PERCENT_SIGN);
                    result.andFilter(Operator.LIKE, new FilterField(col, name), sb.toString());
                }
                else {
                    result.andFilter(operator, new FilterField(col, name), value);
                }
            }
            else {
                if (operator == Operator.LIKE) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Constants.PERCENT_SIGN).append(value).append(Constants.PERCENT_SIGN);
                    result.orFilter(Operator.LIKE, new FilterField(col, name), sb.toString());
                }
                else {
                    result.orFilter(operator, new FilterField(col, name), value);
                }
            }
        }
        
        return result;
    }

    @Override
    public DiyFilter getDiyFilter(long filterId) {
        List<DiyFilter> list = this.select(SELECTOR, new Filter().andFilter(TableConstants.T_DIY_FILTER.ID, filterId));
        if (list.isEmpty()) return null;
        return list.get(0);
    }
    
    @Override
    public void addFilter(FilterType ft, int sort, String name, DiyFilterItem[] items) {
        if (StringUtil.isEmpty(name) || ft == null) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        
        DiyFilter filter = new DiyFilter();
        filter.setFilterType(ft);
        filter.setName(name);
        filter.setSort(sort);
        this.add(filter);
        
        long filterId = filter.getId();
        
        for (DiyFilterItem item : items) {
            if (item == null) continue;
            item.setFilterId(filterId);
            itemService.add(item);
        }
    }

    @Override
    public void updateFilter(DiyFilter filter, DiyFilterItem[] diyItems) {
        this.update(filter);
        itemService.delete(filter.getDiyFilterItems());
        long filterId = filter.getId();
        for (DiyFilterItem item : diyItems) {
            item.setFilterId(filterId);
            itemService.add(item);
        }
    }

    @Override
    public void delFilter(long filterId) {
        this.deleteById(filterId);
        itemService.delete(new Filter().andFilter(TableConstants.T_DIY_FILTER_ITEM.FILTER_ID, filterId));
    }
}