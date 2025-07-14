package com.chenxiaofeng.aibi.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息更新请求
 *
 * @author 尘小风
 */
@Data
public class UserInfoUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    @Serial
    private static final long serialVersionUID = 1L;
}