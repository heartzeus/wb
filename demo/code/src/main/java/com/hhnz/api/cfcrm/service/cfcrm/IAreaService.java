package com.hhnz.api.cfcrm.service.cfcrm;

import java.util.List;

import com.hhnz.api.cfcrm.adapter.FZAreaAdapter;
import com.hhnz.api.cfcrm.model.cfcrm.Area;
import com.tuhanbao.web.service.IService;

public interface IAreaService extends IService<Area> {
    public FZAreaAdapter getAreaDetailByAreaId(String areaId);
    
    public Area getAreaByAreaId(String areaId);

    List<Area> getAllAreas(); 
}