package com.shj.eids.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * @ClassName: TransferImageUtil
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-16 16:34
 **/
public class TransferImageUtil {

    public static String transferImage(MultipartFile image, String basePath, String mid, String contextPath, String authorId) throws Exception{
        String ret = "";
        //生成uuid作为文件名称
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //获得文件类型，如果不是图片，禁止上传
        String contentType = image.getContentType();
        //获得文件的后缀名
        String suffixName = contentType.substring(contentType.indexOf("/")+1);
        //得到文件名
        String imageName = uuid+"."+suffixName;
        //获取文件夹路径
        String direPath = null;
        direPath = basePath + mid + authorId;
        File direFile = new File(direPath);
        //文件夹如果不存在，新建文件夹
        if(!direFile.exists() || !direFile.isDirectory()){
            direFile.mkdirs();
        }
        //得到文件路径
        String path = direPath+"/"+imageName;

        image.transferTo(new File(path));
        //返回给前台用于展示照片的链接地址
        ret= contextPath + "/" + mid + authorId + "/" +imageName;
        return ret;
    }
}