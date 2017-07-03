package com.hhnz.api.cfcrm.tool;

import java.io.InputStream;

import com.tuhanbao.base.util.io.excel.Excel2007Util;

public class ImportTool {
    public static void importCustomInfo(InputStream is) {
        String[][] importInfo = Excel2007Util.read(is, 0);
        

        //1.将所有primaryInfo置为false
        
        //2.遍历所有数据
        
    }

    public static void importInvestInfo(InputStream is) {

    }

    public static void importBackMoneyInfo(InputStream is) {

    }
}
