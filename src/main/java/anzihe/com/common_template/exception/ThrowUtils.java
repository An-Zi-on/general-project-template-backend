package anzihe.com.common_template.exception;

public class ThrowUtils {

    public static void throwException(boolean condition, BusinessException businessException) {
        if (condition) {
            throw businessException;
        }
    }

    public static void throwException(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new BusinessException(errorCode);
        }
    }

    public static void throwException(boolean condition, ErrorCode errorCode, String message) {
        if (condition) {
            throw new BusinessException(errorCode, message);
        }
    }
}
