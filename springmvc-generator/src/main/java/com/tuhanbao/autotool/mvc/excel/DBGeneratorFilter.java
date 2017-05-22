package com.tuhanbao.autotool.mvc.excel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.tuhanbao.Constants;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.autotool.mvc.ModuleManager;
import com.tuhanbao.base.chain.Context;
import com.tuhanbao.base.chain.FilterAnnotation;
import com.tuhanbao.io.impl.tableUtil.DBType;
import com.tuhanbao.io.impl.tableUtil.ImportColumn;
import com.tuhanbao.io.impl.tableUtil.ImportTable;
import com.tuhanbao.io.impl.tableUtil.TableConfig;
import com.tuhanbao.io.impl.tableUtil.src.TableSrcUtilFactory;
import com.tuhanbao.io.objutil.ArrayUtil;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.config.ConfigManager;
import com.tuhanbao.util.config.ConfigPattern;
import com.tuhanbao.util.db.conn.DBSrc;
import com.tuhanbao.util.db.table.CacheType;

@FilterAnnotation("db")
public class DBGeneratorFilter extends ExcelAGCFilter {

    @Override
    public void filter(Context context) {
        for (ConfigPattern cp : ConfigManager.getAllConfigPattern()) {
            String[][] arrays = removeConfig(context, DB + cp.getSuffix());
            if (arrays == null || arrays.length == 0) continue;
            
            int length = arrays.length;
            for (int i = 1; i < length; i++) {
                String[] array = arrays[i];
                String module = null;
                if (!StringUtil.isEmpty(array[0])) {
                    module = array[0];
                }
                DBSrc src = new DBSrc(getDriver(array[1]), array[2], array[3], array[4], 0);
                ModuleManager.addModule(cp, module, src);
            }
        }
        
        List<J2EETable> tables = initTables(context);
        Collections.sort(tables);
        context.putAttr(TABLES, tables);
    }
    
    private List<J2EETable> initTables(Context context) {
        List<J2EETable> list = new ArrayList<J2EETable>();
        Map<String, String[]> tableConfigs = getTableConfigs(context);
        for (String tableName : tableConfigs.keySet()) {
            String[][] arrays = removeConfig(context, tableName);
            list.add(getTable(tableName, tableConfigs.get(tableName), arrays));
        }
        
        //这里需要初始化一下所有的table外键
        for (J2EETable table : list) {
            List<ImportColumn> cols = table.getColumns();
            for (ImportColumn ic : cols) {
                ic.getFkColumn();
            }
        }
        return list;
    }
    
    private J2EETable getTable(String tableName, String[] tableConfig, String[][] arrays) {
        //tableConfig
        //#表名 中文描述    缓存(NOT/ALL/AUTO)默认为NO   序列号（oracle数据库可配置，非必填，填autocreate，会根据表名自动生成序列）   默认排序字段（可不填）
        //T_CRAWLER_CLASS     NO  autocreate  
        String comment = ArrayUtil.indexOf(tableConfig, 1);
        String module = ArrayUtil.indexOf(tableConfig, 2);
        CacheType cacheType = CacheType.getCacheType(ArrayUtil.indexOf(tableConfig, 3));
        String seq = ArrayUtil.indexOf(tableConfig, 4);
        String orderCol = ArrayUtil.indexOf(tableConfig, 5);
        DBType dbType = ModuleManager.getDBSrc(module).getDbType();

        ImportTable table = TableSrcUtilFactory.getTable(tableName, arrays, dbType);
        table.setComment(comment);
        TableConfig tcfg = new TableConfig();
        tcfg.setCacheType(cacheType);
        tcfg.setDefaultOrderColName(orderCol);
        if (dbType == DBType.ORACLE) {
            tcfg.setSequence(getSeqName(seq, tableName));
        }
        else if (dbType == DBType.MYSQL) {
            tcfg.setAutoIncrement(Constants.AUTOCREATE.equalsIgnoreCase(seq));
        }
        table.setTableConfig(tcfg);
        return new J2EETable(table, module);
    }
    
    private static String getSeqName(String seqName, String tableName) {
        if (Constants.AUTOCREATE.equalsIgnoreCase(seqName)) {
            tableName = tableName.toUpperCase();
            if (tableName.startsWith("T_") || tableName.startsWith("V_")) tableName = tableName.substring(2);
            return "SEQ_" + tableName;
        }
        else {
            return seqName;
        }
    }
    
    private static String getDriver(String dbType) {
        if ("oracle".equalsIgnoreCase(dbType)) {
            return "oracle.jdbc.driver.OracleDriver";
        }
        return "com.mysql.jdbc.Driver";
    }

}
