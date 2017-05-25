/**
 * 
 */
package com.hhnz.api.cfcrm.adapter;

import java.util.List;

import com.hhnz.api.cfcrm.model.cfcrm.Area;

/**
 * 2016年11月28日
 * 
 * @author liuhanhui
 */
public class FZAreaAdapter {

    private String areaId;
    
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String countyId;
    private String countyName;
    private String streetId;
    private String streetName;
    
    public FZAreaAdapter() {
        
    }
    
    public FZAreaAdapter(String areaId, List<Area> areas) {
        this.areaId = areaId;
        this.provinceId = getAreaId(areas, 0);
        this.provinceName = getAreaName(areas, 0);
        this.cityId = getAreaId(areas, 1);
        this.cityName = getAreaName(areas, 1);
        this.countyId = getAreaId(areas, 2);
        this.countyName = getAreaName(areas, 2);
        this.streetId = getAreaId(areas, 3);
        this.streetName = getAreaName(areas, 3);
    }
    
    public static Area getArea(List<Area> areas, int level) {
        if (areas == null || areas.size() <= level) return null;
        return areas.get(level);
    }
    
    private static String getAreaId(List<Area> areas, int level) {
        Area area = getArea(areas, level);
        if (area == null) return null;
        else return area.getAreaId();
    }
    
    private static String getAreaName(List<Area> areas, int level) {
        Area area = getArea(areas, level);
        if (area == null) return null;
        else return area.getAreaName();
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getStreetId() {
        return streetId;
    }

    public void setStreetId(String streetId) {
        this.streetId = streetId;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

}
