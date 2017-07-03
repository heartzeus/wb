package com.hhnz.api.cfcrm.tool;

import com.hhnz.api.cfcrm.constants.enums.OfficeType;
import com.hhnz.api.cfcrm.constants.enums.Sex;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.base.util.objutil.StringUtil;

public class ExportDataConverter {
    /**
     * 获取生日日期月和日字段
     * 
     * @author gzh
     * @param birthday
     * @return
     */
    public static String[] getMonthAndDay(String birthday) {
        // To judge whether the format is right or not.检查格式
        String[] month = birthday.split("月");
        String[] day = month[1].split("号");
        String[] getMonthAndDay = {month[0], day[0]};
        return getMonthAndDay;
    }

    /**
     * 性别类型转换器
     * 
     * @author gzh
     * @param sex
     * @return
     */
    public static int sexTypeConverter(String sex) {
        if (StringUtil.isEmpty(sex)) {
            // Need to abstract to ErrorCode
            throw new MyException("导入数据性别类型不能为空！");
        }
        else {
            if ("女".equals(sex)) {
                return Sex.FAMALE.value;
            }
            else {
                return Sex.MALE.value;
            }

        }
    }

    /**
     * 转为int型，乘以了100
     * 
     * @author gzh
     * @param num
     * @return
     */
    public static int intTypeConverter(String num) {
        // 空字符串强double转类型会报错，要先进行处理
        double result = Double.parseDouble(StringUtil.isEmpty(num) ? "0.00" : num);
        double numD = result * 100;
        int numI = (int)numD;
        return numI;
    }

    /**
     * 转为Long型，乘以了100
     * 
     * @author gzh
     * @param num
     * @return
     */
    public static Long longTypeConverter(String num) {
        // 空字符串强转double类型会报错，要先进行处理
        double result = Double.parseDouble(StringUtil.isEmpty(num) ? "0.00" : num);
        double numD = result * 100;
        Long numL = Math.round(numD);
        return numL;
    }

    /**
     * 投资期限转换器
     * 
     * @param investTerm
     * @return
     */
    public static int investTermConverter(String investTerm) {
        // 数据第n行出错记录
        if (investTerm.endsWith("月")) {
            String[] month = investTerm.split("月");
            int num = Integer.parseInt(month[0]);
            return num * 30;
        }
        else if (investTerm.endsWith("天")) {
            String[] day = investTerm.split("天");
            int num = Integer.parseInt(day[0]);
            return num;
        }
        else {
            throw new MyException("投资期限数据有误！");
        }
    }

    /**
     * 投资期限转换器
     * 
     * @param investTerm
     * @return
     */
    public static OfficeType officeTypeConverter(String fzOfficeType) {
        if ("A".equals(fzOfficeType)) {
            return OfficeType.A;
        }
        else if ("B".equals(fzOfficeType)) {
            return OfficeType.B;
        }
        else {
            throw new MyException("分账数据错误");
        }

    }

    // /**
    // * 标类别和标状态转换器
    // *
    // * @param standerType
    // * @return
    // */
    // public static int standerTypeAndStatusConverter(String str) {
    // // 这个需要张舰进行确认。需要将异常抽成常量，暂时定普通贷的类型为1,满标的值为0.
    // if (str.equals("普通贷")) {
    // return 1;
    // } else if (str.equals("满标")) {
    // return 0;
    // } else {
    // throw new MyException("标数据错误！");
    // }
    // }

    // /**
    // * 回款类型转换器
    // *
    // * @param str
    // * @return
    // */
    // public static Long repaymentTypeConverter(String str) {
    // // 抽常量，暂定类型为1
    // if (str == "按月付息一次还本") {
    // return 1l;
    // } else {
    // return 0l;
    // }
    // }
    //
    // /**
    // * 注册推荐人分组转换器
    // *
    // * @return
    // */
    // public static int disPerInviGroupConverter(String str) {
    // // 抽出常量 暂定分站客户类型为0，外部渠道类型为1，其他暂定为2
    // if (str == "财益通_资金渠道_分站客户（哈哈农庄）") {
    // return 0;
    // } else if (str == "财益通_资金渠道_外部渠道（哈哈农庄）") {
    // return 1;
    // } else {
    // return 2;
    // }
    // }

}
