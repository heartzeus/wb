package com.hhnz.api.cfcrm.model.cfcrm;

public class AwardItemInfo {
    private int index;
    
    private String desc;
    
    private String pngUrl;
    
    private String unit;

    public AwardItemInfo(int index, String desc, String unit, String pngUrl) {
        this.index = index;
        this.desc = desc;
        this.unit = unit;
        this.pngUrl = pngUrl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPngUrl() {
        return pngUrl;
    }

    public void setPngUrl(String pngUrl) {
        this.pngUrl = pngUrl;
    }
}
