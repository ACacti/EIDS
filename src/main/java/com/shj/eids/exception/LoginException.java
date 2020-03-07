package com.shj.eids.exception;

import jdk.tools.jlink.internal.packager.AppRuntimeImageBuilder;
import sun.rmi.runtime.Log;

/**
 * @ClassName: LoginException
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 10:08
 **/
public class LoginException extends Exception{
    public LoginException(){
        super();
    }
    public LoginException(String msg){
        super(msg);
    }
}