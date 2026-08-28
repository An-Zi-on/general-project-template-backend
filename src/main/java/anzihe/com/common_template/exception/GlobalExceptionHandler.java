package anzihe.com.common_template.exception;

import anzihe.com.common_template.common.BaseResponse;
import anzihe.com.common_template.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException businessException) {
        log.error("BusinessException: code={}, message={}", businessException.getCode(), businessException.getMessage(), businessException);
        return ResultUtils.error(businessException.getCode(), businessException.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runTimeExceptionHandler(RuntimeException runtimeException) {
        log.error("未处理的运行时异常: {}", String.valueOf(runtimeException), runtimeException);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }
}
