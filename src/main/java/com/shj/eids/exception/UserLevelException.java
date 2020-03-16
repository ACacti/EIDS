package com.shj.eids.exception;

/**
 * @ClassName: UserLevelException
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-16 22:02
 **/
public class UserLevelException extends Exception {
    public UserLevelException() {
        super();
    }
    public UserLevelException(String msg){
        super(msg);
    }
}