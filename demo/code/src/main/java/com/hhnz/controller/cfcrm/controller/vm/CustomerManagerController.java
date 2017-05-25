package com.hhnz.controller.cfcrm.controller.vm;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.SelectorConstants;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.FilterType;
import com.hhnz.api.cfcrm.model.cfcrm.DiyFilter;
import com.hhnz.api.cfcrm.model.cfcrm.DiyFilterItem;
import com.hhnz.api.cfcrm.service.cfcrm.IDiyFilterService;
import com.hhnz.controller.cfcrm.controller.BaseController;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.LogicType;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.filter.DiyFilterUtil;

@RequestMapping(value = ("/custom"), produces = "text/html;charset=UTF-8")
@Controller
public class CustomerManagerController extends BaseController {
    /**
     * 获取所有的条件项
     */
    private static final String GET_AUTO_FILTER_ITEM = "getAutoFilterItem";

    /**
     * 获取所有的自定义过滤
     */
    private static final String LIST_ALL_FILTER = "listAllFilter";

    /**
     * 获取某个自定义过滤的所有条件项
     */
    private static final String LIST_FILTER_ITEMS = "listFilterItems";

    /**
     * 保存一个自定义过滤
     */
    private static final String SAVE_FILTER = "saveFilter";

    /**
     * 删除一个自定义过滤
     */
    private static final String DEL_FILTER = "delFilter";
    

    @Autowired
    private IDiyFilterService filterService;
    
    /**
     * 获取自定义过滤条件
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = LIST_ALL_FILTER)
    @ResponseBody
    public Object listAllFilter(HttpServletRequest request, int filterType) {
        FilterType ft = FilterType.getFilterType(filterType);
        if (ft == null) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        Filter filter = new Filter().andFilter(TableConstants.T_DIY_FILTER.FILTER_TYPE, filterType);
        filter.addOrderField(TableConstants.T_DIY_FILTER.SORT, false);
        return filterService.select(filter);
    }
    
    @RequestMapping(value = GET_AUTO_FILTER_ITEM)
    @ResponseBody
    public Object getAutoFilterItem(HttpServletRequest request, int filterType) {
        FilterType ft = FilterType.getFilterType(filterType);
        if (ft == null) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        return DiyFilterUtil.getItems(SelectorConstants.getSelector(ft));
    }
    
    @RequestMapping(value = LIST_FILTER_ITEMS)
    @ResponseBody
    public Object listFilterItems(HttpServletRequest request, long filterId) {
        DiyFilter filter = filterService.getDiyFilter(filterId);
        if (filter == null) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        return filter;
    }
    
    @RequestMapping(value = SAVE_FILTER)
    @ResponseBody
    public Object saveFilter(HttpServletRequest request, Integer filterType, Integer sort, String filterName, Long filterId, 
            String items) {
        if (filterId == null) {
            FilterType ft = FilterType.getFilterType(filterType);
            if (ft == null) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            String[][] arrays = StringUtil.string2Arrays(items, Constants.COMMA, Constants.EXCLAMATION);
            int length = arrays.length;
            DiyFilterItem[] diyItems = new DiyFilterItem[length];
            for (int i = 0; i < length; i++) {
                diyItems[i] = getFilterItem(arrays[i]);
            }
            filterService.addFilter(ft, sort, filterName, diyItems);
        }
        else {
            DiyFilter filter = filterService.selectById(filterId);
            if (filter == null) throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            String[][] arrays = StringUtil.string2Arrays(items, Constants.COMMA, Constants.EXCLAMATION);
            int length = arrays.length;
            filter.setName(filterName);
            filter.setSort(sort);
            DiyFilterItem[] diyItems = new DiyFilterItem[length];
            for (int i = 0; i < length; i++) {
                diyItems[i] = getFilterItem(arrays[i]);
            }
            filterService.updateFilter(filter, diyItems);
        }
        return NULL;
    }

    @RequestMapping(value = DEL_FILTER)
    @ResponseBody
    public Object delFilter(HttpServletRequest request, long filterId) {
        filterService.delFilter(filterId);
        return NULL;
    }

    private DiyFilterItem getFilterItem(String[] array) {
        if (array == null || array.length == 0) return null;
        DiyFilterItem item = new DiyFilterItem();
        item.setLogicType(LogicType.getLogicType(Integer.valueOf(array[0])));
        item.setName(array[1]);
        item.setOperator(Operator.getOperator(Integer.valueOf(array[2])));
        item.setValue(array[3]);
        return item;
    }
    

}
