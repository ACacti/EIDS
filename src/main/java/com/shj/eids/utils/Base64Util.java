package com.shj.eids.utils;

import java.io.*;
import java.util.Base64;

/**
 * @ClassName: Base64Encode
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 11:37
 **/
public class Base64Util {
    public static String encode(File file) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] b = new byte[bufferedInputStream.available()];
        bufferedInputStream.read(b);
        bufferedInputStream.close();
        return new String(Base64.getEncoder().encode(b));
    }

    public static void decode(String code, String path) throws IOException {
        byte[] b = Base64.getDecoder().decode(code);
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(path));
        os.write(b);
        os.close();
    }
}