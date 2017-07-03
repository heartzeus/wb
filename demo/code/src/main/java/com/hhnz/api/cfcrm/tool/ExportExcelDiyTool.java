package com.hhnz.api.cfcrm.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.enums.CustomType;
import com.hhnz.api.cfcrm.constants.enums.OfficeType;
import com.hhnz.api.cfcrm.constants.enums.Sex;
import com.hhnz.api.cfcrm.model.cfcrm.Area;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IAreaService;
import com.tuhanbao.base.util.objutil.StringUtil;
import com.tuhanbao.base.util.rm.ResourceManager;

@Component
public class ExportExcelDiyTool {
    @Autowired
    private IAreaService iAreaService; // 添加所需service的私有成员
    private static ExportExcelDiyTool exportExcelDiyTool; // 关键点1 静态初使化 一个工具类
                                                          // 这样是为了在spring初使化之前

    private static int indexRow = 0;
    private static short fontSize = 12;
    private static String fontName = "宋体";
    private static String comFert = "复肥客户";
    private static String unComFert = "非复肥客户";

    public void setIAreaService(IAreaService iAreaService) {
        this.iAreaService = iAreaService;
    }

    @PostConstruct // 关键二 通过@PostConstruct 和 @PreDestroy 方法 实现初始化和销毁bean之前进行的操作
    public void init() {
        exportExcelDiyTool = this;
        exportExcelDiyTool.iAreaService = this.iAreaService; // 初使化时将已静态化的testService实例化
    }

