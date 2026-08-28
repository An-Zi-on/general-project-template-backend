package anzihe.com.common_template.model.DTO.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;

    private String userPassword;
}
