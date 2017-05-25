package com.hhnz.impl.cfcrm.service.cfcrm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.hhnz.api.cfcrm.constants.APIResponseArgs;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.MoneyBackStatus;
import com.hhnz.api.cfcrm.model.cfcrm.PaymentCollection;
import com.hhnz.api.cfcrm.service.cfcrm.IPaymentCollectionService;
import com.hhnz.api.cfcrm.tool.ExportDataConverter;
import com.hhnz.api.cfcrm.tool.TimeTool;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.io.excel.util.Excel2007Util;
import com.tuhanbao.io.objutil.ArrayUtil;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.web.filter.MyBatisSelector;

@Service("paymentCollectionService")
@Transactional("cfcrmTransactionManager")
public class PaymentCollectionServiceImpl extends ServiceImpl<PaymentCollection> implements IPaymentCollectionService {

    public static final String Vips = "Vips";
    public static final String PaymentCollectionAmount = "PaymentCollectionAmount";
    private static MyBatisSelector CUSTOM_REMINDER_SELECTOR_RIGHT = new MyBatisSelector(TableConstants.T_PAYMENT_COLLECTION.TABLE);
    static{
        CUSTOM_REMINDER_SELECTOR_RIGHT.joinTable(TableConstants.T_VIP_PRIMARY_INFO.TABLE);
    }


    // 获取当日回款客户数量和回款总金额
    @Override
    public JSONObject getVipsAndPaymentBackAmount() {
        Filter filter4Vips = new Filter();
        // 根据回款日与当前系统日期相等和回款状态为1进行筛选
        // filter4Vips.andFilter(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE,
        // TimeUtil.getCurrentTime());
        // 不清楚这个可不可以用time tool的方法替换
        filter4Vips.andFilter(TableConstants.T_PAYMENT_COLLECTION.PAYMENT_COLLECTION_DATE, TimeTool.getCurentYMD());
        filter4Vips.andFilter(TableConstants.T_PAYMENT_COLLECTION.STATUS, MoneyBackStatus.BACK);
        List<PaymentCollection> list4Vips = select(filter4Vips);
        long paymentCollectionAmount = 0;
        for (PaymentCollection paymentCollection : list4Vips) {
            paymentCollectionAmount += paymentCollection.getPaymentCollection();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(APIResponseArgs.BACK_MONEY_NUM, list4Vips.size());
        jsonObject.put(APIResponseArgs.BACK_MONEY_AMOUNT, paymentCollectionAmount);
        return jsonObject;

    }

    @Override
    public void importPaymentCollection(List<PaymentCollection> paymentCollections) {
        for (PaymentCollection paymentCollection : paymentCollections) {
            this.add(paymentCollection);
        }
    }

    /**
     * 回款计划导入
     * 
     * @author gzh
     * @param is
     */
    @Override
    public void importBackMoneyInfo(InputStream is) {
        String[][] arrays = Excel2007Util.read(is, 0);
        List<PaymentCollection> paymentCollections = new ArrayList<>();
        for (int i = 1; i < arrays.length; i++) {
            String[] array = arrays[i];
            if (ArrayUtil.isEmptyLine(array)) continue;
            int index = 0;
            String customId = array[index++]; // 客户id
            String investId = array[index++]; // 投资id
            String customName = array[index++]; // 客户姓名
            String projectName = array[index++]; // 项目名称
            String capital = array[index++]; // 本金
            String profit = array[index++]; // 收益
            String status = array[index++]; // 状态
            String backTimePlan = array[index++]; // 计划回款时间
            String backTimeFact = array[index++]; // 实际回款时间

            PaymentCollection paymentCollection = new PaymentCollection();
            paymentCollection.setCustomId(Long.parseLong(customId));
            paymentCollection.setProjectName(projectName);
            paymentCollection.setCapital(ExportDataConverter.intTypeConverter(capital));
            paymentCollection.setProfit(ExportDataConverter.intTypeConverter(profit));
            paymentCollection.setStatus(MoneyBackStatus.getMoneyBackStatus(Integer.parseInt(status)));
            // TODO 找产品确认回款金额 以及回款时间是计划的还是实际的
            // paymentCollection.setPaymentCollection(Long.parseLong(paymentCollectionMoney));
            paymentCollection.setPaymentCollectionDate(TimeUtil.parse("MM/dd/yy HH:mm", backTimeFact));
            paymentCollections.add(paymentCollection);
        }
        this.importPaymentCollection(paymentCollections);

    }

    @Override
    public List<PaymentCollection> selectCustomReminder(Filter filter) {
        List<PaymentCollection> customReminder = this.select(CUSTOM_REMINDER_SELECTOR_RIGHT, filter);
        return customReminder;
    }

}