package com.tuhanbao.web.filter;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.util.db.table.Column;
import com.tuhanbao.util.db.table.Table;
import com.tuhanbao.util.exception.MyException;

/**
 * JoinFilter与filter在功能上完全等价
 * 只是在业务用途上有一些区别，joinFilter用于数据库连表时的on过滤，
 * 因为业务的特殊需求，JoinFilter是为了规范程序员的写法，以免传入一些错误的filter进来，比如id = 3，这中filter明显是程序员传参失误导致
 * 
 * joinFilter为什么只允许
 * 
 * @see Filter
 * @author Administrator
 *
 */
public class JoinFilter extends Filter
{

	public JoinFilter()
    {
		super();
    }
	
	public JoinFilter(Table table)
    {
		super(table);
    }
    
    public String getOrderSql()
    {
    	//重写
        return Constants.EMPTY;
    }
    
    protected Filter addItem(ISqlItem item) {
    	//只是校验, join table的条件必须是两列相等
    	if (item instanceof FilterItem) {
    		FilterItem filterItem = ((FilterItem)item);
    		Object arg1 = filterItem.getArg1();
    		Object arg2 = filterItem.getArg2();
    		Object arg3 = filterItem.getArg3();
    		
    		//要求，arg1和arg2为column对象，arg3为null
			if ((arg1 instanceof Column) && (arg2 instanceof Column) && arg3 == null) {
				return super.addItem(item);
    		}
    	}
    	
    	throw new MyException("join table filter is wrong : " + item.toString());
    }
}
