package com.hhnz.controller.cfcrm.controller.vm;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hhnz.api.cfcrm.constants.ErrorCode;
import com.hhnz.api.cfcrm.constants.TableConstants;
import com.hhnz.api.cfcrm.constants.enums.FollowingStage;
import com.hhnz.api.cfcrm.model.cfcrm.VipMarketingRecord;
import com.hhnz.api.cfcrm.model.cfcrm.VipPrimaryInfo;
import com.hhnz.api.cfcrm.service.cfcrm.IVipMarketingRecordService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.hhnz.controller.cfcrm.controller.BaseController;
import com.tuhanbao.base.dataservice.filter.Filter;
import com.tuhanbao.base.dataservice.filter.operator.Operator;
import com.tuhanbao.base.dataservice.filter.page.Page;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.web.controller.authority.IUser;
import com.tuhanbao.web.controller.helper.Pagination;
import com.tuhanbao.web.filter.FilterUtil;

@RequestMapping(value = ("/vipMarketingRecord"), produces = "text/html;charset=UTF-8")
@Controller
public class VipMarketingRecordController extends BaseController {

    /**
     * 查看营销记录
     */
    private static final String GET_VIP_MARKETING = "/getVipMarketing";

    @Autowired
    private IVipMarketingRecordService vmrService;

    @Autowired
    private IVipPrimaryInfoService vipPrimaryInfoService;

    /**
     * 查询营销记录
     * 
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = GET)
    @ResponseBody
    public Object getMarketingRecord(HttpServletRequest request, String customId, String customName, Integer flowStage, String startCreateDate,
            String endCreateDate, String startFollowDate, String endFollowDate, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize) {
        Filter filter = new Filter();
        Page page = new Page(pageNum, pageSize);
        filter.setPage(page);
        if (flowStage != null) {
            filter.andFilter(TableConstants.T_VIP_MARKETING_RECORD.FOLLOWING_STAGE, FollowingStage.getFollowingStage(flowStage));
        }
        if (!StringUtil.isEmpty(startCreateDate)) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_MARKETING_RECORD.CREATE_DATE,
                    new Date(TimeUtil.getTimeByDay(startCreateDate)));
        }
        if (!StringUtil.isEmpty(endCreateDate)) {
            filter.andFilter(Operator.LESS, TableConstants.T_VIP_MARKETING_RECORD.CREATE_DATE,
                    new Date(TimeUtil.getTimeByDay(endCreateDate) + TimeUtil.DAY2MILLS));
        }
        if (!StringUtil.isEmpty(startFollowDate)) {
            filter.andFilter(Operator.GREATER_EQUAL, TableConstants.T_VIP_MARKETING_RECORD.FOLLOWING_DATE,
                    new Date(TimeUtil.getTimeByDay(startFollowDate)));
        }
        if (!StringUtil.isEmpty(endFollowDate)) {
            filter.andFilter(Operator.LESS, TableConstants.T_VIP_MARKETING_RECORD.FOLLOWING_DATE,
                    new Date(TimeUtil.getTimeByDay(endFollowDate) + TimeUtil.DAY2MILLS));
        }
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_MARKETING_RECORD.CUSTOM_ID, filter, customId);
        FilterUtil.addLikeWordToFilter(TableConstants.T_VIP_PRIMARY_INFO.NAME, filter, customName);
        filter.addOrderField(TableConstants.T_VIP_MARKETING_RECORD.FOLLOWING_DATE, true);
        List<VipMarketingRecord> list = vmrService.selectVipMarketingRecord(filter);
        return new Pagination(page.getTotalCount(), list);
    }

    /**
     * 新增营销记录
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = ADD)
    @ResponseBody
    public Object addVmr(HttpServletRequest request, Long customId, String dateStr, Integer flowStage, String content) {
        VipMarketingRecord item = new VipMarketingRecord();
        // IUser user = getCurrentUser(request);
        // item.setEditor(user.getUserId());
        if(customId == null){
            throw new MyException("客户ID为必填项");
        }
        item.setContents(content);
        Date now = new Date();
        item.setCreateDate(now);
        item.setEditDate(now);

        if (!StringUtil.isEmpty(dateStr)) {
            item.setFollowingDate(new Date(TimeUtil.getTime(dateStr)));
        }
        if (flowStage != null) {
            item.setFollowingStage(FollowingStage.getFollowingStage(flowStage));
        }
        if (customId != null) {
            VipPrimaryInfo vpi = vipPrimaryInfoService.selectById(customId);

            if (vpi == null) {
                throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            }
			else {
                item.setCustomId(customId);
            }
        }
        vmrService.add(item);
        return NULL;
    }

    /**
     * 删除营销记录
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = DELETE)
    @ResponseBody
    public Object delVmr(HttpServletRequest request, @RequestParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
        }
        vmrService.deleteById(ids);
        return NULL;
    }

    /**
     * 编辑营销记录
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = UPDATE)
    @ResponseBody
    public Object editVmr(HttpServletRequest request, long id, Long customId, String dateStr, Integer flowStage, String content) {
        VipMarketingRecord item = new VipMarketingRecord();
        IUser user = getCurrentUser(request);
        item.setEditorId(user.getUserId());
        item.setId(id);
        item.setContents(content);
        item.setEditDate(new Date());
        if (flowStage != null) {
            item.setFollowingStage(FollowingStage.getFollowingStage(flowStage));
        }

        if (!StringUtil.isEmpty(dateStr)) {
            item.setFollowingDate(new Date(TimeUtil.getTime(dateStr)));
        }
        if (customId != null) {
            VipPrimaryInfo vpi = vipPrimaryInfoService.selectById(customId);

            if (vpi == null) {
                throw new MyException(ErrorCode.ILLEGAL_INCOMING_ARGUMENT);
            }
            else {
                item.setCustomId(customId);
            }
        }
        vmrService.updateSelective(item);
        return NULL;
    }

    /**
     * 查看营销记录详情
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = GET_VIP_MARKETING)
    @ResponseBody
    public Object getVipMarketing(HttpServletRequest request, Long id) {
        if (id == null) {
            throw new MyException("前端所传ID不能为空");
        }
        return vmrService.selectById(id);
    }

}
