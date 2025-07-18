# 建表脚本
# @author 尘小风

-- 创建库
create database if not exists aibi;

-- 切换库
use aibi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
    ) comment '用户' collate = utf8mb4_unicode_ci;

-- 图表表
create table if not exists chart
(
    id           bigint          auto_increment comment 'id' primary key,
    goal		 text            null comment '分析目标',
    chartData    text            null comment '图表数据',
    chartType	 varchar(128)    null comment '图表类型',
    genChart	 text	         null comment '生成的图表数据',
    genResult	 text	         null comment '生成的分析结论',
    userId       bigint          null comment '创建用户 id',
    createTime   datetime        default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime        default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint         default 0   not null comment '是否删除',
    chartName    varchar(128)    null comment '图表名称',
    genStatus    tinyint(1)      default 0   not null comment '生成状态[0:等待1:运行中2:失败3:成功]',
    execMessage  text             null comment '执行信息',
    index idx_chartName (chartName)
) comment '图表信息表' collate = utf8mb4_unicode_ci;
