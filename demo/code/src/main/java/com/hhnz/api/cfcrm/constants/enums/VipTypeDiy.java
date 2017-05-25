package com.hhnz.api.cfcrm.constants.enums;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;

public enum VipTypeDiy {
    /**
     * 长线客户
     */
    LONG_VIP(VipType.LONG_VIP, ConstantsConfig.MAX_DAY, Integer.MAX_VALUE),

    /**
     * 短线客户
     */
    SHORT_VIP(VipType.SHORT_VIP, ConstantsConfig.ZERO_DAY, ConstantsConfig.MIN_DAY),

    /**
     * 中线客户
     */
    MEDIUM_VIP(VipType.MEDIUM_VIP, ConstantsConfig.MIN_DAY, ConstantsConfig.MEDIUM_DAY),

    /**
     * 中长线客户
     */
    MEDIUM_LONG_VIP(VipType.MEDIUM_LONG_VIP, ConstantsConfig.MEDIUM_DAY, ConstantsConfig.MAX_DAY);

    public final VipType value;

    private int minDays, maxDays;

    private VipTypeDiy(VipType value, int minDays, int maxDays) {
        this.value = value;
        this.minDays = minDays;
        this.maxDays = maxDays;
    }

    public static VipTypeDiy getVipTypeDiy(int value) {
        for (VipTypeDiy temp : VipTypeDiy.values()) {
            if (temp.value.value == value) {
                return temp;
            }
        }

        return null;
    }

    public boolean isConstainTerm(int investTerm) {
        return investTerm >= minDays && investTerm <= maxDays;
    }

}