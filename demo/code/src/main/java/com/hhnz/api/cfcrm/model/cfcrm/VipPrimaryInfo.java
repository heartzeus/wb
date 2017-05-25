package com.hhnz.api.cfcrm.model.cfcrm;

public class VipPrimaryInfo extends VipPrimaryInfoMO {
    
    private VipInvestInfo currentMonthInvestInfo;
    
    private VipInvestInfo currentYearInvestInfo; 
    
    private String flag; 
    
    private int rank;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public VipPrimaryInfo() {

    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
    
    public VipInvestInfo getCurrentMonthInvestInfo() {
        return currentMonthInvestInfo;
    }

    public void setCurrentMonthInvestInfo(VipInvestInfo currentMonthInvestInfo) {
        this.currentMonthInvestInfo = currentMonthInvestInfo;
    }

    public VipInvestInfo getCurrentYearInvestInfo() {
        return currentYearInvestInfo;
    }

    public void setCurrentYearInvestInfo(VipInvestInfo currentYearInvestInfo) {
        this.currentYearInvestInfo = currentYearInvestInfo;
    }

}