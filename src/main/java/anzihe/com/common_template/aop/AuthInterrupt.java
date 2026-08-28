package anzihe.com.common_template.aop;

import anzihe.com.common_template.annotation.authCheck;
import anzihe.com.common_template.exception.BusinessException;
import anzihe.com.common_template.exception.ErrorCode;
import anzihe.com.common_template.model.VO.LoginUserVO;
import anzihe.com.common_template.model.enums.UserRoleEnum;
import anzihe.com.common_template.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterrupt {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheckAnnotation)")
    public Object doInterrupt(ProceedingJoinPoint proceedingJoinPoint, authCheck authCheckAnnotation) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        LoginUserVO loginUserVO = userService.currentUser(request);
        UserRoleEnum currentUserRole = UserRoleEnum.getEnumByText(loginUserVO.getUserRole());
        UserRoleEnum needUserRole = UserRoleEnum.getEnumByText(authCheckAnnotation.mustRole());
        if (needUserRole == null) {
            return proceedingJoinPoint.proceed();
        }
        if (currentUserRole == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (UserRoleEnum.ADMIN.equals(needUserRole) && !needUserRole.equals(currentUserRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return proceedingJoinPoint.proceed();
    }
}
