package com.tuhanbao.web.filter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.util.db.table.Column;
import com.tuhanbao.util.db.table.Table;
import com.tuhanbao.web.db.page.Page;
import com.tuhanbao.web.filter.operator.Operator;

/**
 * 条件查询
 * 
 * <p>
 * 应用于mybatis的map.xml时只支持条件语句的顺序写法，
 * 如id=1，但是不支持1=id
 * </P>
 * 
 * <p>
 * mybatis示例写法，其中变量名item不允许改变
 * <sql id="Filter_Where_Clause" >
    <where>
      <foreach collection="items" item="item" separator="" >
        ${item.getSql()}
        <if test="item.isList()">
       		<foreach collection="item.listValue()" item="listItem" open="(" close=")" separator="," >
				#{listItem}
           	</foreach>
        </if>
      </foreach>
    </where>
  </sql>
  </p>
 * 
 * @author Administrator
 *
 */
public class Filter
{
    private List<ISqlItem> items;

    private List<OrderColumn> orderColumns;

    private static final String ORDERBY = "order by";
    
    private Page page;
    
    private boolean distinct = false;
    
    private Table table;

	public Filter()
    {
    }
	
	public Filter(Table table)
    {
		this.table = table;
    }
    
    /**
     * arg2 为list，代表in,
     * arg2 位二维数组，代表between
     * 其余默认为等于
     * 
     * @param arg1
     * @param arg2
     */
    public Filter andFilter(Object ... args)
    {
    	return this.andFilter(getDefaultOperator(args), args);
    }
    
    public Filter orFilter(Object ... args)
    {
        return this.orFilter(getDefaultOperator(args), args);
    }

	private Operator getDefaultOperator(Object ... args) {
		if (args.length == 2) {
		    if (args[1] == null) return Operator.IS_NULL;
			if (args[1] instanceof Collection) {
			    if (((Collection<?>)args[1]).isEmpty()) {
			        return Operator.IS_NULL;
			    }
			    else return Operator.IN;
			}
			if (args[1].getClass().isArray()) {
				List<Object> list = new ArrayList<Object>();
				int len = Array.getLength(args[1]);
				if (len == 0) return Operator.IS_NULL;
	            for (int i = 0; i < len; ++i) {
	                Object item = Array.get(args[1], i);
	                list.add(item);
	            }
				args[1] = list;
				return Operator.IN;
			}
		}
    	else if (args.length == 3) return Operator.BETWEEN;
    	else if (args.length == 1) return Operator.IS_NULL;
    	else if (args.length > 3) {
    		List<Object> list = new ArrayList<Object>();
			for (int i = 1; i < args.length; i++) {
				list.add(args[i]);
			}
			args[1] = list;
			return Operator.IN;
    	}
		return Operator.EQUAL;
	}
    
    public Filter andFilter(Operator operator, Object ... args)
    {
    	addLogicType(LogicType.AND);
		return addItem(new FilterItem(operator, args));
    }
    
    public Filter orFilter(Operator operator, Object ... args)
    {
    	addLogicType(LogicType.OR);
		return addItem(new FilterItem(operator, args));
    }
    
    public Filter addOrderColumn(Column col) {
    	return this.addOrderColumn(col, false);
    }
    
    public Filter addOrderColumn(Column col, boolean isDesc) {
    	if (orderColumns == null) orderColumns = new ArrayList<OrderColumn>();
    	orderColumns.add(new OrderColumn(col, isDesc));
    	
    	return this;
    }
    
    public String getSelectSql()
    {
        StringBuilder sb = new StringBuilder();
        if (items != null && getSize() >= 1) {
	        sb.append(items.get(0).toString());
	        for (int i = 1, size = getSize(); i < size; i++)
	        {
	            sb.append(Constants.BLANK).append(items.get(i));
	        }
        }
        return sb.toString();
    }
    
    public String getOrderSql()
    {
        StringBuilder sb = new StringBuilder();
        if (orderColumns != null && orderColumns.size() >= 1) {
        	sb.append(Constants.BLANK).append(ORDERBY);
        	sb.append(Constants.BLANK).append(orderColumns.get(0).toString());
        }
        return sb.toString();
    }
    
    public Filter addLeftParenthese()
    {
    	return addItem(ParentheseItem.LEFT_PARENTHESE);
    }
    
    public Filter andLeftParenthese()
    {
        addLogicType(LogicType.AND);
        return addLeftParenthese();
    }
    
    public Filter orLeftParenthese()
    {
        addLogicType(LogicType.OR);
        return addLeftParenthese();
    }
    
    public Filter addRightParenthese()
    {
    	return addItem(ParentheseItem.RIGHT_PARENTHESE);
    }

	private void addLogicType(LogicType logicType) {
		int size = getSize();
		if (size >= 1 && items.get(size - 1) != ParentheseItem.LEFT_PARENTHESE) addItem(logicType);
	}

	private int getSize() {
		if (items == null) return 0;
		return items.size();
	}
    
    protected Filter addItem(ISqlItem item) {
    	if (item == null) return this;
    	if (items == null) items = new ArrayList<ISqlItem>();
    	items.add(item);
    	return this;
    }
    
    public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	public List<ISqlItem> getItems() {
		return items;
	}
	
	public Table getTable() {
		return this.table;
	}

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
