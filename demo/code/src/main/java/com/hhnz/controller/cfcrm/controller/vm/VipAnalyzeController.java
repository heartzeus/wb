package com.hhnz.controller.cfcrm.controller.vm;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.hhnz.api.cfcrm.constants.APIResponseArgs;
import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.CustomType;
import com.hhnz.api.cfcrm.constants.enums.OfficeType;
import com.hhnz.api.cfcrm.constants.enums.RankType;
import com.hhnz.api.cfcrm.constants.enums.Sex;
import com.hhnz.api.cfcrm.model.cfcrm.VipInvestInfo;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IDiyFilterService;
import com.hhnz.api.cfcrm.service.cfcrm.IPaymentCollectionService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestFlowService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.api.cfcrm.tool.ExportDataConverter;
import com.hhnz.api.cfcrm.tool.ExportExcelDiyTool;
import com.hhnz.api.cfcrm.tool.TimeTool;
import com.hhnz.controller.cfcrm.controller.BaseController;
import com.hhnz.impl.cfcrm.DIYServerManager;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.page.Page;
import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.thirdapi.cache.CacheManager;
import com.tuhanbao.util.ResourceManager;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.controller.helper.Pagination;

@RequestMapping(value = ("/analyze"), produces = "text/html;charset=UTF-8")
@Controller
public class VipAnalyzeController extends BaseController {

    public static final String VIP_MSG = "/vipMsg";

    public static final String GET_VIP_DETAIL = "/getVipDetail";

    public static final String ADD_VIP = "/addVip";

    public static final String EDIT_VIP = "/editVip";

    public static final String DEL_VIP = "/delVip";

    public static final String RANK_VIP = "/rankVip";

    public static final String IMPORT_CUSTOM_INFO = "/importCustomInfo";

    public static final String IMPORT_INVEST_INFO = "/importInvestInfo";

    public static final String IMPORT_BACKMONEY_INFO = "/importBackMoneyInfo";

    public static final String EXPORT_INFO = "/exportInfo";

    public static final String WORK_BENCH_CON = "/workBenchCon";
    
    private static final String VIP = "VIP";
    
    private static final String ORIDINARY = "普通";
    
    private static final String NOT_FZ_USER = "非分账用户";

    @Autowired
    private IDiyFilterService iFilterService;
    @Autowired
    private IVipPrimaryInfoService iVipPrimaryInfoService;
    @Autowired
    private IVipInvestFlowService iVipInvestFlowService;
    @Autowired
    private IPaymentCollectionService iPaymentCollectionService;

