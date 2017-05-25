package com.hhnz.api.cfcrm.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tuhanbao.util.exception.MyException;

public class UploadUtil {

    public static InputStream importExcel(HttpServletRequest request) {
        MultipartHttpServletRequest mhs = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> mapFile = mhs.getFileMap();
        if (mapFile.isEmpty()) {
            return null;
        }
        try {
            return mapFile.get(0).getInputStream();
        }
        catch (IOException e) {
            throw MyException.getMyException(e);
        }
    }
    
}
