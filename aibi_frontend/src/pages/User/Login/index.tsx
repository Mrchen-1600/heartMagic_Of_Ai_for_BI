
import Footer from '@/components/Footer';
import { userLoginUsingPost } from '@/services/aibi/userController';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormText } from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Link, useSearchParams } from '@@/exports';
import { Helmet, history, useModel } from '@umijs/max';
import { message, Tabs } from 'antd';
import React from 'react';
import { flushSync } from 'react-dom';
import Settings from '../../../../config/defaultSettings';

const UserLoginPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const { initialState, setInitialState } = useModel('@@initialState');
  const containerClassName = useEmotionCss(() => {
    return {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    };
  });

  /**
   * 用户登录
   * @param fields
   */
  const doUserLogin = async (fields: API.UserLoginRequest) => {
    const hide = message.loading('登录中');
    try {
      const res = await userLoginUsingPost({ ...fields });
      message.success('登录成功');
      flushSync(() => {
        setInitialState({
          ...initialState,
          loginUser: res.data,
        } as InitialState);
      });
      // 重定向到之前页面
      history.push(searchParams.get('redirect') ?? '/');
    } catch (e: any) {
      message.error(e.message);
    } finally {
      hide();
    }
  };

  return (
    <div className={containerClassName}>
      <Helmet>
        <title>
          {'登录'}- {Settings.title}
        </title>
      </Helmet>
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={<img alt="logo" src="/logo.svg" />}
          title="心灵魔方智能 BI"
          subTitle="心灵魔方智能 BI"
          onFinish={async (values) => {
            await doUserLogin(values as API.UserLoginRequest);
          }}
        >
          <Tabs
            centered
            items={[
              {
                key: 'account',
                label: '账号密码登录',
              },
            ]}
          />

          <>
            <ProFormText
              name="userAccount"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined />,
              }}
              placeholder={'请输入用户名'}
              rules={[
                {
                  required: true,
                  message: '请输入用户名！',
                },
              ]}
            />
            <ProFormText.Password
              name="userPassword"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined />,
              }}
              placeholder={'请输入密码'}
              rules={[
                {
                  required: true,
                  message: '请输入密码！',
                },
              ]}
            />
          </>

          <div
            style={{
              marginBottom: 24,
            }}
          >
            <Link to="/user/register">新用户注册</Link>
            <Link
              to="/"
              style={{
                float: 'right',
              }}
            >
              返回主页
            </Link>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default UserLoginPage;
