package anzihe.com.common_template.service;

import anzihe.com.common_template.model.DTO.user.UserQueryRequest;
import anzihe.com.common_template.model.VO.LoginUserVO;
import anzihe.com.common_template.model.VO.UserVO;
import anzihe.com.common_template.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService extends IService<User> {

    long register(String userAccount, String password, String checkPassword);

    LoginUserVO userLogin(String userAccount, String password, HttpServletRequest request);

    LoginUserVO currentUser(HttpServletRequest request);

    boolean loginOutUser(HttpServletRequest request);

    String getEncryptPassword(String text);

    LoginUserVO toLoginUserVO(User user);

    UserVO toUserVO(User user);

    List<UserVO> toListUserVO(List<User> userList);

    QueryWrapper<User> getUserQueryWrapper(UserQueryRequest queryRequest);

    boolean isAdmin(LoginUserVO userVO);
}
