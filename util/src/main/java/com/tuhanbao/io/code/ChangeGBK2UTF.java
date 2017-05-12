package com.tuhanbao.io.code;

import java.io.File;
import java.io.IOException;

import com.tuhanbao.io.txt.util.TxtUtil;

/**
 * 用于转换代码编码格式用
 * @author tuhanbao
 *
 */
public class ChangeGBK2UTF
{
    public static void main(String args[]) throws IOException
    {
        String url = "D:/work/code2/server/ioutil";
        change(new File(url));
    }
    public static void change(File f) throws IOException
    {
        if (f.isDirectory())
        {
            for (File t : f.listFiles())
            {
                change(t);
            }
        }
        else if (f.isFile())
        {
            if (isJavaFile(f.getName()))
            {
                String url = f.getPath();
                TxtUtil.write(url, TxtUtil.read(url, "GBK"), "UTF-8", false);
            }
        }
        else
        {
            //...
        }
        
    }
    
    private static boolean isJavaFile(String name)
    {
        return name.endsWith(".java");
    }
}
