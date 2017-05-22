package com.tuhanbao.io.impl.tableUtil;
import java.util.ArrayList;
import java.util.List;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.util.db.table.CacheType;


public class ImportTable
{
	public final String name;
    
    protected String comment;
    
    protected List<ImportColumn> columns = new ArrayList<ImportColumn>();
    
    protected ImportColumn PK;
    
    private TableConfig tableConfig;
    
    private boolean isView = false;
    
    //用于生成代码，别人外键过来的列
    protected List<ImportColumn> fkColumns = new ArrayList<ImportColumn>();
    
    public ImportTable(String name) {
    	this(name, new TableConfig());
    }
    
    public ImportTable(String name, TableConfig tableConfig) {
    	this.name = name;
    	TableManager.addTable(this);
    	this.tableConfig = tableConfig;
    }
    
	public static ImportTable getTableBySql(String s)
    {
        int index = s.indexOf("`");
        int index2 = s.indexOf("`", index + 1);
        
        return new ImportTable(s.substring(index + 1, index2));
    }
    
	public void setTableConfig(TableConfig tableConfig) {
		this.tableConfig = tableConfig;
	}
	
    public String[][] toArray()
    {
        int size = columns.size() + 1;
        String[][] array = new String[size][];
        
        array[0] = new String[]{upFirstWorld(name), name};
        for (int i = 1; i < size; i++)
        {
            array[i] = columns.get(i - 1).toArray();
        }
        
        return array;
    }
    
    private String upFirstWorld(String s)
    {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    public void addColumn(ImportColumn col)
    {
    	if (columns == null) columns = new ArrayList<ImportColumn>();
    	columns.add(col);
    	col.setTable(this);
    }
    
    public void setPK(ImportColumn col) {
    	this.PK = col;
    	col.setPK(true);
    	
    	if (this.columns == null || !this.columns.contains(col)) {
    		addColumn(col);
    	}
    }
    
    public ImportColumn getPK() {
    	return this.PK;
    }

    public List<ImportColumn> getColumns()
    {
        return columns;
    }
    
    public ImportColumn getColumn(String colName)
    {
    	for (ImportColumn col : columns) {
    		if (col.name.equalsIgnoreCase(colName)) return col;
    	}
        return null;
    }
    
    public ImportColumn getPreColumn(ImportColumn col) {
    	int index = this.columns.indexOf(col);
    	if (index == -1 || index == 0) return null;
    	return columns.get(index - 1);
    }

    public void setColumns(List<ImportColumn> list)
    {
        this.columns = list;
    }

	public String getSeqName() {
		return tableConfig.getSequence();
	}

	public boolean isAutoIncrement() {
	    return tableConfig.isAutoIncrement();
	}

	public String getName() {
		return this.name;
	}
	
	public CacheType getCacheType() {
		return tableConfig.getCacheType();
	}

	public TableConfig getTableConfig() {
		return this.tableConfig;
	}

	public boolean needCutPage() {
		return tableConfig.isNeedCutPage();
	}

	public ImportColumn getDefaultOrderCol() {
		return this.getColumn(tableConfig.getDefaultOrderColName());
	}
	
	public List<ColumnEntry[]> getSelectKeys() {
		List<ColumnEntry[]> list = new ArrayList<ColumnEntry[]>();
		if (this.tableConfig.getSelectKeys() != null) {
			for (String[] array : this.tableConfig.getSelectKeys()) {
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
						col = this.getColumn(s);
					} else {
						String colName = s.substring(0, index);
						col = this.getColumn(colName);
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
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.GAP1 + "public static final class " + getTableName()).append(Constants.ENTER);
        sb.append(Constants.GAP1 + "{").append(Constants.ENTER);
        sb.append(Constants.GAP2 + "public static final Table TABLE = new Table(\"").append(name.toUpperCase()).append("\", ")
                .append("CacheType.").append(getCacheTypeStr()).append(");").append(Constants.ENTER);
        sb.append(Constants.ENTER);
        for (ImportColumn c : columns)
        {
            sb.append(c.toString()).append(Constants.ENTER);
        }
        
        sb.append(Constants.GAP1 + "}").append(Constants.ENTER);
        
        return sb.toString();
    }

    public String getCacheTypeStr()
    {
        if (getCacheType() == CacheType.NOT_CACHE)
        {
            return "NOT_CACHE";
        }
        else if (getCacheType() == CacheType.CACHE_ALL)
        {
            return "CACHE_ALL";
        }
        else
        {
            return "AUTO";
        }
    }
    
    public String getTableName() {
		return name.toUpperCase();
    }

	public boolean isView() {
		return isView;
	}

	public void setView(boolean isView) {
		this.isView = isView;
	}
	
	public String getComment() {
			return comment;
    }

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void addFkColumn(ImportColumn column) {
		this.fkColumns.add(column);
	}
	
	public List<ImportColumn> getFKColumns() {
		return this.fkColumns;
	}

	public int getIndex(ImportColumn col) {
		return this.columns.indexOf(col);
	}
}
