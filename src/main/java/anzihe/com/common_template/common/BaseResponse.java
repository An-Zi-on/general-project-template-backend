package anzihe.com.common_template.common;

import anzihe.com.common_template.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private T data;

    private int code;

    private String message;

    public BaseResponse(int code, String message, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public BaseResponse(ErrorCode errorCode, T data) {
        this(errorCode.getCode(), errorCode.getMessage(), data);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode, null);
    }

}
