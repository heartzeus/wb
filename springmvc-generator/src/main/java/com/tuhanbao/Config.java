package com.tuhanbao;

import java.util.List;

/**
 * Created by yang on 2016/6/21.
 */
public class Config {
    private String outDir;
    private String cTemplate;
    private String sTemplate;
    private String iTemplate;

    public String getcTemplate() {
        return cTemplate;
    }

    public void setcTemplate(String cTemplate) {
        this.cTemplate = cTemplate;
    }

    public String getsTemplate() {
        return sTemplate;
    }

    public void setsTemplate(String sTemplate) {
        this.sTemplate = sTemplate;
    }

    public String getiTemplate() {
        return iTemplate;
    }

    public void setiTemplate(String iTemplate) {
        this.iTemplate = iTemplate;
    }

    private List<Pack> packList;
    private List<Bean> beanList;

    public String getOutDir() {
        return outDir;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public List<Pack> getPackList() {
        return packList;
    }

    public void setPackList(List<Pack> packList) {
        this.packList = packList;
    }

    public List<Bean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<Bean> beanList) {
        this.beanList = beanList;
    }
}

class Pack{
    private String name;
    private String type;
    private String out;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}

class Bean{
    private String name;
    private String type;
    private String out;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
