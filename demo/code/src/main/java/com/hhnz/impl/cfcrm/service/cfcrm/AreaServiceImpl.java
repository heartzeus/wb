package com.hhnz.impl.cfcrm.service.cfcrm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.adapter.FZAreaAdapter;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.Area;
import com.hhnz.api.cfcrm.service.cfcrm.IAreaService;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.base.util.objutil.StringUtil;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("areaService")
@Transactional("cfcrmTransactionManager")
public class AreaServiceImpl extends ServiceImpl<Area> implements IAreaService {
    
    private static MyBatisSelector FZ_AREA_SELECTOR = new MyBatisSelector(TableConstants.T_AREA.TABLE, "T1");
    
    private static final String CHINA_AREA_ID = "0";
    
    static {
        FZ_AREA_SELECTOR.joinTable(TableConstants.T_AREA.TABLE, "T2").joinTable(TableConstants.T_AREA.TABLE, "T3");
    }
    
//    @Override
//    public List<Map<String, Object>> getPCDareaId(String areaId){
//        //TODO 性能问题，接口返回参数 cfcrm tables中 缓存将T_AREA设置成ALL，提升性能 @WB看下呢
//        String sql = "SELECT " +
//                     " A .AREA_ID AS DISTRICT_ID, " +
//                     " B.AREA_ID AS CITY_ID, " + 
//                     " C.AREA_ID AS PROVINCE_ID " +
//                     " FROM " +
//                     " T_AREA A " +
//                     " LEFT JOIN T_AREA B ON A .PARENT_ID = B.AREA_ID " + 
//                     " LEFT JOIN T_AREA C ON B.PARENT_ID = C.AREA_ID " +
//                     " WHERE A.AREA_ID = " + areaId;
//        List<Map<String, Object>> list = this.excuteSql(sql);
//        return list;
//    }

    public List<Area> getAllAreas() {
        return this.select(FZ_AREA_SELECTOR, new Filter().andFilter(FZ_AREA_SELECTOR.getTable().getColumn(TableConstants.T_AREA.PARENT_ID), 0));
    }

    /*
     * 不论传入的是哪级地址，都返回所有地域信息（如传入二级地址，则返回省和市）
     * 
     * @see com.hhnz.api.yyt.service.fz.IAreaService#getAreaDetailByAreaId(long)
     */
    @Override
    public FZAreaAdapter getAreaDetailByAreaId(String areaId) {
        if (StringUtil.isEmpty(areaId)) return null;
        List<Area> areas = new ArrayList<Area>();
        // 县
        Area area = getAreaByAreaId(areaId);
        while (area != null) {
            //从前面插入
            areas.add(0, area);
            String parentId = area.getParentId();
            if (StringUtil.isEmpty(parentId) || CHINA_AREA_ID.equals(parentId)) {
                break;
            }
            area = getAreaByAreaId(parentId);
        }
        if (areas.isEmpty()) return null;
        return new FZAreaAdapter(areaId, areas);
    }

    @Override
    public Area getAreaByAreaId(String areaId) {
        if (StringUtil.isEmpty(areaId)) throw new MyException(ErrorCode.ILLEGAL_ARGUMENT);

        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_AREA.AREA_ID, areaId);
        List<Area> listArea = this.select(filter);
        if (listArea.size() == 1) {
            return listArea.get(0);
        }
        return null;
    }
}