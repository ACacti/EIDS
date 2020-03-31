package com.shj.eids.utils;

import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @ClassName: FileDownloadUtil
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-31 19:04
 **/
public class FileDownloadUtil {
    public static void doDownload(HttpServletRequest request, HttpServletResponse response, File file) {
        ServletContext context = request.getServletContext();
        String name = file.getName();
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(file);
            response.setHeader("content-type", context.getMimeType(name));
            response.setHeader("content-disposition", "attachment;filename="+ name);
            ServletOutputStream sos = response.getOutputStream();
            byte[] buff = new byte[1024*8];
            int len = fileInputStream.read(buff);
            while(len != -1){
                sos.write(buff, 0, len);
                len = fileInputStream.read(buff);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}