package com.hhnz.impl.cfcrm;

import java.util.List;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hhnz.api.cfcrm.constants.APIResponseArgs;
import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.hhnz.api.cfcrm.model.cfcrm.MsgAutoSend;
import com.hhnz.api.cfcrm.model.cfcrm.VipAwardManage;
import com.hhnz.api.cfcrm.service.cfcrm.IMsgAutoSendService;
import com.hhnz.api.cfcrm.service.cfcrm.IPaymentCollectionService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipAwardManageService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipInvestInfoService;
import com.hhnz.api.cfcrm.service.cfcrm.IVipPrimaryInfoService;
import com.tuhanbao.io.objutil.TimeUtil;
import com.tuhanbao.thirdapi.cache.CacheManager;
import com.tuhanbao.thirdapi.cache.ICacheKey;
import com.tuhanbao.thirdapi.cache.NoExpireCacheKey;
import com.tuhanbao.util.log.LogManager;
import com.tuhanbao.util.thread.ScheduledThreadManager;
import com.tuhanbao.web.IServerManager;
import com.tuhanbao.web.controller.authority.TokenService;

@Component
public class DIYServerManager implements IServerManager {	
	  
    public static final ICacheKey WORK_BENCH = new NoExpireCacheKey("workbench");

    @Autowired 
    private IVipInvestInfoService investInfoService;
    
    @Autowired 
    private IVipPrimaryInfoService vipPrimaryInfoService;

    @Autowired 
    private IPaymentCollectionService paymentCollectionService;

    @Autowired 
    private IMsgAutoSendService msgAutoSendService;

    @Autowired 
    private IVipAwardManageService vipAwardManageService;

    public DIYServerManager() {
        
    }
    
    /**
     * 有需要请自行覆盖，服务器启动时会自动执行
     */
    public void init() {
        msgAutoSendService.updateBirthSendMsgTimer();
        TokenService.registerTokenService(new MyTokenService());
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                initWorkBench();
            }
        };
        try {
            ScheduledThreadManager.execute(new Runnable() {

                @Override
                public void run() {
                    task.run();
                }
            });
        }
        catch (Exception e) {
            LogManager.error(e);
        }
        
        ScheduledThreadManager.executeOnTimer(ConstantsConfig.AFFAIR_EXUTE_TIME, 0, 0, task);
        
        final TimerTask msgAutoSendtask = new TimerTask() {
            @Override
            public void run() {
                startMsgAutoSend();
                startAwardAutoSend();
            }
        };
        try {
            ScheduledThreadManager.execute(new Runnable() {

                @Override
                public void run() {
                    msgAutoSendtask.run();
                }
            });
        }
        catch (Exception e) {
            LogManager.error(e);
        }
        //延迟10s，以免获取时间还是昨天
        ScheduledThreadManager.executeOnTimer(0, 0, 10, msgAutoSendtask);
    }
    
	private void initWorkBench() {
	    // 回款人数和回款金额
	    JSONObject json = paymentCollectionService.getVipsAndPaymentBackAmount();
        
    	CacheManager.set(WORK_BENCH, APIResponseArgs.BIRTH, vipPrimaryInfoService.getVipAmountOnBirthday());
    	CacheManager.set(WORK_BENCH, APIResponseArgs.INVEST_REMIND, investInfoService.getVipDailyDesUpSomePercent());
    	CacheManager.set(WORK_BENCH, APIResponseArgs.BACK_MONEY_AMOUNT, json.get(APIResponseArgs.BACK_MONEY_AMOUNT));
    	CacheManager.set(WORK_BENCH, APIResponseArgs.BACK_MONEY_NUM, json.get(APIResponseArgs.BACK_MONEY_NUM));
	}
	
	private void startMsgAutoSend() {
	    List<MsgAutoSend> list = msgAutoSendService.selectAllNeedSendMsg(TimeUtil.now());
	    for (MsgAutoSend item : list) {
	        msgAutoSendService.sendMsg(item);
	    }
    }
	
	private void startAwardAutoSend() {
	    List<VipAwardManage> list = vipAwardManageService.selectAllNeedSendAward(TimeUtil.now());
	    for (VipAwardManage item : list) {
	        vipAwardManageService.sendAward(item);
	    }
	}
}
