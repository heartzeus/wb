package com.hhnz.impl.cfcrm.service.cfcrm;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestFlow;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestFlowService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestInfoService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.api.cfcrm.tool.ExportDataConverter;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.io.excel.util.Excel2007Util;
import com.tuhanbao.io.objutil.ArrayUtil;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.util.log.LogManager;

@Service("vipInvestFlowService")
@Transactional("cfcrmTransactionManager")
public class VipInvestFlowServiceImpl extends ServiceImpl<VipInvestFlow> implements IVipInvestFlowService {
    
    @Autowired
    private IVipPrimaryInfoService vipPrimaryInfoService;

    @Autowired
    private IVipInvestInfoService iVipInvestInfoService;

    @Override
    public void importInvestFlow(List<VipInvestFlow> flows) {
        for (VipInvestFlow vipInvestFlow : flows) {
            this.add(vipInvestFlow);
        }
    }

    /**
     * 导入投资流水
     * 
     * @param is
     */
    @Override
    public void importInvestInfo(InputStream is) {
        String[][] arrays = Excel2007Util.read(is, 0);
        List<VipInvestFlow> vipInvestFlows = new ArrayList<>();
        for (int i = 1; i < arrays.length; i++) {
            String[] array = arrays[i];
            if (ArrayUtil.isEmptyLine(array)) continue;
            int index = 0;
            String date = array[index++]; // 日期
            String teamId = array[index++]; // 分组id
            String siteId = array[index++]; // 分站id
            String siteName = array[index++]; // 分站名
            String investId = array[index++]; // 投资ID
            String invProTitle = array[index++]; // 投资产品标题
            String investDate = array[index++]; // 投资时间
            String expectedBackRate = array[index++]; // 预期收益率
            String customName = array[index++]; // 客户姓名
            String customId = array[index++]; // 客户ID
            String regTeamId = array[index++]; // 注册分组ID
            String investorRegDate = array[index++]; // 注册时间
            String investAmount = array[index++]; // 投资金额
            String annualizedInvest = array[index++]; // 投资金额年化
            String investSource = array[index++]; // 投资来源
            String investTerm = array[index++]; // 投资期限
            String discountCode = array[index++]; // 优惠码
            String discountCustomName = array[index++]; // 优惠码所属人
            String regInviNum = array[index++]; // 注册邀请码
            String discountPersonInvitationName = array[index++]; // 注册邀请码所属人
            String standerType = array[index++]; // 标类别
            String standerStatus = array[index++]; // 标状态
            String settlementDate = array[index++]; // 结算时间
            String invitationMoney = array[index++]; // 机构返利
            String invPerTeam = array[index++]; // 投资推荐人分组
            String discountPersonInvitationGroup = array[index++]; // 注册推荐人分组
            String isFirstInv = array[index++]; // 是否首投
            String repaymentType = array[index++]; // 还款方式
            String standerTag = array[index++]; // 标的标签
            String investInvitation = array[index++]; // 投资人邀请码

            VipInvestFlow vipInvestFlow = new VipInvestFlow();
            vipInvestFlow.setCustomId(Long.parseLong(customId));
            vipInvestFlow.setName(customName);
            // TODO 暂时先将投资来源这么写，确认之后再改
            if ("else".equals(investSource)) {
                vipInvestFlow.setInvestSource(Long.parseLong("0"));
            }
            vipInvestFlow.setExpectedBackRate(expectedBackRate);
            // 解决了数据格式不对的问题
            vipInvestFlow.setInvestorRegDate(TimeUtil.parse("MM/dd/yy", investorRegDate));
            vipInvestFlow.setInvestAmount(ExportDataConverter.longTypeConverter(investAmount));
            vipInvestFlow.setInvestTerm(ExportDataConverter.investTermConverter(investTerm));
            vipInvestFlow.setAnnualizedInvest(annualizedInvest);
            vipInvestFlow.setDiscountCode(discountCode);
            // vipInvestFlow.setDiscountCustomId(Long.parseLong(discountCustomId));
            vipInvestFlow.setDiscountCustomName(discountCustomName);
            // vipInvestFlow.setDiscountCustomCreateDate(new
            // Date(TimeUtil.getTime(discountCustomCreateDate)));
            // vipInvestFlow.setInvitationLevel(Long.parseLong(invitationLevel));
            vipInvestFlow.setInvitationMoney(invitationMoney);
            vipInvestFlow.setStanderType(standerType);
            vipInvestFlow.setStanderStatus(standerStatus);
            // vipInvestFlow.setSettlementStatus(Long.parseLong(settlementStatus));
            if (settlementDate != null && !settlementDate.isEmpty()) {
                vipInvestFlow.setSettlementDate(new Date(TimeUtil.getTime(settlementDate)));
            }
            vipInvestFlow.setRepaymentTyep(repaymentType);
            // vipInvestFlow.setDiscountPersonInvitationId(Long.parseLong(discountPersonInvitationId));
            vipInvestFlow.setDiscountPersonInvitationName(discountPersonInvitationName);
            vipInvestFlow.setDiscountPersonInvitationGroup(discountPersonInvitationGroup);
            vipInvestFlow.setStanderTag(standerTag);
            vipInvestFlow.setInvestInvitation(investInvitation);
            vipInvestFlow.setInvestDate(TimeUtil.parse("MM/dd/yy HH:mm", investDate));
            vipInvestFlows.add(vipInvestFlow);
        }
        vipPrimaryInfoService.updateInvestInfo(vipInvestFlows);
        this.importInvestFlow(vipInvestFlows);

    }

