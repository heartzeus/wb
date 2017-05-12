package com.tuhanbao.web.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tuhanbao.io.impl.tableUtil.DataType;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.db.table.Column;
import com.tuhanbao.util.db.table.DataValueFactory;
import com.tuhanbao.util.exception.BaseErrorCode;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.filter.operator.Operator;

/**
 * 过滤表达式 常见的过滤表达式都是二元比较，但是也有类似与between，in等三元的比较符
 * 
 * @author tuhanbao
 *
 */
public class FilterItem extends SqlItem {
    private Object arg1, arg2, arg3;

    private Operator operator;

    /**
     * args必须为DataValue或Column
     * 
     * @param operator
     * @param args
     */
    protected FilterItem(Operator operator, Object... args) {
        super(null);
        this.operator = operator;
        if (args == null || args.length == 0 || args[0] == null) throw new MyException(BaseErrorCode.ILLEGAL_ARGUMENT, "FilterItem args is null");
        this.arg1 = args[0];

        this.arg2 = args.length > 1 ? args[1] : null;
        if (operator == Operator.BETWEEN) {
            if (args.length < 3 || args[2] == null) throw new MyException(BaseErrorCode.ILLEGAL_ARGUMENT, "FilterItem args is null");
            this.arg3 = args[2];
        }

        changeValue();
    }

    /**
     * 将非dataValue的转换为DataValue
     * 
     * @return
     */
    private void changeValue() {
        // 如果 arg1是column，只能修改arg2和arg3的值
        if (arg1 instanceof Column) {
            DataType dataType = ((Column)arg1).getDataType();
            // 暂时只有这两个需要处理
            if (dataType == DataType.BOOLEAN || dataType == DataType.DATE) {
                if (!(arg2 instanceof Column)) {
                    arg2 = toDataValue(dataType, arg2);
                }
                if (!(arg3 instanceof Column)) {
                    arg3 = toDataValue(dataType, arg3);
                }
            }
        }

    }

    private static Object toDataValue(DataType dataType, Object value) {
        if (value == null) return null;
        if (value instanceof Collection<?>) {
            Collection<?> col = (Collection<?>)value;
            List<Object> list = new ArrayList<Object>();
            for (Object item : col) {
                list.add(DataValueFactory.toDataValue(dataType, item));
            }
            return list;
        }

        return DataValueFactory.toDataValue(dataType, value);
    }

    public Object getArg1() {
        return arg1;
    }

    public Object getArg2() {
        return arg2;
    }

    public Object getArg3() {
        return arg3;
    }

    public Object getArg1ForMybatis() {
        if (arg1 instanceof IFilterColumn) return ((IFilterColumn)arg1).getSqlName();
        else if (arg1 instanceof Column) return arg1;
        else return "#{item.arg1}";
    }

    public Object getArg2ForMybatis() {
        if (arg2 instanceof IFilterColumn) return ((IFilterColumn)arg2).getSqlName();
        else if (arg2 instanceof Column) return arg2;
        else return "#{item.arg2}";
    }

    public Object getArg3ForMybatis() {
        if (arg3 instanceof IFilterColumn) return ((IFilterColumn)arg3).getSqlName();
        else if (arg3 instanceof Column) return arg3;
        else return "#{item.arg3}";
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String getSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(getArg1ForMybatis()).append(operator);
        if (Operator.BETWEEN == operator) {
            sb.append(getArg2ForMybatis()).append(" and ").append(getArg3ForMybatis());
        }
        else if (Operator.IN == operator || Operator.NOT_IN == operator) {
        }
        else if (Operator.IS_NULL != operator && Operator.NOT_NULL != operator) {
            sb.append(getArg2ForMybatis());
        }
        return sb.toString();
    }

    @Override
    public boolean isList() {
        return Operator.IN == operator || Operator.NOT_IN == operator;
    }

    /**
     * isList = true时请重写
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<Object> listValue() {
        return (Collection<Object>)arg2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(arg1.toString());
        sb.append(operator);
        if (Operator.BETWEEN == operator) {
            sb.append(arg2).append(" and ").append(arg3);
        }
        else if (Operator.IN == operator || Operator.NOT_IN == operator) {
            sb.append(StringUtil.array2String((Collection<?>)arg2));
        }
        else if (Operator.IS_NULL != operator && Operator.NOT_NULL != operator) {
            sb.append(arg2);
        }
        return sb.toString();
    }
}
