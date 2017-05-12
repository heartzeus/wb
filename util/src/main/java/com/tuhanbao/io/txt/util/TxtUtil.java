package com.tuhanbao.io.txt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.tuhanbao.io.IOUtil;
import com.tuhanbao.io.objutil.FileUtil;

public class TxtUtil
{
    public static String read(String url) throws IOException
    {
        return read(url, IOUtil.DEFAULT_CHARSET);
    }
    
    public static String read(File f) throws IOException
    {
        return read(f, IOUtil.DEFAULT_CHARSET);
    }
    
    public static String read(String url, String charset) throws IOException
    {
        return read(new File(url), charset);
    }
    
    public static String read(File f, String charset) throws IOException
    {
        FileInputStream fi = null;
        FileChannel channel = null;
        
        byte[] total = new byte[0];
        try
        {
            if (f == null || !f.exists())
            {
                return null;
            }
            
            fi = new FileInputStream(f);
            channel = fi.getChannel();
            ByteBuffer dst = ByteBuffer.allocate(1000);
            int length;
            while ((length = channel.read(dst)) != -1)
            {
                byte[] temp = total;
                total = new byte[temp.length + length];
                System.arraycopy(temp, 0, total, 0, temp.length);
                System.arraycopy(dst.array(), 0, total, temp.length, length);
                dst.clear();
            }
        }
        finally
        {
            IOUtil.close(fi);
            IOUtil.close(channel);
        }
        return new String(total, charset);
    }
    
    public static void write(String url, String s) throws IOException
    {
        write(url, s, IOUtil.DEFAULT_CHARSET, false);
    }
    
    public static void writeWithBom(String url, String s) throws IOException
    {
        write(url, s, IOUtil.DEFAULT_CHARSET, false, true);
    }
    
    public static void write(String url, String s, boolean append) throws IOException
    {
        write(url, s, IOUtil.DEFAULT_CHARSET, append);
    }
    
    public static void write(String url, String s, String charset, boolean append) throws IOException
    {
    	write(url, s, charset, append, false);
    }
    
    public static void write(String url, String s, String charset, boolean append, boolean withBom) throws IOException
    {
        FileOutputStream fo = null;
        FileChannel channel = null;
        
        try
        {
            File f = new File(url);
            if (!f.exists())
            {
            	FileUtil.createFile(f);
            }
            
            fo = new FileOutputStream(f, append);
            channel = fo.getChannel();
            byte[] bytes = s.getBytes(charset);
			if (withBom)
            {
				byte[] bytesWithBom = new byte[bytes.length + 3];
				System.arraycopy(bytes, 0, bytesWithBom, 3, bytes.length);
				bytesWithBom[0] = (byte)0xEF;
				bytesWithBom[1] = (byte)0xBB;
				bytesWithBom[2] = (byte)0xBF;
            	channel.write(ByteBuffer.wrap(bytesWithBom));
            }
            else
            {
            	channel.write(ByteBuffer.wrap(bytes));
            }
        }
        finally
        {
            IOUtil.close(fo);
            IOUtil.close(channel);
        }
    }
}
