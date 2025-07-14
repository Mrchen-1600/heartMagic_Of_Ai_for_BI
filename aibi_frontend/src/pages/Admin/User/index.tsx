import { USER_ROLE } from '@/constants/user/userRole';
import CreateUserModal from '@/pages/Admin/User/components/CreateUserModal';
import UpdateUserModal from '@/pages/Admin/User/components/UpdateUserModal';
import { deleteUserUsingPost, listUserByPageUsingPost } from '@/services/aibi/userController';
import { ActionType, PageContainer, ProColumns, ProTable } from '@ant-design/pro-components';
import { Button, Divider, message, Popconfirm, Space, Typography } from 'antd';
import React, { useRef, useState } from 'react';

/**
 * 用户管理页面
 * @constructor
 */
const AdminUserPage: React.FC<unknown> = () => {
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const [updateData, setUpdateData] = useState<API.User>({});
  const actionRef = useRef<ActionType>();
  const [loading, setLoading] = useState<boolean>(false);

  /**
   *  删除节点
   * @param selectedRows
   */
  const doDelete = async (selectedRows: API.User[]) => {
    const hide = message.loading('正在删除');
    if (!selectedRows) return true;
    try {
      await deleteUserUsingPost({
        id: selectedRows.find((row) => row.id)?.id || 0,
      });
      message.success('操作成功');
      actionRef.current?.reload();
    } catch (e: any) {
      message.error('操作失败，' + e.message);
    } finally {
      hide();
    }
  };

  /**
   * 表格列配置
   */
  const columns: ProColumns<API.User>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'text',
    },
    {
      title: '用户昵称',
      dataIndex: 'userName',
      valueType: 'text',
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '账号',
      dataIndex: 'userAccount',
      valueType: 'text',
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '密码',
      dataIndex: 'userPassword',
      valueType: 'text',
      hideInTable: true,
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '用户头像',
      dataIndex: 'userAvatar',
      valueType: 'image',
    },
    {
      title: '用户角色',
      dataIndex: 'userRole',
      valueType: 'text',
      valueEnum: USER_ROLE,
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <Space split={<Divider type="vertical" />}>
          <Typography.Link
            onClick={() => {
              setUpdateData(record);
              setUpdateModalVisible(true);
            }}
          >
            修改
          </Typography.Link>
          <Popconfirm
            title="您确定要删除么？"
            onConfirm={() => doDelete([record])}
            okText="确认"
            cancelText="取消"
          >
            <Typography.Link type="danger">删除</Typography.Link>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.User>
        headerTitle="用户管理"
        actionRef={actionRef}
        loading={loading}
        rowKey="id"
        search={{
          labelWidth: 'auto',
        }}
        toolBarRender={() => [
          <Button key="1" type="primary" onClick={() => setCreateModalVisible(true)}>
            新建
          </Button>,
        ]}
        request={async (params) => {
          setLoading(true);
          const searchParams: API.UserQueryRequest = {
            ...params,
            sortField: 'createTime',
            sortOrder: 'desc',
          };
          const { data, code } = await listUserByPageUsingPost(searchParams);
          setLoading(false);
          return {
            data: data?.records || [],
            success: code === 0,
            total: data?.total,
          } as any;
        }}
        columns={columns}
      />
      <CreateUserModal
        modalVisible={createModalVisible}
        columns={columns}
        onSubmit={() => {
          // setCreateModalVisible(false);
          // actionRef.current?.reload();
        }}
        onCancel={() => {
          setCreateModalVisible(false);
        }}
      />
      <UpdateUserModal
        oldData={updateData}
        modalVisible={updateModalVisible}
        columns={columns}
        onSubmit={() => {}}
        onCancel={() => setUpdateModalVisible(false)}
      />
    </PageContainer>
  );
};

export default AdminUserPage;
