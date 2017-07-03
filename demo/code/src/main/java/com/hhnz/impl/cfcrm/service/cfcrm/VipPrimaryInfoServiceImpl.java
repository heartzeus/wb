package com.hhnz.impl.cfcrm.service.cfcrm;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.SelectorConstants;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.CustomType;
import com.hhnz.api.cfcrm.constants.enums.Sex;
import com.hhnz.api.cfcrm.constants.enums.VipType;
import com.hhnz.api.cfcrm.constants.enums.VipTypeDiy;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestFlow;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IAreaService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestFlowService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestInfoService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.api.cfcrm.service.fz.IOfficeService;
import com.hhnz.api.cfcrm.tool.ExportDataConverter;
import com.hhnz.api.cfcrm.tool.TimeTool;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.JoinType;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.base.util.io.excel.Excel2007Util;
import com.tuhanbao.base.util.objutil.ArrayUtil;
import com.tuhanbao.base.util.objutil.TimeUtil;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("vipPrimaryInfoService")
@Transactional("cfcrmTransactionManager")
public class VipPrimaryInfoServiceImpl extends ServiceImpl<VipPrimaryInfo> implements IVipPrimaryInfoService {

    private static MyBatisSelector CUSTOM_REMINDER_SELECTOR = new MyBatisSelector(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    private static MyBatisSelector CUSTOM_MSG_SELECTOR = new MyBatisSelector(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    private static MyBatisSelector CUSTOM_AWARD_SELECTOR = new MyBatisSelector(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    private static MyBatisSelector CUSTOM_REMINDER_SELECTOR_RIGHT = new MyBatisSelector(TableConstants.T_PAYMENT_COLLECTION.TABLE);
    private static final String EMPTY = "";

    @Autowired
    private IVipInvestInfoService vipInvestInfoService;

    @Autowired
    private IVipInvestFlowService vipInvestFlowService;

    @Autowired
    private IOfficeService iOfficeService;

    @Autowired
    private IAreaService iAreaService;

    static {
        CUSTOM_REMINDER_SELECTOR.joinTable(TableConstants.T_PAYMENT_COLLECTION.TABLE, JoinType.RIGHT_JOIN);
        CUSTOM_MSG_SELECTOR.joinTable(TableConstants.T_VIP_INVEST_INFO.TABLE);
        CUSTOM_AWARD_SELECTOR.joinTable(TableConstants.T_AWARD_VIP.TABLE);
        CUSTOM_REMINDER_SELECTOR_RIGHT.joinTable(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    }

    /**
     * 3.14号代码评审 使用count sql语句代替了原来的取出size的大小 优化了性能
     * 
     * @author gzh
     * @return
     */
    @Override
    public int getVipAmountOnBirthday() {
        List<Map<String, Object>> mapList = this.excuteSql(
                "SELECT count(*) as count FROM `t_vip_primary_info` WHERE DAY = EXTRACT(DAY FROM NOW()) AND MONTH = EXTRACT(MONTH FROM NOW());");
        int count = ((Long)mapList.get(0).get("count")).intValue();
        return count;
    }

    @Override
    public Map<String, Object> getCtPrMapByDsId(String dsId) {
        String sql = "SELECT a.PARENT_ID as CITYID,b.PARENT_ID as PROVENCEID FROM t_area A LEFT JOIN t_area b ON A .PARENT_ID = b.AREA_ID WHERE A.AREA_ID = "
                + dsId;
        List<Map<String, Object>> mapList = this.excuteSql(sql);
        return mapList.get(0);
    }

    @Override
    public List<VipPrimaryInfo> selectCustomReminder(Filter filter) {
        // List<VipPrimaryInfo> customReminder =
        // this.select(CUSTOM_REMINDER_SELECTOR, filter);
        List<VipPrimaryInfo> customReminder = this.select(CUSTOM_REMINDER_SELECTOR_RIGHT, filter);
        return customReminder;
    }

    @Override
    public List<VipPrimaryInfo> selectByAutoFilter(Filter filter) {
        if (filter == null) {
            filter = new Filter();
        }
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        return super.select(SelectorConstants.VIP_INVEST_SELECTOR, filter);
    }

    @Override
    public void createNewCustom(VipPrimaryInfo vipPrimaryInfo) {
        // 检查 vipPrimaryInfo是否存在，存在报错

        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, vipPrimaryInfo.getId());
        List<VipPrimaryInfo> list = this.select(filter);
        if (!(list == null || list.isEmpty())) {
            throw new MyException(ErrorCode.ID_IS_USED);
        }
        // 查出我自己的月排名 根据月投资额，查有没有跟自己相同的人，有的话排名等于他，否则查所有大于我投资额的总人数，排名为N+1
        // 更新所有小于我投资额的人排名 rank = rank + 1
        VipInvestInfo currentMonthInvestInfo = vipPrimaryInfo.getCurrentMonthInvestInfo();
        int year = currentMonthInvestInfo.getYear();
        int month = currentMonthInvestInfo.getMonth();
        Filter investMonthFilter = new Filter();
        long yearMonthInvest = currentMonthInvestInfo.getYearMonthInvestAmount();
        investMonthFilter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR_MONTH_INVEST_AMOUNT, yearMonthInvest);
        investMonthFilter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, year);
        investMonthFilter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, month);
        List<VipInvestInfo> investList = vipInvestInfoService.select(investMonthFilter);
        if (investList != null && !investList.isEmpty()) {
            currentMonthInvestInfo.setRank(list.get(0).getCurrentMonthInvestInfo().getRank());
        }
        else {
            List<Map<String, Object>> mapList = this.excuteSql("select count(*) as count from  t_vip_invest_info where YEAR_MONTH_INVEST_AMOUNT > "
                    + yearMonthInvest + " and month =" + month + " and year =" + year);
            Long count = (Long)mapList.get(0).get("count");
            currentMonthInvestInfo.setRank(count.intValue() + 1);
        }
        this.excuteSql("update t_vip_invest_info set RANK = RANK + 1 where YEAR_MONTH_INVEST_AMOUNT < " + yearMonthInvest + " and month =" + month
                + " and year =" + year);

        // 年排名与上两步类似
        Filter investYearFilter = new Filter();
        long yearMonthInvestNew = vipPrimaryInfo.getCurrentYearInvestInfo().getYearMonthInvestAmount();
        investYearFilter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR_MONTH_INVEST_AMOUNT, yearMonthInvestNew);
        investYearFilter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, year);
        investYearFilter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ConstantsConfig.MONTH);
        List<VipInvestInfo> investListNew = vipInvestInfoService.select(investYearFilter);
        VipInvestInfo currentYearInvestInfo = vipPrimaryInfo.getCurrentYearInvestInfo();
        if (investListNew != null && !investListNew.isEmpty()) {
            currentYearInvestInfo.setRank(list.get(0).getCurrentMonthInvestInfo().getRank());
        }
        else {
            List<Map<String, Object>> mapList = this.excuteSql("select count(*) as count from T_VIP_INVEST_INFO where YEAR_MONTH_INVEST_AMOUNT > "
                    + yearMonthInvestNew + " and month =" + month + " and year =" + year);
            Long count = (Long)mapList.get(0).get("count");
            if (count == 0) {
                currentYearInvestInfo.setRank(count.intValue() + 1);
            }
        }
        this.excuteSql("update T_VIP_INVEST_INFO set RANK = RANK + 1 where YEAR_MONTH_INVEST_AMOUNT < " + yearMonthInvestNew + " and month =" + month
                + " and year =" + year);

        // 把我的月投资信息，年投资信息，个人信息add进数据库
        addNewCustom(vipPrimaryInfo);
    }

    @Override
    public void deleteCustom(Long id) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, id);
        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.YEAR, true).addOrderField(TableConstants.T_VIP_INVEST_INFO.MONTH, true);
        List<VipInvestInfo> investList = vipInvestInfoService.select(filter);
        VipInvestInfo vipInvestInfo = new VipInvestInfo();
        if (investList != null && !investList.isEmpty()) {
            vipInvestInfo = investList.get(0);
        }
        Long currentMonthInvestAmount = vipInvestInfo.getYearMonthInvestAmount();
        this.excuteSql("update T_VIP_PRIMARY_INFO set RANK = RANK - 1 where YEAR_MONTH_INVEST_AMOUNT < " + currentMonthInvestAmount + "and month ="
                + vipInvestInfo.getMonth() + "and year =" + vipInvestInfo.getYear());

        Filter deleteInfoFilter = new Filter();
        deleteInfoFilter.andFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, id);
        Filter deleteFlowFilter = new Filter();
        deleteFlowFilter.andFilter(TableConstants.T_VIP_INVEST_FLOW.CUSTOM_ID, id);
        this.deleteById(id);
        vipInvestInfoService.delete(deleteInfoFilter);
        vipInvestFlowService.delete(deleteFlowFilter);
    }

    private void addNewInvestInfo(VipInvestInfo vipInvestInfo) {
        // 检查数据库有没有同年同月的数据，有的话更新，无则新增
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, vipInvestInfo.getCustomId());
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, vipInvestInfo.getMonth());
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, vipInvestInfo.getYear());
        List<VipInvestInfo> info = vipInvestInfoService.selectInvestInfo(filter);
        if (info != null && !info.isEmpty()) {
            vipInvestInfoService.updateSelective(vipInvestInfo, filter);
        }
        else {
            vipInvestInfoService.add(vipInvestInfo);
        }
    }

    public void updateInvestInfo(List<VipInvestFlow> vipInvestFlows) {
        // 查询当前人所有的历史流水，按时间倒序排序
        for (VipInvestFlow vipInvestFlow : vipInvestFlows) {
            Long customId = vipInvestFlow.getCustomId();
            Filter filter = new Filter();
            filter.andFilter(TableConstants.T_VIP_INVEST_FLOW.CUSTOM_ID, customId);
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_INVEST_FLOW.INVEST_DATE,
                    TimeUtil.now() - TimeUtil.DAY2MILLS * ConstantsConfig.INVEST_DAY);
            filter.addOrderField(TableConstants.T_VIP_INVEST_FLOW.INVEST_DATE, true);
            List<VipInvestFlow> list = vipInvestFlowService.select(filter);
            // 分析其半年内的投资情况，得出其投资类型;
            VipType vipType = null;
            // 未投资天数
            int noInvestDays = 0;
            if (list != null && !list.isEmpty()) {
                noInvestDays = getNoInvestDays(list.get(0));
                list.add(vipInvestFlow);
                vipType = getVipType(list);
            }
            // 存库
            VipInvestInfo vipInvestInfo = new VipInvestInfo();
            vipInvestInfo.setOffDay(noInvestDays);
            if (vipType != null) {
                vipInvestInfo.setVipType(vipType);
            }
            Filter vifFilter = new Filter().andFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, customId);
            vipInvestInfoService.updateSelective(vipInvestInfo, vifFilter);
        }
    }

    private int getNoInvestDays(VipInvestFlow vipInvestFlow) {
        // 取第一条数据与当前时间比较
        long time = TimeUtil.now();
        long investTime = vipInvestFlow.getInvestDate().getTime();
        long delay = time - investTime;
        int day = (int)(delay / TimeUtil.DAY2MILLS);
        return day;
    }

    private VipType getVipType(List<VipInvestFlow> vipInvestFlows) {
        long total = 0;
        Map<VipType, Long> vipTypeInvestTotal = new HashMap<VipType, Long>();
        for (VipInvestFlow vipInvestFlow : vipInvestFlows) {
            int investTerm = vipInvestFlow.getInvestTerm();
            long investAmount = vipInvestFlow.getInvestAmount();
            total += investAmount;

            // 算出不同类型投资总额
            for (VipTypeDiy vType : VipTypeDiy.values()) {
                if (vType.isConstainTerm(investTerm)) {
                    addInvestAmount(vipTypeInvestTotal, investAmount, vType.value);
                }
            }
        }

        VipType maxInvestVT = null;
        long maxInvestAmount = 0;
        for (Entry<VipType, Long> entry : vipTypeInvestTotal.entrySet()) {
            if (entry.getValue() > maxInvestAmount) {
                maxInvestAmount = entry.getValue();
                maxInvestVT = entry.getKey();
            }
        }

        // 取最后一条，除以总投资额，是否超过60%
        if (maxInvestAmount * 100d / total > ConstantsConfig.INVEST_RATE) {
            return maxInvestVT;
        }
        else {
            return null;
        }
    }

    private void addInvestAmount(Map<VipType, Long> vipTypeInvestTotal, long investAmount, VipType vType) {
        long currentAmount = 0;
        if (vipTypeInvestTotal.containsKey(vType)) {
            currentAmount = vipTypeInvestTotal.get(vType);
        }

        vipTypeInvestTotal.put(vType, currentAmount + investAmount);
    }

    private void addNewCustom(VipPrimaryInfo vipPrimaryInfo) {
        // 需要判断网信ID是否已经存在
        VipPrimaryInfo vipPrimaryInfoJudge = this.selectById(vipPrimaryInfo.getId());
        if (vipPrimaryInfoJudge != null) {
            if (vipPrimaryInfoJudge.isDel()) {
                this.update(vipPrimaryInfo);
            }
            else {
                throw new MyException(ErrorCode.ID_IS_USED);
            }
        }
        else {
            this.add(vipPrimaryInfo);
        }

        addNewInvestInfo(vipPrimaryInfo.getCurrentMonthInvestInfo());
        addNewInvestInfo(vipPrimaryInfo.getCurrentYearInvestInfo());
    }

    @Override
    public void importCustomData(List<VipPrimaryInfo> customs) {
        // 将所有人的isDel置为true
        VipPrimaryInfo vipPrimaryInfo = new VipPrimaryInfo();
        vipPrimaryInfo.setIsDel(true);
        vipPrimaryInfo.setIsReset(true);
        this.updateSelective(vipPrimaryInfo, null);

        // 更新当月排名，年排名和投资降幅
        updateRankAndInvestDrop(customs);
        updateAccRankAndInvestDrop(customs);
        // 新增所有人 循环调用addNewCustom方法，需要将isDel置为false
        for (VipPrimaryInfo custom : customs) {
            custom.setIsDel(false);
            custom.setIsReset(false);
            addNewCustom(custom);
        }
    }

    private void updateRankAndInvestDrop(List<VipPrimaryInfo> list) {
        // TODO 导入的年化额是历史所有累计的年化额还是当年累计，找产品确认。

        // 遍历所有人和投资信息，根据当前月份取出上一个月的投资信息（取不到默认为0）
        // 用当前投资信息减去上一个月的投资信息，缓存本月投资信息
        Collections.sort(list, new Comparator<VipPrimaryInfo>() {
            @Override
            public int compare(VipPrimaryInfo o1, VipPrimaryInfo o2) {
                return (int)(o2.getCurrentMonthInvestInfo().getYearMonthInvestAmount() - o1.getCurrentMonthInvestInfo().getYearMonthInvestAmount());
            }
        });

        int index = 1;
        int rank = 1;
        long lastYearMonthInvestAmount = -1;
        for (VipPrimaryInfo primaryInfo : list) {
            long currentYearMonthInvestAmount = primaryInfo.getCurrentMonthInvestInfo().getYearMonthInvestAmount();
            if (currentYearMonthInvestAmount != lastYearMonthInvestAmount) {
                rank = index;
            }
            primaryInfo.getCurrentMonthInvestInfo().setRank(rank);
            index++;
            lastYearMonthInvestAmount = currentYearMonthInvestAmount;
        }

        Collections.sort(list, new Comparator<VipPrimaryInfo>() {
            @Override
            public int compare(VipPrimaryInfo o1, VipPrimaryInfo o2) {
                return (int)(o2.getCurrentYearInvestInfo().getYearMonthInvestAmount() - o1.getCurrentYearInvestInfo().getYearMonthInvestAmount());
            }
            
        });

        index = 1;
        rank = 1;
        lastYearMonthInvestAmount = -1;
        for (VipPrimaryInfo primaryInfo : list) {
            long currentYearMonthInvestAmount = primaryInfo.getCurrentYearInvestInfo().getYearMonthInvestAmount();
            if (currentYearMonthInvestAmount != lastYearMonthInvestAmount) {
                rank = index;
            }
            primaryInfo.getCurrentYearInvestInfo().setRank(rank);
            index++;
            lastYearMonthInvestAmount = currentYearMonthInvestAmount;
        }
    }
    /**
     * 根据累计年化
     * @param list
     */
    private void updateAccRankAndInvestDrop(List<VipPrimaryInfo> list) {
        Collections.sort(list, new Comparator<VipPrimaryInfo>() {
            @Override
            public int compare(VipPrimaryInfo o1, VipPrimaryInfo o2) {
                return (int)(o2.getCurrentMonthInvestInfo().getAccInvAmount() - o1.getCurrentMonthInvestInfo().getAccInvAmount());
            }
        });
        
        int index = 1;
        int accRank = 1;
        long lastAccInvAmount = -1;
        for (VipPrimaryInfo primaryInfo : list) {
            long AccInvAmount = primaryInfo.getCurrentMonthInvestInfo().getAccInvAmount();
            if (AccInvAmount != lastAccInvAmount) {
                accRank = index;
            }
            primaryInfo.getCurrentMonthInvestInfo().setRankAcc(accRank);
            index++;
            lastAccInvAmount = AccInvAmount;
        }
        
        Collections.sort(list, new Comparator<VipPrimaryInfo>() {
            @Override
            public int compare(VipPrimaryInfo o1, VipPrimaryInfo o2) {
                return (int)(o2.getCurrentYearInvestInfo().getAccInvAmount() - o1.getCurrentYearInvestInfo().getAccInvAmount());
            }
            
        });
        
        index = 1;
        accRank = 1;
        lastAccInvAmount = -1;
        for (VipPrimaryInfo primaryInfo : list) {
            long currentYearMonthInvestAmount = primaryInfo.getCurrentYearInvestInfo().getAccInvAmount();
            if (currentYearMonthInvestAmount != lastAccInvAmount) {
                accRank = index;
            }
            primaryInfo.getCurrentYearInvestInfo().setRankAcc(accRank);
            index++;
            lastAccInvAmount = currentYearMonthInvestAmount;
        }
        
        
        
    }

    @Override
    public List<VipPrimaryInfo> selectInvestInfo(Filter filter) {
        List<VipPrimaryInfo> list = this.select(CUSTOM_MSG_SELECTOR, filter);
        return list;
    }

    @Override
    public List<VipPrimaryInfo> selectAwardInfo(Filter filter) {
        List<VipPrimaryInfo> list = this.select(CUSTOM_AWARD_SELECTOR, filter);
        return list;
    }

    public VipInvestInfo getVipInvestInfo(int year, int month, long customId) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, month);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, year);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, customId);
        List<VipInvestInfo> infos = vipInvestInfoService.select(filter);
        if (infos != null && !infos.isEmpty()) {
            return infos.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * 客户信息导入
     * 
     * @param is
     */
    @Override
    public void importCustomInfo(InputStream is) {
        String[][] arrays = Excel2007Util.read(is, 0);

        // 1.将所有primaryInfo置为false

        // 2.遍历所有数据

        List<VipPrimaryInfo> vipPrimaryInfos = new ArrayList<>();

        int[] ymd = TimeTool.getCurentYMD();
        Date date = new Date();

        for (int i = 1; i < arrays.length; i++) {
            String[] array = arrays[i];
            if (ArrayUtil.isEmptyLine(array)) continue;
            int index = 0;
            String deadLine = array[index++]; // 截止日期
            String customId = array[index++]; // 客户ID
            String sex = array[index++]; // 性别
            String birthday = array[index++]; // 生日日期
            String dayInvestBalance = array[index++]; // 当日在投余额
            String dayAccountBalance = array[index++]; // 当日账户余额
            String dayAddInvestAmount = array[index++]; // 当日新增投资额
            String dayAddInvestTimes = array[index++]; // 当日新增投资笔数
            String totalInvestTimes = array[index++]; // 累计投资笔数
            String accInvAmount = array[index++]; // 累计投资额
            String accInvYearAmount = array[index++]; // 累计年化投资额
            String accInviInvestAmount = array[index++]; // 累计邀请投资额
            String accInviInvestPersons = array[index++]; // 累计邀请投资人数
            String accInviInvestAmountYear = array[index++]; // 累计邀请年化投资额
            String selfCountCode = array[index++]; // 自身优惠码
            String customName = array[index++]; // 姓名
            String phone = array[index++]; // 手机
            // 根据客户ID，查找客户所在省市区地址
            VipPrimaryInfo vipPrimaryInfo = new VipPrimaryInfo();
            vipPrimaryInfo.setId(Long.parseLong(customId));
            vipPrimaryInfo.setSex(Sex.getSex(ExportDataConverter.sexTypeConverter(sex)));
            vipPrimaryInfo.setCustomType(CustomType.IS_FH);
            vipPrimaryInfo.setName(customName);
            // InvitateNum is the selfCountCode.
            vipPrimaryInfo.setInvitateNum(selfCountCode);
            vipPrimaryInfo.setPhone(phone);
//            vipPrimaryInfo.setIsDel(false);
//            vipPrimaryInfo.setIsReset(false);
            vipPrimaryInfo.setMonth(Integer.parseInt(ExportDataConverter.getMonthAndDay(birthday)[0]));
            vipPrimaryInfo.setDay(Integer.parseInt(ExportDataConverter.getMonthAndDay(birthday)[1]));
            VipPrimaryInfo vipPrimaryInfoCheck = this.selectById(Long.parseLong(customId));
            if (vipPrimaryInfoCheck == null || vipPrimaryInfoCheck.getVipStart() == null || vipPrimaryInfoCheck.isReset() == true) {
                vipPrimaryInfo.setVipStart(date);
            } else {                
                vipPrimaryInfo.setVipStart(vipPrimaryInfoCheck.getVipStart());
            }
            //在分账库查找省市区等信息
            iOfficeService.getTypeAndAreaById(vipPrimaryInfo);
            vipPrimaryInfos.add(vipPrimaryInfo);

            VipInvestInfo currentMonthInvestInfo = new VipInvestInfo();
            currentMonthInvestInfo.setCustomId(Long.parseLong(customId));
            currentMonthInvestInfo.setDayAccountBalance(ExportDataConverter.longTypeConverter(dayAccountBalance));
            currentMonthInvestInfo.setDayAddInvestAmount(ExportDataConverter.longTypeConverter(dayAddInvestAmount));
            currentMonthInvestInfo.setTotalInvestTimes(ExportDataConverter.intTypeConverter(totalInvestTimes) / 100);
            currentMonthInvestInfo.setAccInviInvestPersons(ExportDataConverter.intTypeConverter(accInviInvestPersons) / 100);
            currentMonthInvestInfo.setAccInviInvestAmountYear(ExportDataConverter.longTypeConverter(accInviInvestAmountYear));
            currentMonthInvestInfo.setAccInviInvestAmount(ExportDataConverter.longTypeConverter(accInviInvestAmount));
            currentMonthInvestInfo.setAccInvAmount(ExportDataConverter.longTypeConverter(accInvAmount));
            currentMonthInvestInfo.setAccInvYearAmount(ExportDataConverter.longTypeConverter(accInvYearAmount));
            currentMonthInvestInfo.setDayInvestBalance(ExportDataConverter.longTypeConverter(dayInvestBalance));
            // 投资降幅计算方法 NEED TO CHECK!
            try {
                currentMonthInvestInfo.setDailyDecreasement(vipInvestFlowService.calculateDailyDescreasement(Integer.parseInt(customId), ExportDataConverter.longTypeConverter(dayInvestBalance), ExportDataConverter.longTypeConverter(dayAccountBalance), ymd));
            }
            catch (NumberFormatException | ParseException e) {
                e.printStackTrace();
            }
            currentMonthInvestInfo.setYear(ymd[0]);
            currentMonthInvestInfo.setMonth(ymd[1]);
            long accInvLastMonthAmount = 0;
            int lastMonth = ymd[1] - 1;
            int lastYear = ymd[0];
            if (lastMonth == 0 && isCountAll()) {
                lastYear -= 1;
                lastMonth = ConstantsConfig.MONTH;
                VipInvestInfo lastMonthInvestInfo = new VipInvestInfo();
                lastMonthInvestInfo = this.getVipInvestInfo(lastYear, lastMonth, Long.parseLong(customId));
                accInvLastMonthAmount = lastMonthInvestInfo != null ? lastMonthInvestInfo.getYearMonthInvestAmount() : 0;
            }
            else if (lastMonth > 0) {
                VipInvestInfo lastMonthInvestInfo = new VipInvestInfo();
                lastMonthInvestInfo = this.getVipInvestInfo(lastYear, lastMonth, Long.parseLong(customId));
                accInvLastMonthAmount = lastMonthInvestInfo != null ? lastMonthInvestInfo.getYearMonthInvestAmount() : 0;
            }

            currentMonthInvestInfo.setYearMonthInvestAmount(ExportDataConverter.longTypeConverter(accInvYearAmount) - accInvLastMonthAmount);
            vipPrimaryInfo.setCurrentMonthInvestInfo(currentMonthInvestInfo);

            VipInvestInfo currentYearInVestInfo = new VipInvestInfo();
            currentYearInVestInfo.setCustomId(Long.parseLong(customId));
            currentYearInVestInfo.setDayAccountBalance(ExportDataConverter.longTypeConverter(dayAccountBalance));
            currentYearInVestInfo.setDayAddInvestAmount(ExportDataConverter.longTypeConverter(dayAddInvestAmount));
            currentYearInVestInfo.setTotalInvestTimes(ExportDataConverter.intTypeConverter(totalInvestTimes) / 100);
            currentYearInVestInfo.setAccInviInvestPersons(ExportDataConverter.intTypeConverter(accInviInvestPersons) / 100);
            currentYearInVestInfo.setAccInviInvestAmountYear(ExportDataConverter.longTypeConverter(accInviInvestAmountYear));
            currentYearInVestInfo.setAccInviInvestAmount(ExportDataConverter.longTypeConverter(accInviInvestAmount));
            currentYearInVestInfo.setAccInvAmount(ExportDataConverter.longTypeConverter(accInvAmount));
            currentYearInVestInfo.setAccInvYearAmount(ExportDataConverter.longTypeConverter(accInvYearAmount));
            currentYearInVestInfo.setDayInvestBalance(ExportDataConverter.longTypeConverter(dayInvestBalance));
            currentYearInVestInfo.setYear(ymd[0]);
            currentYearInVestInfo.setMonth(ConstantsConfig.MONTH);
            currentYearInVestInfo.setYearMonthInvestAmount(ExportDataConverter.longTypeConverter(accInvYearAmount));
            vipPrimaryInfo.setCurrentYearInvestInfo(currentYearInVestInfo);
        }
        this.importCustomData(vipPrimaryInfos);

    }

    // TODO To make sure.
    private static boolean isCountAll() {
        return false;
    }

}