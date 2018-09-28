package com.pagoda.account.migrate.producerconsumer.base.normal;
/**
 * @author YSK
 * 响应码枚举，参考HTTP状态码的语义
 */
public enum ResultCode {
    // 系统级别错误码
    SUCCESS(200,"OK"),
    //FAIL(-99999, "发生异常!"),
    FAIL(-99999, "发生异常!"),
    EMPTY(-1,"数据为空！");


    private int code ;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 枚举类型的判断和获取
     * @param code 错误码
     * @return 返回错误码对应的枚举信息
     */
    public static ResultCode statusOf(int code){
        for(ResultCode error : values()){
            if(error.getCode() == code){
                return error;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getSCode() {
        return String.valueOf(code);
    }

    public String getMsg() {
        return msg;
    }

}
