package com.hhnz.impl.cfcrm.service.fz;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.adapter.FZAreaAdapter;
import com.hhnz.api.cfcrm.constants.enums.OfficeType;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.model.fz.Office;
import com.hhnz.api.cfcrm.service.cfcrm.IAreaService;
import com.hhnz.api.cfcrm.service.fz.IOfficeService;
import com.hhnz.api.cfcrm.tool.ExportDataConverter;
import com.tuhanbao.base.util.objutil.TimeUtil;

@Service("officeService")
@Transactional("fzTransactionManager")
public class OfficeServiceImpl extends ServiceImpl<Office> implements IOfficeService {

    @Autowired
    private IAreaService areaService;
    
    @Override
    public void getTypeAndAreaById(VipPrimaryInfo vipPrimaryInfo){
        int[] ymd = TimeUtil.getTodayYearMonthDayHour();
        String sql = " SELECT c.OFFICE_TYPE, c.AREA_ID,r.VIP, y.customer_id FROM" +
                     " \"OFFICE\" c INNER JOIN \"OFFICE_RELATION\" n ON c.OFFICE_ID = n.OFFICE_ID " +
                     " INNER JOIN \"C_EXTEND_INFO\" o ON n.NONGZHUANG_ID = o.NONGZHUANG_ID " +
                     " INNER JOIN \"T_SEPARATE_BILLS_APPLY\" y ON n.NONGZHUANG_ID = y.APPLY_ID " +
                     " LEFT JOIN ( " +
                     " SELECT " + 
                     " E .NONGZHUANG_ID, " +
                     " E .VIP " + 
                     " FROM " +
                     " \"ROLE\" E " +
                     " WHERE " +
                     " E . YEAR =  " + ymd[0] +
                     " AND E . MONTH =  " + ymd[1] +
                     " ) r ON r.NONGZHUANG_ID = n.NONGZHUANG_ID " +
                     " WHERE y.customer_id = " + vipPrimaryInfo.getId();
        List<Map<String, Object>> list = this.excuteSql(sql);
        if (list != null && !list.isEmpty()) {
            Map<String, Object> map = list.get(0);
            String areaId = map.get("AREA_ID").toString();
            FZAreaAdapter area = areaService.getAreaDetailByAreaId(areaId);
            if (area != null) {
                vipPrimaryInfo.setVipDistrictId(areaId);
                vipPrimaryInfo.setVipCityId(area.getCityId());
                vipPrimaryInfo.setVipProvinceId(area.getProvinceId());
            }
            if (map.get("VIP") == null) {
                vipPrimaryInfo.setBeVip(false);
            }else {
                vipPrimaryInfo.setBeVip(Boolean.parseBoolean((String)map.get("VIP")));
            }
            
            if (list.size() == 1) {
                // rewrite 根据key寻找value
                vipPrimaryInfo.setOfficeType(ExportDataConverter.officeTypeConverter(map.get("OFFICE_TYPE").toString()));
            }
            else if (list.size() == 2) {
                vipPrimaryInfo.setOfficeType(OfficeType.A);
            }

        }
    }
}