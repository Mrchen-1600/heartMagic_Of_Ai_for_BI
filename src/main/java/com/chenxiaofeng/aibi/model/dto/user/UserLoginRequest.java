package com.chenxiaofeng.aibi.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户登录请求
 *
 * @author 尘小风
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
