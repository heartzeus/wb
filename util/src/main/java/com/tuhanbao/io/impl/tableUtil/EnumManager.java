package com.tuhanbao.io.impl.tableUtil;

import java.util.HashMap;
import java.util.Map;

import com.tuhanbao.io.impl.classUtil.EnumClassInfo;
import com.tuhanbao.io.impl.classUtil.EnumType;
import com.tuhanbao.io.impl.classUtil.IEnumType;

public class EnumManager {

	private static final Map<String, IEnumType> ENUMS = new HashMap<String, IEnumType>();
	
	
	static {
		//系统有一些初始化的枚举
		register("FlowStatus", new EnumType("FlowStatus", "com.tuhanbao.thirdapi.pay.FlowStatus", EnumClassInfo.INT));
		register("PayPlatform", new EnumType("PayPlatform", "com.tuhanbao.thirdapi.pay.PayPlatform", EnumClassInfo.INT));
		register("Operator", new EnumType("Operator", "com.tuhanbao.web.filter.operator.Operator", EnumClassInfo.INT));
		register("LogicType", new EnumType("LogicType", "com.tuhanbao.web.filter.LogicType", EnumClassInfo.INT));
	}
	
	public static void register(String key, IEnumType enumInfo) {
		ENUMS.put(key, enumInfo);
	}
	
	public static IEnumType getEnum(String key) {
		return ENUMS.get(key);
	}
}
