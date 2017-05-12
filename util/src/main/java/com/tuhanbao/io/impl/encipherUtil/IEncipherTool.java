package com.tuhanbao.io.impl.encipherUtil;

public interface IEncipherTool
{
    public byte[] encrypt(byte[] bytes);
    
    public byte[] decrypt(byte[] bytes);
}