    /**
     * 计算投资降幅方法
     * 
     * @author gzh
     * @param customId
     */
    @Override
    public int calculateDailyDescreasement(int customId, long dayInvestBalance, long dayAccountBalance, int[] ymd) throws ParseException {
        long dayInvestBalanceDayLastMonth = 0L; // 上月月末在投金额
        long dayAccountBalanceDayLastMonth = 0L; // 上月月末现金余额
        // 获取客户上个月最后一天关键数据
        Filter filter4CurrentDayLastMonth = new Filter();
        filter4CurrentDayLastMonth.andFilter(TableConstants.T_VIP_INVEST_INFO.CUSTOM_ID, customId);
        // 如果当前月份是1月份，则上个月为前一年的12月份
        if (ymd[1] == 1) {
            filter4CurrentDayLastMonth.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0] - 1);
            filter4CurrentDayLastMonth.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, 12);
        }
        else {
            filter4CurrentDayLastMonth.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, ymd[0]);
            filter4CurrentDayLastMonth.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, ymd[1] - 1);
        }
        List<VipInvestInfo> listLastMonth = iVipInvestInfoService.select(filter4CurrentDayLastMonth);
        if (listLastMonth != null && listLastMonth.size() > 0) {
            dayInvestBalanceDayLastMonth = listLastMonth.get(0).getDayInvestBalance();
            dayAccountBalanceDayLastMonth = listLastMonth.get(0).getDayAccountBalance();
        }

        // 调用计算公式，得出当日降幅。
        int dailyDescreasement = calFormula(dayInvestBalance, dayAccountBalance, dayInvestBalanceDayLastMonth, dayAccountBalanceDayLastMonth);        
        return dailyDescreasement;
    }

    /**
     * 投资降幅具体计算公式
     * 
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    private int calFormula(long dayInvestBalance, long dayAccountBalance, long dayInvestBalanceDayLastMonth, long dayAccountBalanceDayLastMonth) {
        double decreasePercent = 0;
        if (dayInvestBalanceDayLastMonth + dayAccountBalanceDayLastMonth != 0) {
            decreasePercent = 1 - ((double)dayInvestBalance + (double)dayAccountBalance) / ((double)dayInvestBalanceDayLastMonth + (double)dayAccountBalanceDayLastMonth);
            LogManager.info("当日在投金额: " + dayInvestBalance + " 当日现金余额: " + dayAccountBalance + " 上月月末在投金额: " + dayInvestBalanceDayLastMonth + " 上月月末现金余额: " + dayAccountBalanceDayLastMonth + " 同比上月份投资降幅: " + decreasePercent );
        }
        int percent = (new Double(decreasePercent * 100)).intValue();
        return percent;
    }

}