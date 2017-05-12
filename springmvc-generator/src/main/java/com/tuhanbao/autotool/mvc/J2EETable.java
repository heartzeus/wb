package com.tuhanbao.autotool.mvc;

import java.util.ArrayList;
import java.util.List;

import com.tuhanbao.Constants;
import com.tuhanbao.autotool.mvc.clazz.ClazzCreator;
import com.tuhanbao.io.base.BinaryUtil;
import com.tuhanbao.io.impl.codeUtil.Xls2CodeUtil;
import com.tuhanbao.io.impl.tableUtil.ColumnEntry;
import com.tuhanbao.io.impl.tableUtil.ImportColumn;
import com.tuhanbao.io.impl.tableUtil.ImportTable;
import com.tuhanbao.io.impl.tableUtil.Relation;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.db.table.CacheType;

public class J2EETable implements Comparable<J2EETable>{
	/*
	 * j2ee的一些属性
	 */
	//对象所在模块
	private String module;
	
	private String modelName;
	
	private boolean hasBlobCol = false;
	
	public ImportTable table;
	
	public J2EETable(ImportTable table) {
		this.table = table;
		
		//最前面的一般是T_,I_是不需要解析的
		String tableName = table.name;
		if (table.name.startsWith("T_") || table.name.startsWith("I_")) {
			tableName = table.name.substring(2);
		}
		modelName = ClazzCreator.getClassName(tableName);
		

    	J2EETableManager.addTable(this);
	}
	
	public J2EETable(ImportTable table, String module) {
		this(table);
		this.module = module;
	}

	public void addColumn(ImportColumn col) {
		table.addColumn(col);
		
		if (col.isBlob()) {
			this.hasBlobCol = true;
		}
	}
	
	public void setModule(String module) {
		this.module = module;
	}

	public String getModule() {
		return this.module;
	}

	public String getModelName() {
		return this.modelName;
	}

	public String getExampleName() {
		return this.modelName + "Example";
	}

	public String getMapperName() {
		return this.modelName + "Mapper";
	}
	
	public String getIServiceName() {
		return "I" + this.modelName + "Service";
	}

	public String getServiceName() {
		return this.modelName + "ServiceImpl";
	}
	
	public String getControllerName() {
		return this.modelName + "Controller";
	}
	
	public boolean needC() {
		return !BinaryUtil.isZero(table.getTableConfig().getCrud(), 3);
	}
	public boolean needR() {
		return !BinaryUtil.isZero(table.getTableConfig().getCrud(), 2);
	}
	public boolean needU() {
		return !BinaryUtil.isZero(table.getTableConfig().getCrud(), 1);
	}
	public boolean needD() {
		return !BinaryUtil.isZero(table.getTableConfig().getCrud(), 0);
	}
	
	public boolean hasBlobCol() {
		return this.hasBlobCol;
	}

	public void setPK(ImportColumn column) {
		table.setPK(column);
	}

	public String getName() {
		return table.getName();
	}

	public List<ImportColumn> getColumns() {
		return table.getColumns();
	}

	public ImportColumn getPK() {
		return table.getPK();
	}

	public String getSeqName() {
		return table.getSeqName();
	}

	public boolean needCutPage() {
		return table.getTableConfig().isNeedCutPage();
	}

	public ImportColumn getDefaultOrderCol() {
		return table.getColumn(table.getTableConfig().getDefaultOrderColName());
	}
	
	public List<ColumnEntry[]> getSelectKeys() {
		List<ColumnEntry[]> list = new ArrayList<ColumnEntry[]>();
		if (this.table.getTableConfig().getSelectKeys() != null) {
			for (String[] array : this.table.getTableConfig().getSelectKeys()) {
				int length = array.length;
				ColumnEntry[] entrys = new ColumnEntry[length];
				for (int i = 0; i < length; i++) {
					String s = array[i];
					ImportColumn col = null;
					//默认1对多
					Relation relation = Relation.One2N;
					//格式  fk_id(1:1)  
					int index = s.indexOf(Constants.LEFT_PARENTHESE);
					if (index == -1) {
						col = table.getColumn(s);
					} else {
						String colName = s.substring(0, index);
						col = table.getColumn(colName);
						int endIndex = s.indexOf(Constants.RIGHT_PARENTHESE);
						relation = Relation.getRelation(s.substring(index + 1, endIndex));
					}
					entrys[i] = new ColumnEntry(col, relation);
				}
				list.add(entrys);
			}
		}
		return list;
	}

	public String getTableName() {
		return table.getTableName();
	}

	public boolean isView() {
		return table.isView();
	}
    
    public String toString(J2EEProjectInfo project)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Xls2CodeUtil.getGap(1) + "public static final class " + getTableName());
        sb.append(Constants.BLANK).append("{").append(Constants.ENTER);
        List<ImportColumn> columns = table.getColumns();
        sb.append(Xls2CodeUtil.getGap(2) + "public static final Table TABLE = new Table(").append(columns.size()).append(", \"")
        		.append(getName().toUpperCase()).append("\", ")
                .append("CacheType.").append(table.getCacheTypeStr())
                .append(", \"").append(project.getServiceBeanPath(this.getModule())).append(".").append(modelName).append("\"");
        if (!StringUtil.isEmpty(this.getSeqName())) {
        	sb.append(", \"").append(this.getSeqName()).append("\"");
        }
        sb.append(");").append(Constants.ENTER);
        sb.append(Constants.ENTER);
        for (ImportColumn c : columns)
        {
            sb.append(c.toString()).append(Constants.ENTER);
        }
        
        sb.append(Xls2CodeUtil.getGap(1) + "}").append(Constants.ENTER);
        
        return sb.toString();
    }
	
	public String getComment(){
		return table.getComment();
	}

	public List<ImportColumn> getFKColumns() {
		return this.table.getFKColumns();
	}

	public CacheType getCacheType() {
		return table.getCacheType();
	}
	
	public ImportTable getTable() {
		return this.table;
	}

    @Override
    public int compareTo(J2EETable o) {
        if (this.getName() == null) return -1;
        return this.getName().compareTo(o.getName());
    }
}
