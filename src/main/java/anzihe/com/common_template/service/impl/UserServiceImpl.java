package anzihe.com.common_template.service.impl;

import anzihe.com.common_template.common.UserConstant;
import anzihe.com.common_template.exception.ErrorCode;
import anzihe.com.common_template.exception.ThrowUtils;
import anzihe.com.common_template.mapper.UserMapper;
import anzihe.com.common_template.model.DTO.user.UserQueryRequest;
import anzihe.com.common_template.model.VO.LoginUserVO;
import anzihe.com.common_template.model.VO.UserVO;
import anzihe.com.common_template.model.entity.User;
import anzihe.com.common_template.model.enums.UserRoleEnum;
import anzihe.com.common_template.service.UserService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static anzihe.com.common_template.common.UserConstant.USER_LOGIN;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long register(String userAccount, String password, String checkPassword) {
        ThrowUtils.throwException(userAccount.length() < 8, ErrorCode.PARAMS_ERROR, "账号长度大于8位");
        ThrowUtils.throwException(password.length() < 8 || checkPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度大于8位");
        ThrowUtils.throwException(!password.equals(checkPassword), ErrorCode.PARAMS_ERROR, "输入密码不一致");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        ThrowUtils.throwException(count > 0, ErrorCode.PARAMS_ERROR, "账号已存在");
        String encryptPassword = getEncryptPassword(password);
        User updateUser = new User();
        updateUser.setUserAccount(userAccount);
        updateUser.setUserPassword(encryptPassword);
        updateUser.setUserName("无名");
        updateUser.setUserRole(UserRoleEnum.USER.getValue());
        long result = userMapper.insert(updateUser);
        ThrowUtils.throwException(result <= 0, ErrorCode.SYSTEM_ERROR);
        return result;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String password, HttpServletRequest request) {
        ThrowUtils.throwException(userAccount.length() < 8 || StrUtil.isEmptyIfStr(userAccount), ErrorCode.PARAMS_ERROR);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User selectUser = userMapper.selectOne(queryWrapper);
        ThrowUtils.throwException(selectUser == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        String userPassword = selectUser.getUserPassword();
        ThrowUtils.throwException(!userPassword.equals(getEncryptPassword(password)), ErrorCode.PARAMS_ERROR, "密码错误");
        LoginUserVO loginUserVO = toLoginUserVO(selectUser);
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute(USER_LOGIN);
        ThrowUtils.throwException(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        User selectUser = userMapper.selectById(loginUser.getId());
        LoginUserVO loginUserVO = toLoginUserVO(selectUser);
        session.setAttribute(USER_LOGIN, loginUserVO);
        return loginUserVO;
    }

    @Override
    public boolean loginOutUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        LoginUserVO currentUser = (LoginUserVO) session.getAttribute(USER_LOGIN);
        ThrowUtils.throwException(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        session.removeAttribute(USER_LOGIN);
        return true;
    }

    @Override
    public String getEncryptPassword(String text) {
        return SecureUtil.md5(text + UserConstant.USER_PASSWORD_SALT);
    }

    @Override
    public LoginUserVO toLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO bean = BeanUtil.toBean(user, LoginUserVO.class);
        bean.setCreateTime(LocalDateTimeUtil.of(user.getCreateTime()));
        bean.setUpdateTime(LocalDateTimeUtil.of(user.getUpdateTime()));
        return bean;
    }

    @Override
    public UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        return BeanUtil.toBean(user, UserVO.class);
    }

    @Override
    public List<UserVO> toListUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::toUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getUserQueryWrapper(UserQueryRequest queryRequest) {
        ThrowUtils.throwException(queryRequest == null, ErrorCode.OPERATION_ERROR, "查询条件为空");
        Long id = queryRequest.getId();
        String userName = queryRequest.getUserName();
        String userAccount = queryRequest.getUserAccount();
        String userProfile = queryRequest.getUserProfile();
        String userRole = queryRequest.getUserRole();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(LoginUserVO userVO) {
        return userVO != null && UserRoleEnum.ADMIN.equals(UserRoleEnum.getEnumByValue(userVO.getUserRole()));
    }
}
