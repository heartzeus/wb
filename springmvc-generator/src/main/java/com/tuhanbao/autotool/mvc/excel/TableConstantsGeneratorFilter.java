package com.tuhanbao.autotool.mvc.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tuhanbao.Constants;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.autotool.mvc.J2EETableManager;
import com.tuhanbao.autotool.mvc.SpringMvcProjectInfo;
import com.tuhanbao.base.chain.Context;
import com.tuhanbao.base.chain.FilterAnnotation;
import com.tuhanbao.base.chain.event.CreateFileEvent;
import com.tuhanbao.io.impl.codeUtil.Xls2CodeUtil;
import com.tuhanbao.io.impl.tableUtil.ImportColumn;
import com.tuhanbao.io.impl.tableUtil.TableUtil;
import com.tuhanbao.io.objutil.FileUtil;
import com.tuhanbao.io.objutil.OverwriteStrategy;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.log.LogManager;

@FilterAnnotation("tableConstants")
public class TableConstantsGeneratorFilter extends ExcelAGCFilter {

    @Override
    public void filter(Context context) {
        try {
            SpringMvcProjectInfo project = this.getProject(context);
            String url = project.getConstantsUrl();
            String fullPath = project.getConstantsPath();
            context.addEvent(new CreateFileEvent(FileUtil.appendPath(fullPath, Constants.TABLE_CONSTANTS_CLASS), 
                    getConstantClassStr(url, J2EETableManager.getAllTables().values(), project)));
            writeTableLanguage(project, context);
        }
        catch (Exception e) {
            LogManager.error(e);
        }
    }

    /**
     * 构造TableConstants文件
     * 
     * @param tables
     * @return
     * @throws IOException
     */
    private static String getConstantClassStr(String url, Collection<J2EETable> tables, SpringMvcProjectInfo project) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.PACKAGE).append(Constants.BLANK).append(url).append(
                Constants.SEMICOLON).append(Constants.ENTER).append(Constants.ENTER);
        
        sb.append("import java.util.HashMap;").append(Constants.ENTER);
        sb.append("import java.util.Map;").append(Constants.ENTER);
        sb.append("import com.tuhanbao.util.util.clazz.ClazzUtil;").append(Constants.ENTER);

        sb.append("import com.tuhanbao.io.impl.tableUtil.DataType;").append(Constants.ENTER);
        sb.append("import com.tuhanbao.io.impl.tableUtil.Relation;").append(Constants.ENTER);
        sb.append("import com.tuhanbao.util.db.table.CacheType;").append(Constants.ENTER);
        sb.append("import com.tuhanbao.util.db.table.Column;").append(Constants.ENTER);
        sb.append("import com.tuhanbao.util.db.table.ColumnFactory;").append(Constants.ENTER);
        sb.append("import com.tuhanbao.util.db.table.Table;").append(Constants.ENTER);
//        sb.append("import com.tuhanbao.util.db.table.TableRegister;").append(Constants.ENTER);
        sb.append(Constants.ENTER);
        sb.append(Xls2CodeUtil.getNotesInfo(Constants.CONSTANT_CLASS_NOTES, 0));
        sb.append(Constants.PUBLIC_CLASS).append(Constants.BLANK).append(TableUtil.TABLECONSTANTS_CLASS_NAME).append(
                Constants.BLANK);
        sb.append(Constants.LEFT_BRACE).append(Constants.ENTER);

        sb.append(Constants.GAP1).append("public static final Column COUNT = ColumnFactory.createColumn(null, \"COUNT(1)\", DataType.INT);").append(Constants.ENTER);
        sb.append(Constants.ENTER);
        sb.append(Constants.GAP1).append("public static final Map<String, Table> TABLES = new HashMap<String, Table>();").append(Constants.ENTER);
        sb.append(Constants.ENTER);
        sb.append(Constants.GAP1).append("static { init(); }").append(Constants.ENTER);
        sb.append(Constants.ENTER);
        sb.append(Constants.GAP1).append("private TableConstants() {}").append(Constants.ENTER);
        sb.append(Constants.ENTER);

        sb.append(Constants.GAP1).append("public static final void register(Table table)");
        sb.append(Constants.BLANK).append("{").append(Constants.ENTER);
        sb.append(Constants.GAP2).append("TABLES.put(getClassName(table.getName()), table);").append(Constants.ENTER);
        sb.append(Constants.GAP1).append("}").append(Constants.ENTER);
        sb.append(Constants.ENTER);

        sb.append(Constants.GAP1).append("public static Table getTableByClassName(String name)");
        sb.append(Constants.BLANK).append("{").append(Constants.ENTER);
        sb.append(Constants.GAP2).append("return TABLES.get(name);").append(Constants.ENTER);
        sb.append(Constants.GAP1).append("}").append(Constants.ENTER);
        sb.append(Constants.ENTER);

        sb.append(Constants.GAP1).append("private static String getClassName(String tableName)");
        sb.append(Constants.BLANK).append("{").append(Constants.ENTER);
        sb.append(Constants.GAP2).append("if (tableName.startsWith(\"T_\") || tableName.startsWith(\"I_\"))");
        sb.append(Constants.BLANK).append("{").append(Constants.ENTER);
        sb.append(Constants.GAP3).append("tableName = tableName.substring(2);").append(Constants.ENTER);
        sb.append(Constants.GAP2).append("}").append(Constants.ENTER);
        sb.append(Constants.GAP2).append("return ClazzUtil.getClassName(tableName);").append(Constants.ENTER);
        sb.append(Constants.GAP1).append("}").append(Constants.ENTER);
        sb.append(Constants.ENTER);

        sb.append(Constants.GAP1).append("public static void init()");
        sb.append(Constants.BLANK).append(Constants.LEFT_BRACE).append(Constants.ENTER);
        List<J2EETable> list = new ArrayList<J2EETable>();
        list.addAll(tables);
        Collections.sort(list);
        for (J2EETable table : list)
            sb.append(Constants.GAP2).append("register(").append(table.getTableName()).append(".TABLE);").append(
                    Constants.BLANK).append(Constants.ENTER);
        sb.append(Constants.GAP1).append(Constants.RIGHT_BRACE).append(Constants.ENTER);
        sb.append(Constants.ENTER);

        for (J2EETable t : list)
        {
            sb.append(t.toString(project)).append(Constants.ENTER);
        }
        sb.append(Constants.RIGHT_BRACE);
        return sb.toString();
    }
    
    private static void writeTableLanguage(SpringMvcProjectInfo project, Context context) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (J2EETable table : J2EETableManager.getAllTables().values()) {
            sb.append(getTableLanguage(table));
        }
        String url = FileUtil.appendPath(project.getRootPath(), project.getConfigUrl(),
                Constants.LANGUAGE_PROPERTIES);
        context.addEvent(new CreateFileEvent(url, sb.toString(), OverwriteStrategy.ADD));
    }
    
    private static String getTableLanguage(J2EETable table) {
        StringBuilder sb = new StringBuilder();
        String comment = table.getComment();
        String tableName = table.getName();
        if (!StringUtil.isEmpty(comment)) {
            sb.append(tableName + "=" + comment).append(Constants.ENTER);
        }
        for (ImportColumn column : table.getColumns()) {
            comment = column.getComment();
            if (!StringUtil.isEmpty(comment)) {
                sb.append(tableName).append(".").append(column.getName()).append("=").append(comment).append(Constants.ENTER);
            }
        }
        return sb.toString();
    }

}