    /**
     * 导出VIP客户理财信息方法
     * 
     * @author gzh
     * @param selectInvestInfo
     * @return
     */
    public static HSSFWorkbook exportVIPInfo(List<VipPrimaryInfo> selectInvestInfo) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(ConstantsConfig.FILE_NAME);
        HSSFFont font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        HSSFRow row = sheet.createRow(indexRow + 0);
        HSSFCell title1 = row.createCell(0);
        title1.setCellValue("客户ID");
        HSSFCell title2 = row.createCell(1);
        title2.setCellValue("姓名");
        HSSFCell title3 = row.createCell(2);
        title3.setCellValue("注册手机号");
        HSSFCell title4 = row.createCell(3);
        title4.setCellValue("性别");
        HSSFCell title5 = row.createCell(4);
        title5.setCellValue("客户本人邀请码");
        HSSFCell title6 = row.createCell(5);
        title6.setCellValue("生日");
        HSSFCell title7 = row.createCell(6);
        title7.setCellValue("客户渠道");
        HSSFCell title8 = row.createCell(7);
        title8.setCellValue("当日在投余额");
        HSSFCell title9 = row.createCell(8);
        title9.setCellValue("当日账户余额");
        HSSFCell title10 = row.createCell(9);
        title10.setCellValue("当日新增投资额");
        HSSFCell title11 = row.createCell(10);
        title11.setCellValue("总投资笔数");
        HSSFCell title12 = row.createCell(11);
        title12.setCellValue("累计投资额");
        HSSFCell title13 = row.createCell(12);
        title13.setCellValue("累计年化投资额");
        HSSFCell title14 = row.createCell(13);
        title14.setCellValue("累计邀请投资人数");
        HSSFCell title15 = row.createCell(14);
        title15.setCellValue("累计邀请投资额");
        HSSFCell title16 = row.createCell(15);
        title16.setCellValue("累计邀请年化投资额");
        HSSFCell title17 = row.createCell(16);
        title17.setCellValue("客户标签");
        HSSFCell title18 = row.createCell(17);
        title18.setCellValue("客户区域");
        HSSFCell title19 = row.createCell(18);
        title19.setCellValue("首次注册VIP时间");
        int flag = 1;
        for (VipPrimaryInfo vipPrimaryInfo : selectInvestInfo) {
            long customId = vipPrimaryInfo.getId(); // 客户ID
            String name = vipPrimaryInfo.getName(); // 姓名
            String phone = vipPrimaryInfo.getPhone(); // 注册手机号
            Sex sex = vipPrimaryInfo.getSex(); // 性别
            String inviteNum = vipPrimaryInfo.getInvitateNum(); // 客户本人邀请码
            String sexTag = ResourceManager.getResource(sex);
            StringBuilder tag = new StringBuilder();
            tag.append(vipPrimaryInfo.getBeVip() ? "VIP" : "普通");
            // 数据库改成枚举
            if (vipPrimaryInfo.getOfficeType() != null && vipPrimaryInfo.getOfficeType() != OfficeType.DEFAULT) {
                tag.append(ResourceManager.getResource(vipPrimaryInfo.getOfficeType()));
                vipPrimaryInfo.setFlag(tag.toString());
            }
            else {
                vipPrimaryInfo.setFlag("非分账用户");
            }
            if (StringUtil.isEmpty(inviteNum)) {
                inviteNum = ConstantsConfig.DEFAULT;
            }
            String label = vipPrimaryInfo.getFlag(); // 客户标签
            StringBuilder birthday = new StringBuilder();
            birthday.append(vipPrimaryInfo.getMonth()).append("月").append(vipPrimaryInfo.getDay()).append("日");
            String customResource = ""; // 客户渠道
            if (vipPrimaryInfo.getCustomType() == null) {
                customResource = ConstantsConfig.DEFAULT;
            }
            else if (vipPrimaryInfo.getCustomType().equals(CustomType.IS_FH)) {
                customResource = comFert;
            }
            else if (vipPrimaryInfo.getCustomType().equals(CustomType.NOT_FH)) {
                customResource = unComFert;
            }
            double dayInvestBalance = 0; // 当日在投余额
            double dayAccountBalance = 0; // 当日账户余额
            double dayAddInvestAmount = 0; // 当日新增投资额
            int totalInvestTimes = 0; // 总投资笔数
            double accInvAmount = 0; // 累计投资额
            double accInvYearAmount = 0; // 累计年化投资额
            int accInviInvestPersons = 0; // 累计邀请投资人数
            double accInviInvestAmount = 0; // 累计邀请投资额
            double accInviInvestAmountYear = 0; // 累计邀请年化投资额

            for (VipInvestInfo info : vipPrimaryInfo.getVipInvestInfos()) {
                dayInvestBalance = (double)info.getDayInvestBalance();
                dayAccountBalance = (double)info.getDayAccountBalance();
                dayAddInvestAmount = (double)info.getDayAddInvestAmount();
                totalInvestTimes = info.getTotalInvestTimes();
                accInvAmount = (double)info.getAccInvAmount();
                accInvYearAmount = (double)info.getAccInvYearAmount();
                accInviInvestPersons = info.getAccInviInvestPersons();
                accInviInvestAmount = (double)info.getAccInviInvestAmount();
                accInviInvestAmountYear = (double)info.getAccInviInvestAmountYear();
            }
            StringBuilder cusArea = new StringBuilder(); // 客户区域
            // 根据客户地区ID来获取省市区名称
            if (StringUtil.isEmpty(vipPrimaryInfo.getVipDistrictId()) || StringUtil.isEmpty(vipPrimaryInfo.getVipCityId()) || StringUtil.isEmpty(vipPrimaryInfo.getVipProvinceId())) {
                cusArea.append(ConstantsConfig.DEFAULT);
            }
            else {
                // TODO 放入缓存
                Area district = exportExcelDiyTool.iAreaService.selectById(vipPrimaryInfo.getVipDistrictId());
                Area province = exportExcelDiyTool.iAreaService.selectById(vipPrimaryInfo.getVipProvinceId());
                Area city = exportExcelDiyTool.iAreaService.selectById(vipPrimaryInfo.getVipCityId());
                cusArea.append(province.getAreaName()).append(city.getAreaName()).append(district.getAreaName());
            }
            Date time = vipPrimaryInfo.getVipStart(); // 首次注册成为vip时间
            String start = ConstantsConfig.DEFAULT;
            if (time != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                start = sdf.format(time);
            }
            HSSFRow rowValue = sheet.createRow(indexRow + flag);
            HSSFCell value0 = rowValue.createCell(0);
            value0.setCellValue(customId);
            HSSFCell value1 = rowValue.createCell(1);
            value1.setCellValue(name);
            HSSFCell value2 = rowValue.createCell(2);
            value2.setCellValue(phone);
            HSSFCell value3 = rowValue.createCell(3);
            value3.setCellValue(sexTag);
            HSSFCell value4 = rowValue.createCell(4);
            value4.setCellValue(inviteNum);
            HSSFCell value5 = rowValue.createCell(5);
            value5.setCellValue(birthday.toString());
            HSSFCell value6 = rowValue.createCell(6);
            value6.setCellValue(customResource);
            HSSFCell value7 = rowValue.createCell(7);
            value7.setCellValue(dayInvestBalance / 100);
            HSSFCell value8 = rowValue.createCell(8);
            value8.setCellValue(dayAccountBalance / 100);
            HSSFCell value9 = rowValue.createCell(9);
            value9.setCellValue(dayAddInvestAmount / 100);
            HSSFCell value10 = rowValue.createCell(10);
            value10.setCellValue(totalInvestTimes);
            HSSFCell value11 = rowValue.createCell(11);
            value11.setCellValue(accInvAmount / 100);
            HSSFCell value12 = rowValue.createCell(12);
            value12.setCellValue(accInvYearAmount / 100);
            HSSFCell value13 = rowValue.createCell(13);
            value13.setCellValue(accInviInvestPersons);
            HSSFCell value14 = rowValue.createCell(14);
            value14.setCellValue(accInviInvestAmount / 100);
            HSSFCell value15 = rowValue.createCell(15);
            value15.setCellValue(accInviInvestAmountYear / 100);
            HSSFCell value16 = rowValue.createCell(16);
            value16.setCellValue(label);
            HSSFCell value17 = rowValue.createCell(17);
            value17.setCellValue(cusArea.toString());
            HSSFCell value18 = rowValue.createCell(18);
            value18.setCellValue(start);
            flag++;

        }

        return workbook;

    }

}
