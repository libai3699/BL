package com.gp.common.base.context;

/**
 * 结果码类型，SUCCESS-0,BIZ_ERROR-1,SYS_ERROR-2,THIRD_ERROR-3,SYS_EXCP-4
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
public class ResultCodeType {

    /**成功*/
    public static final String SUCCESS = "0";

    /**业务异常*/
    public static final String BIZ_ERROR = "1";

    /**系统异常*/
    public static final String SYS_ERROR = "2";

    /**第三方异常*/
    public static final String THIRD_ERROR = "3";

    /**未知异常*/
    public static final String SYS_EXCP = "4";

}
