package com.tuhanbao.io.impl;


/**
 * 对象的拥有类型
 * @author tuhanbao
 *
 */
public enum ObjectWriteType
{
    //服务端仅有
    SERVER(0),
    
    //客户端仅有
    CLIENT(1),
    
    //公用
    ALL(2);
    
    private int value;
    
    private ObjectWriteType(int value)
    {
        this.value = value;
    }
    
    public static ObjectWriteType getObjectWriteType(int value)
    {
        for (ObjectWriteType temp : ObjectWriteType.values())
        {
            if (temp.value == value)
            {
                return temp;
            }
        }

        return null;
    }
}