    /**
     * 查询根据自定义条件过滤出的数据
     * 
     * @param request
     * @param filterId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = VIP_MSG)
    @ResponseBody
    public Object getVipList(HttpServletRequest request, Long filterId, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize) {
        Filter filter = null;
        if (filterId != null) {
            filter = iFilterService.getFilter(filterId);
        }
        else {
            filter = new Filter();
        }
        int[] currentTime = TimeTool.getCurentYMD();
        int currentYear = currentTime[0];
        int currentMonth = currentTime[1];

        // 如果年月都不为空
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, currentYear);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, currentMonth);

        // 将获取的数据按照排名升序排列
        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.RANK, false);

        Page page = new Page(pageNum, pageSize);
        filter.setPage(page);
        List<VipPrimaryInfo> selectInvestInfo = iVipPrimaryInfoService.selectByAutoFilter(filter);
        // TODO CHECK
        for (VipPrimaryInfo vipPrimaryInfo : selectInvestInfo) {
            StringBuilder flag = new StringBuilder();
            flag.append(vipPrimaryInfo.getBeVip() ? VIP : ORIDINARY);
            // 数据库改成枚举
            if (vipPrimaryInfo.getOfficeType() != null && vipPrimaryInfo.getOfficeType() != OfficeType.DEFAULT) {
                flag.append(ResourceManager.getResource(vipPrimaryInfo.getOfficeType()));
                vipPrimaryInfo.setFlag(flag.toString());
             }
            else {
                vipPrimaryInfo.setFlag(NOT_FZ_USER);
            }

        }
        return new Pagination(page.getTotalCount(), selectInvestInfo);

    }

    /**
     * 查询根据自定义条件过滤出的数据
     * 
     * @param request
     * @param filterId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = GET_VIP_DETAIL)
    @ResponseBody
    public Object getVipDetail(HttpServletRequest request, long id) {
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, id);
        List<VipPrimaryInfo> selectInvestInfo = iVipPrimaryInfoService.selectByAutoFilter(filter);
        if (selectInvestInfo.isEmpty()) {
            throw new MyException(ErrorCode.NO_RECORD_DATA);
        }
        return selectInvestInfo.get(0);
    }

    /**
     * 客户管理界面 编辑用户接口
     * 
     * @param request
     * @param filterId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = EDIT_VIP)
    @ResponseBody
    public Object editAreaDetail(HttpServletRequest request, Long id, String provinceId, String cityId, String districtId, String detailAddress) {
        if (id == null) {
            throw new MyException(ErrorCode.ILLEGAL_ARGUMENT);
        }
        // 根据最小区域id来查询其对应的市id和省id
        Map<String, Object> map = iVipPrimaryInfoService.getCtPrMapByDsId(districtId);
        String provinceAreaId = map.get("PROVENCEID").toString();
        String cityAreaId = map.get("CITYID").toString();
        // 如果省ID和市ID前端都传了，要进行后端验证并判断
        if (!provinceId.isEmpty() && !cityId.isEmpty()) {
            if (!provinceAreaId.equals(provinceId) || !cityAreaId.equals(cityId)) {
                throw new MyException(ErrorCode.PRO_CITY_ID_ERROR);
            }
        }
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, id);
        VipPrimaryInfo vipPrimaryInfo = new VipPrimaryInfo();
        vipPrimaryInfo.setVipProvinceId(provinceAreaId);
        vipPrimaryInfo.setVipCityId(cityAreaId);
        vipPrimaryInfo.setVipDistrictId(districtId);
        vipPrimaryInfo.setDetailAddress(detailAddress);
        iVipPrimaryInfoService.updateSelective(vipPrimaryInfo, filter);
        return NULL;
    }

    /**
     * 客户管理界面 删除用户接口
     * 
     * @param request
     * @param filterId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = DEL_VIP)
    @ResponseBody
    public Object delVip(HttpServletRequest request, @RequestParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_ARGUMENT);
        }
        Filter filter = new Filter();
        filter.andFilter(TableConstants.T_VIP_PRIMARY_INFO.ID, ids);

        VipPrimaryInfo vipPrimaryInfo = new VipPrimaryInfo();
        vipPrimaryInfo.setIsDel(true);
        iVipPrimaryInfoService.updateSelective(vipPrimaryInfo, filter);
        return NULL;
    }

    /**
     * 客户管理界面 新增用户接口
     * 
     * @param request
     * @param filterId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = ADD_VIP)
    @ResponseBody
    public Object addVipById(HttpServletRequest request, String dayInvestBalance, String dayAccountBalance, String dayAddInvestAmount,
            String accInvestAmount, String accInvestYearAmount, String totalInvestTimes, String accInviInvestPersons, String accInviInvestAmount,
            String accInviInvestAmountYear) {
        String userName = request.getParameter("userName");// 用户名
        long id = Long.parseLong(request.getParameter("id"));// 用户网信id
        String phone = request.getParameter("phone");// 用户手机号
        Sex sex = Sex.getSex(Integer.parseInt(request.getParameter("sex")));// 性别
        Date birthday = TimeUtil.parse("yyyy-MM-dd", request.getParameter("birthday"));// 生日
        String province = request.getParameter("province");// 省
        String city = request.getParameter("city");// 市
        String district = request.getParameter("district");// 区
        String detailAddr = request.getParameter("detailAddr");// 详细地址
        int customType = Integer.parseInt(request.getParameter("customType"));// 客户类型
        // double dayInvestBalance =
        // Double.parseDouble(request.getParameter("dayInvestBalance"));//
        // 当日在投余额
        // long dayAccountBalance =
        // Long.parseLong(request.getParameter("dayAccountBalance"));// 当日可用余额
        // long dayAddInvestAmount =
        // Long.parseLong(request.getParameter("dayAddInvestAmount"));// 当日回款余额
        // long accInvestAmount =
        // Long.parseLong(request.getParameter("accInvestAmount"));// 累计投资额
        // long accInvestYearAmount =
        // Long.parseLong(request.getParameter("accInvestYearAmount"));//
        // 累计年化投资额
        // int totalInvestTimes =
        // Integer.parseInt(request.getParameter("totalInvestTimes"));// 总投资笔数
        // int accInviInvestPersons =
        // Integer.parseInt(request.getParameter("accInviInvestPersons"));//
        // 累计邀请投资人数
        // long accInviInvestAmount =
        // Long.parseLong(request.getParameter("accInviInvestAmount"));//
        // 累计邀请投资额
        // long accInviInvestAmountYear =
        // Long.parseLong(request.getParameter("accInviInvestAmountYear"));//
        // 累计邀请年化投资额

        VipPrimaryInfo vipPrimaryInfo = new VipPrimaryInfo();
        vipPrimaryInfo.setId(id);
        vipPrimaryInfo.setPhone(phone);
        vipPrimaryInfo.setName(userName);
        vipPrimaryInfo.setSex(sex);
        vipPrimaryInfo.setBirthday(birthday);
        vipPrimaryInfo.setVipProvinceId(province);
        vipPrimaryInfo.setVipCityId(city);
        vipPrimaryInfo.setVipDistrictId(district);
        vipPrimaryInfo.setDetailAddress(detailAddr);
        vipPrimaryInfo.setInvitateNum(Constants.EMPTY);
        vipPrimaryInfo.setCustomType(CustomType.getCustomType(customType));
        vipPrimaryInfo.setIsDel(false);
        int[] birthymd = TimeUtil.getYearMonthDayHour(birthday.getTime());
        vipPrimaryInfo.setMonth(birthymd[1]);
        vipPrimaryInfo.setDay(birthymd[2]);

        int[] ymd = TimeTool.getCurentYMD();
        VipInvestInfo currentMonthInvestInfo = new VipInvestInfo();
        currentMonthInvestInfo.setCustomId(id);
        currentMonthInvestInfo.setAccInvAmount(ExportDataConverter.longTypeConverter(accInvestAmount));
        currentMonthInvestInfo.setAccInviInvestAmount(ExportDataConverter.longTypeConverter(accInviInvestAmount));
        currentMonthInvestInfo.setAccInviInvestAmountYear(ExportDataConverter.longTypeConverter(accInviInvestAmountYear));
        currentMonthInvestInfo.setAccInviInvestPersons(Integer.valueOf(accInviInvestPersons));
        currentMonthInvestInfo.setAccInvYearAmount(ExportDataConverter.longTypeConverter(accInvestYearAmount));
        currentMonthInvestInfo.setDayAccountBalance(ExportDataConverter.longTypeConverter(dayAccountBalance));
        currentMonthInvestInfo.setDayAddInvestAmount(ExportDataConverter.longTypeConverter(dayAddInvestAmount));
        currentMonthInvestInfo.setDayInvestBalance(ExportDataConverter.longTypeConverter(dayInvestBalance));
        currentMonthInvestInfo.setTotalInvestTimes(Integer.valueOf(totalInvestTimes));
        currentMonthInvestInfo.setYearMonthInvestAmount(ExportDataConverter.longTypeConverter(accInvestAmount));
        currentMonthInvestInfo.setYear(ymd[0]);
        currentMonthInvestInfo.setMonth(ymd[1]);
        vipPrimaryInfo.setCurrentMonthInvestInfo(currentMonthInvestInfo);

        VipInvestInfo currentYearInvestInfo = new VipInvestInfo();
        currentYearInvestInfo.setCustomId(id);
        currentYearInvestInfo.setAccInvAmount(ExportDataConverter.longTypeConverter(accInvestAmount));
        currentYearInvestInfo.setAccInviInvestAmount(ExportDataConverter.longTypeConverter(accInviInvestAmount));
        currentYearInvestInfo.setAccInviInvestAmountYear(ExportDataConverter.longTypeConverter(accInviInvestAmountYear));
        currentYearInvestInfo.setAccInviInvestPersons(Integer.valueOf(accInviInvestPersons));
        currentYearInvestInfo.setAccInvYearAmount(ExportDataConverter.longTypeConverter(accInvestYearAmount));
        currentYearInvestInfo.setDayAccountBalance(ExportDataConverter.longTypeConverter(dayAccountBalance));
        currentYearInvestInfo.setDayAddInvestAmount(ExportDataConverter.longTypeConverter(dayAddInvestAmount));
        currentYearInvestInfo.setDayInvestBalance(ExportDataConverter.longTypeConverter(dayInvestBalance));
        currentYearInvestInfo.setTotalInvestTimes(Integer.valueOf(totalInvestTimes));
        currentYearInvestInfo.setYearMonthInvestAmount(ExportDataConverter.longTypeConverter(accInvestAmount));
        currentYearInvestInfo.setYear(ymd[0]);
        currentYearInvestInfo.setMonth(ConstantsConfig.MONTH);
        vipPrimaryInfo.setCurrentYearInvestInfo(currentYearInvestInfo);

        iVipPrimaryInfoService.createNewCustom(vipPrimaryInfo);

        return NULL;

    }

    /**
     * 查询根据自定义条件过滤出的用户排名
     * 
     * @param request
     * @param pageNum
     * @param pageSize
     * @param year
     * @param month
     * @return
     */
    @RequestMapping(value = RANK_VIP)
    @ResponseBody
    public Object showVipRank(HttpServletRequest request, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, Integer year,
            Integer month, Integer rankType) {
        Filter filter4VipRankDetail = new Filter();
        int[] ymd = TimeTool.getCurentYMD();

        // 这么写我觉得没什么问题  如果year字段为空，则默认当前年
        if (year == null) {
            year = ymd[0];            
        }

        // 如果年月都不为空
        filter4VipRankDetail.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, year);
        // 如果年不为空，月为空
        if (month == null) {
            month = ConstantsConfig.MONTH;
        }
        filter4VipRankDetail.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, month);
        if (rankType == RankType.RANK.value) {
            filter4VipRankDetail.addOrderField(TableConstants.T_VIP_INVEST_INFO.RANK, false);
        }else if (rankType == RankType.RANK_ACC.value) {
            filter4VipRankDetail.addOrderField(TableConstants.T_VIP_INVEST_INFO.RANK_ACC, false);
        }

        filter4VipRankDetail.andFilter(TableConstants.T_VIP_PRIMARY_INFO.IS_DEL, false);
        // 将获取的数据按照排名升序排列

        Page page = new Page(pageNum, pageSize);
        filter4VipRankDetail.setPage(page);
        List<VipPrimaryInfo> selectInvestInfo = iVipPrimaryInfoService.selectByAutoFilter(filter4VipRankDetail);
        for (VipPrimaryInfo vipPrimaryInfo : selectInvestInfo) {
            if (rankType == RankType.RANK.value) {
                vipPrimaryInfo.setRank(vipPrimaryInfo.getVipInvestInfos().get(0).getRank());
            } else if (rankType == RankType.RANK_ACC.value) {
                vipPrimaryInfo.setRank(vipPrimaryInfo.getVipInvestInfos().get(0).getRankAcc());
            }
        }
        return new Pagination(page.getTotalCount(), selectInvestInfo);
    }

    /**
     * 前端调用工作台接口
     * 
     * @param request
     * @param filterId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = WORK_BENCH_CON)
    @ResponseBody
    public Object workBench(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        json.put(APIResponseArgs.BIRTH, CacheManager.get(DIYServerManager.WORK_BENCH, APIResponseArgs.BIRTH));
        json.put(APIResponseArgs.INVEST_REMIND, CacheManager.get(DIYServerManager.WORK_BENCH, APIResponseArgs.INVEST_REMIND));
        json.put(APIResponseArgs.BACK_MONEY_AMOUNT, CacheManager.get(DIYServerManager.WORK_BENCH, APIResponseArgs.BACK_MONEY_AMOUNT));
        json.put(APIResponseArgs.BACK_MONEY_NUM, CacheManager.get(DIYServerManager.WORK_BENCH, APIResponseArgs.BACK_MONEY_NUM));
        return json;

    }

    /**
     * 导入客户excel
     * 
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = IMPORT_CUSTOM_INFO)
    @ResponseBody
    public Object importCustomInfo(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest mhs = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> mapFile = mhs.getFileMap();
        if (mapFile.isEmpty()) {
            return null;
        }
        for (String s : mapFile.keySet()) {
            MultipartFile file = mapFile.get(s);
            iVipPrimaryInfoService.importCustomInfo(file.getInputStream());
        }
        return NULL;
    }

    /**
     * 导入投资excel
     * 
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = IMPORT_INVEST_INFO)
    @ResponseBody
    public Object importInvestInfo(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest mhs = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> mapFile = mhs.getFileMap();
        if (mapFile.isEmpty()) {
            return null;
        }
        for (String s : mapFile.keySet()) {
            MultipartFile file = mapFile.get(s);
            iVipInvestFlowService.importInvestInfo(file.getInputStream());
        }
        return NULL;
    }

    /**
     * 导入回款excel
     * 
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = IMPORT_BACKMONEY_INFO)
    @ResponseBody
    public Object importBackMoneyInfo(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest mhs = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> mapFile = mhs.getFileMap();
        if (mapFile.isEmpty()) {
            return null;
        }
        for (String s : mapFile.keySet()) {
            MultipartFile file = mapFile.get(s);
            iPaymentCollectionService.importBackMoneyInfo(file.getInputStream());
        }
        return NULL;

    }

    /**
     * 导出客户信息接口
     * 支持不分页，导出全部
     * 符合过滤条件数据
     * @author gzh
     * @param request
     * @param response
     * @param filterId
     * @return
     */
    @RequestMapping(value = EXPORT_INFO)
    @ResponseBody
    public Object exportInfo(HttpServletRequest request, HttpServletResponse response, Long filterId) {
        Filter filter = null;
        if (filterId != null) {
            filter = iFilterService.getFilter(filterId);
        }
        else {
            filter = new Filter();
        }
        int[] currentTime = TimeUtil.getTodayYearMonthDayHour();
        int currentYear = currentTime[0];
        int currentMonth = currentTime[1];
        int currentDay = currentTime[2];
        // 如果年月都不为空
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.YEAR, currentYear);
        filter.andFilter(TableConstants.T_VIP_INVEST_INFO.MONTH, currentMonth);
        // 将获取的数据按照排名升序排列
        filter.addOrderField(TableConstants.T_VIP_INVEST_INFO.RANK, false);
        List<VipPrimaryInfo> selectInvestInfo = iVipPrimaryInfoService.selectByAutoFilter(filter);
        try {
            response.setContentType("application/x-download");// 下面三行是关键代码，处理乱码问题
            response.setCharacterEncoding(Constants.UTF_8);
            // 生成文件名称规则暂定为名称+时间(年月日)
//            String filename = ConstantsConfig.FILE_NAME + currentYear + "/" + currentMonth + "/" + currentDay;
            String filename = currentYear + "/" + currentMonth + "/" + currentDay;
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gbk"), "iso8859-1") + ".xls");
            ServletOutputStream os = response.getOutputStream();
            HSSFWorkbook wb = ExportExcelDiyTool.exportVIPInfo(selectInvestInfo);
            wb.write(os);
        }
        catch (IOException e) {
            throw new MyException(ErrorCode.EXPORT_ERROR);
        }

        return NULL;
    }
}
