package com.tuhanbao.io.impl.encipherUtil;

import java.util.HashMap;
import java.util.Map;

import com.tuhanbao.io.objutil.ByteUtil;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.config.BaseConfigConstants;
import com.tuhanbao.util.config.ConfigManager;

/**
 * 用来加解密字节
 * @author tuhanbao
 *
 */
public class Encipher 
{
    public static IEncipherTool TCP_TOOL = null;
    public static IEncipherTool HTTP_TOOL = null;
    
    private static Map<EncipherType, IEncipherTool> TOOLS = new HashMap<EncipherType, IEncipherTool>();
    
    
    static
    {
        register(EncipherType.SELF, SelfEncipherTool.INSTANCE);
        register(EncipherType.DES, SelfEncipherTool.INSTANCE);
        register(EncipherType.AES, SelfEncipherTool.INSTANCE);
        
        //初始化加解密
        String httpEnclipherType = ConfigManager.getBaseConfig().getString(BaseConfigConstants.HTTP_ENCLIPHER);
        String tcpEnclipherType = ConfigManager.getBaseConfig().getString(BaseConfigConstants.TCP_ENCLIPHER);
        if (!StringUtil.isEmpty(httpEnclipherType)) {
            HTTP_TOOL = getToolByType(EncipherType.valueOf(httpEnclipherType));
        }
        if (!StringUtil.isEmpty(tcpEnclipherType)) {
            TCP_TOOL = getToolByType(EncipherType.valueOf(tcpEnclipherType));
        }
    }
    
    public static byte[] encrypt(EncipherType type, byte[] bytes) {
        IEncipherTool tool = getToolByType(type);
        if (tool == null) return bytes;
        else return tool.encrypt(bytes);
    }
    
    public static byte[] decrypt(EncipherType type, byte[] bytes) {
        IEncipherTool tool = getToolByType(type);
        if (tool == null) return bytes;
        else return tool.decrypt(bytes);
    }
    
    public static String encrypt(EncipherType type, String str) {
        return ByteUtil.bytes2String(encrypt(type, ByteUtil.string2Bytes(str)));
    }
    
    public static String decrypt(EncipherType type, String str) {
        return ByteUtil.bytes2String(decrypt(type, ByteUtil.string2Bytes(str)));
    }
    
    public static byte[] encryptTcp(byte[] bytes)
    {
        if (TCP_TOOL == null) return bytes;
        else return TCP_TOOL.encrypt(bytes);
    }
    
    public static byte[] decryptTcp(byte[] bytes)
    {
        if (TCP_TOOL == null) return bytes;
        else return TCP_TOOL.decrypt(bytes);
    }
    
    public static byte[] encryptHttp(String str)
    {
        if (HTTP_TOOL == null) return ByteUtil.string2Bytes(str);
        else return HTTP_TOOL.encrypt(ByteUtil.string2Bytes(str));
    }
    
    public static String decryptHttp(String str)
    {
        if (HTTP_TOOL == null) return str;
        else return ByteUtil.bytes2String(HTTP_TOOL.decrypt(ByteUtil.string2Bytes(str)));
    }
    
    private static IEncipherTool getToolByType(EncipherType type)
    {
        return TOOLS.get(type);
    }
    
    public static void register(EncipherType type, IEncipherTool tool)
    {
        TOOLS.put(type, tool);
    }
}
