package com.tuhanbao.web.filter;

import java.util.Collection;
import java.util.Map;

import org.apache.ibatis.executor.resultset.SelectorResultParser;

import com.tuhanbao.util.db.table.Column;
import com.tuhanbao.util.db.table.Table;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.db.page.Page;

/**
 * 查询器 暂时只支持单表查询，可以扩展为多表查询
 * 
 * PS 如果不是必须，请尽量不要使用别名，即所有带有asName参数的方法
 * 一般用于区分同一个Table在同一个selector中，才会使用到别名
 * @author tuhanbao
 *
 */
public class SelectorFilter {
	private Selector tablesOfSelector;
	
	private Filter filter;
	
	private boolean is4Count = false;

//	private Selector(Table table) {
//		this.tablesOfSelector = new TablesOfSelector(table);
//	}
//	
//	private Selector(Table table, String asName) {
//		this.tablesOfSelector = new TablesOfSelector(table, asName);
//	}
//	
//	private Selector(Table table, Filter filter) {
//		this(table);
//		this.filter = filter;
//	}

    public SelectorFilter(Selector tablesOfSelector) {
		if (tablesOfSelector == null) throw new MyException("tablesOfSelector is null!");
		this.tablesOfSelector = tablesOfSelector;
	}
	
	public SelectorFilter(Selector tablesOfSelector, Filter filter) {
		this(tablesOfSelector);
		this.filter = filter;
	}
	
	/**
	 * 返回的子table构造的selectTable本身
	 * @param table
	 * @param column
	 * @return
	 */
	
	public SelectTable joinTable(Table table) {
		return this.tablesOfSelector.joinTable(table);
	}
	
	public SelectTable joinTable(Table table, Column column) {
		return this.tablesOfSelector.joinTable(table, column);
	}
	
	public SelectTable joinTable(Table table, String asName, Column column) {
		return this.tablesOfSelector.joinTable(table, asName, column);
	}
	
	public SelectTable joinTable(Table table, String asName, Column column, JoinType joinType) {
		return this.tablesOfSelector.joinTable(table, asName, column, joinType);
	}
	
	@Deprecated
	public SelectTable joinTable(Table table, JoinFilter filter) {
		return this.tablesOfSelector.joinTable(table, filter);
	}
	
	@Deprecated
	public SelectTable joinTable(Table table, String asName, JoinFilter filter) {
		return this.tablesOfSelector.joinTable(table, asName, filter);
	}
	
	@Deprecated
	public SelectTable joinTable(Table table, String asName, JoinFilter filter, JoinType joinType) {
		return this.tablesOfSelector.joinTable(table, asName, filter, joinType);
	}
	
    /**
     * 在连表查询时，请保证每个表的主键又在select column之中 否则可能导致关系混乱
     * 
     * @see SelectorResultParser
     * @param column
     */
	public void addSelectColumn(Column column) {
		this.tablesOfSelector.addSelectColumn(column);
	}
	
	public void addSelectColumn(Table table) {
		this.tablesOfSelector.addSelectColumn(table);
	}
	
	public Collection<Column> getSelectColumns() {
		return this.tablesOfSelector.getSelectColumns();
	}
	
	/**
	 * getter and setter
	 */
	public SelectTable getTable() {
		return this.tablesOfSelector.getTable();
	}

	public void setSelectColumns(SelectColumns selectColumns) {
		this.tablesOfSelector.setSelectColumns(selectColumns);
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	public Filter getFilter() {
		return this.filter;
	}
	
	public Page getPage() {
		if (this.filter == null) return null;
		return this.filter.getPage();
	}

	public Map<String, SelectTable> getAllTables() {
		return this.tablesOfSelector.getAllTables();
	}
    
    public boolean isIs4Count() {
        return is4Count;
    }

    public void setIs4Count(boolean is4Count) {
        this.is4Count = is4Count;
    }
}
