package com.tuhanbao.io.excel.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.tuhanbao.io.IOUtil;

public class CsvUtil {
    public static List<String> read(String url)
    {
        return read(url, IOUtil.DEFAULT_CHARSET);
    }
    
    public static List<String> read(String url, String charset)
    {
		CsvReader reader = null;
        List<String> result = new ArrayList<String>();
        try
        {
        	reader = new CsvReader(new InputStreamReader(
    				new FileInputStream(url), charset));
        	//大记录时需要打开
        	reader.setSafetySwitch(false);
        	while (reader.readRecord()) {
        		result.add(reader.getRawRecord());
        	}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	if (reader != null) reader.close();
        }

        return result;
    }
}
