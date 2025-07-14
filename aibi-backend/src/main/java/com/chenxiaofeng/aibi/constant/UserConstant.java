package com.chenxiaofeng.aibi.constant;

/**
 * 用户常量
 * @author 尘小风
 */
public interface UserConstant {

    /**
     * 盐值，混淆密码
     */
    String SALT = "chenxiaofeng";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    /**
     * 默认头像
     */
    String DEFAULT_USER_AVATAR = "https://images.pexels.com/photos/31275701/pexels-photo-31275701.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2";

    /**
     * 默认用户名
     */
    String DEFAULT_USERNAME = "匿名用户";

    // endregion
}
