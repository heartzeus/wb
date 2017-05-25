package com.hhnz.api.cfcrm.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.tuhanbao.io.objutil.TimeUtil;

public class TimeTool {
    
    public static int[] getCurentYMD() {
        long time = getCurentYMDTime();
        return TimeUtil.getYearMonthDayHour(time);
    }

    public static long getCurentYMDTime() {
        return TimeUtil.now() - ConstantsConfig.SYSTEM_DELAY * TimeUtil.DAY2MILLS;
    }
    /**
     * 获取某一天起始时间和终止时间方法，参数为标识符
     * 用以区分是哪天，目前仅支持当日和上个月最后一天
     * @author gzh
     * @param IDentifier
     * @return
     * @throws ParseException
     */
    public static Date[] getOneDayStartAndEndTime(String IDentifier) throws ParseException{
        Calendar calendar = Calendar.getInstance();
        // 表示为上个月的最后一天
        if ("LastMonth".equals(IDentifier)) {
            calendar.set(Calendar.DAY_OF_MONTH, 1); 
            calendar.add(Calendar.DATE, -1);
        }       
        Date[] dates = new Date[2];
        Date start = calendar.getTime();
        Date end = calendar.getTime();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat formater2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");         
        start = formater2.parse(formater.format(start)+ " 00:00:00");
        end = formater2.parse(formater.format(end)+ " 23:59:59");
        dates[0] = start;
        dates[1] = end;
        System.out.println(dates[0]);
        System.out.println(dates[1]);
        return dates;
    }


}
