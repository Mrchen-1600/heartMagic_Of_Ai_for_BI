import MyDivider from '@/components/MyDivider';
import UpdateUserInfoModal from '@/pages/User/Info/components/UpdateUserInfoModal';
import { logoutAccountUsingGet } from '@/services/aibi/userController';
import { useModel } from '@@/exports';
import { EditOutlined } from '@ant-design/icons';
import { PageContainer, ProColumns } from '@ant-design/pro-components';
import { Avatar, Button, Card, Col, Descriptions, Divider, message, Row } from 'antd';
import Meta from 'antd/es/card/Meta';
import React, { useState } from 'react';

const tabListNoTitle = [
  {
    key: 'userInfo',
    label: '个人资料',
  },
];

const UserInfoPage: React.FC = () => {
  const [activeTabKey, setActiveTabKey] = useState<string>('userInfo');
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  const [updateData, setUpdateData] = useState<API.User>({});
  const { initialState } = useModel('@@initialState');
  const { loginUser } = initialState || {};

  const onTab2Change = (key: string) => {
    setActiveTabKey(key);
  };

  /**
   * 注销账号
   * @param userId
   */
  const doLogoutAccount = async (userId: number) => {
    const hide = message.loading('正在注销');
    if (!userId) return true;
    try {
      await logoutAccountUsingGet({
        userId: userId ?? 0,
      });
      message.success('操作成功');
      // 跳转至登录页
      // window.location.href = '/user/login';
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
      valueType: 'index',
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
      title: '用户简介',
      dataIndex: 'userProfile',
      valueType: 'text',
    },
    {
      title: '用户头像',
      dataIndex: 'userAvatar',
      valueType: 'image',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
  ];

  const contentListNoTitle: Record<string, React.ReactNode> = {
    userInfo: (
      <>
        <div className={'user-detail-card'}>
          <Descriptions
            title={'基本信息'}
            column={1}
            extra={
              <Button
                type={'link'}
                icon={<EditOutlined />}
                onClick={() => {
                  setUpdateData(loginUser as API.User);
                  setUpdateModalVisible(true);
                }}
              >
                编辑
              </Button>
              // <Link to={'/user/info/edit'}>
              // </Link>
            }
            labelStyle={{ color: 'black', marginRight: 20 }}
          >
            <Descriptions.Item label="性别">{'暂无'}</Descriptions.Item>
            <Descriptions.Item label="年龄">{'暂无'}</Descriptions.Item>
            <Descriptions.Item label="手机号">{'暂无'}</Descriptions.Item>
            <Descriptions.Item label="邮箱">{'暂无'}</Descriptions.Item>
            <Descriptions.Item label="地区">{'暂无'}</Descriptions.Item>
          </Descriptions>
          <Divider />

          <Descriptions
            title={'其他'}
            column={1}
            extra={
              <Button
                type={'link'}
                icon={<EditOutlined />}
                onClick={() => {
                  setUpdateData(loginUser as API.User);
                  setUpdateModalVisible(true);
                }}
              >
                编辑
              </Button>
            }
            labelStyle={{ color: 'black', marginRight: 20 }}
          >
            <Descriptions.Item label="id">{loginUser?.id}</Descriptions.Item>
            <Descriptions.Item label="注册时间">{loginUser?.createTime}</Descriptions.Item>
          </Descriptions>
        </div>
      </>
    ),
  };

  return (
    <PageContainer title>
      <Card>
        <Row justify={'space-between'}>
          <Meta
            title={<h3>{loginUser?.userName}</h3>}
            description={
              <>
                <div>简介：{loginUser?.userProfile}</div>
              </>
            }
            avatar={
              <Avatar
                style={{ width: 96, height: 96, lineHeight: 96, fontSize: 18 }}
                src={loginUser?.userAvatar}
                onError={() => true}
              />
            }
          />
          <Col span={3}>
            <Button
              type={'primary'}
              ghost
              onClick={() => {
                setUpdateData(loginUser as API.User);
                setUpdateModalVisible(true);
              }}
            >
              编辑个人资料
            </Button>
            <MyDivider />
            <Button
              danger
              onClick={() => {
                doLogoutAccount(loginUser?.id ?? 0);
              }}
            >
              注销账号
            </Button>
          </Col>
        </Row>
      </Card>
      <MyDivider />
      <Card tabList={tabListNoTitle} activeTabKey={activeTabKey} onTabChange={onTab2Change}>
        {contentListNoTitle[activeTabKey]}
      </Card>
      <UpdateUserInfoModal
        oldData={updateData}
        modalVisible={updateModalVisible}
        columns={columns}
        onSubmit={() => {}}
        onCancel={() => setUpdateModalVisible(false)}
      />
    </PageContainer>
  );
};
export default UserInfoPage;
