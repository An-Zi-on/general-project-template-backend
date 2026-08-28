package anzihe.com.common_template.controller;

import anzihe.com.common_template.annotation.authCheck;
import anzihe.com.common_template.common.BaseResponse;
import anzihe.com.common_template.common.DeleteRequest;
import anzihe.com.common_template.common.ResultUtils;
import anzihe.com.common_template.common.UserConstant;
import anzihe.com.common_template.exception.ErrorCode;
import anzihe.com.common_template.exception.ThrowUtils;
import anzihe.com.common_template.model.DTO.user.UserAddRequest;
import anzihe.com.common_template.model.DTO.user.UserLoginRequest;
import anzihe.com.common_template.model.DTO.user.UserQueryRequest;
import anzihe.com.common_template.model.DTO.user.UserRegisterRequest;
import anzihe.com.common_template.model.DTO.user.UserUpdateRequest;
import anzihe.com.common_template.model.VO.LoginUserVO;
import anzihe.com.common_template.model.VO.UserVO;
import anzihe.com.common_template.model.entity.User;
import anzihe.com.common_template.service.UserService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<?> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        userService.register(userAccount, password, checkPassword);
        return ResultUtils.success("");
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, password, request);
        // todo 修改 登录逻辑
        //StpLogic space = StpKit.SPACE;
        //space.login(loginUserVO.getId());
        //space.getSession().set(UserConstant.USER_LOGIN, loginUserVO);
        return ResultUtils.success(loginUserVO);
    }

    @GetMapping("/current")
    public BaseResponse<LoginUserVO> currentUser(HttpServletRequest request) {
        LoginUserVO loginUserVO = userService.currentUser(request);
        return ResultUtils.success(loginUserVO);
    }

    @GetMapping("/loginOut")
    public BaseResponse<?> loginOut(HttpServletRequest request) {
        boolean result = userService.loginOutUser(request);
        return ResultUtils.success(result);
    }

    @PostMapping("/add")
    @authCheck(mustRole = UserConstant.USER_ADMIN)
    public BaseResponse<?> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwException(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        user.setUserPassword(userService.getEncryptPassword(UserConstant.DEFAULT_PASSWORD));
        user.setUserRole(UserConstant.USER);
        boolean save = userService.save(user);
        ThrowUtils.throwException(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(save);
    }

    @PostMapping("/delete")
    @authCheck(mustRole = UserConstant.USER_ADMIN)
    public BaseResponse<?> delete(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwException(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        boolean result = userService.removeById(id);
        ThrowUtils.throwException(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @authCheck(mustRole = UserConstant.USER_ADMIN)
    public BaseResponse<?> update(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwException(userUpdateRequest == null || userUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwException(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    @PostMapping("/list/page/vo")
    @authCheck(mustRole = UserConstant.USER_ADMIN)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwException(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getUserQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> listUserVO = userService.toListUserVO(userPage.getRecords());
        userVOPage.setRecords(listUserVO);
        return ResultUtils.success(userVOPage);
    }
}
