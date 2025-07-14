import Footer from '@/components/Footer';
import { getLoginUserUsingGet } from '@/services/aibi/userController';
import { LinkOutlined } from '@ant-design/icons';
import { SettingDrawer } from '@ant-design/pro-components';
import type { RunTimeLayoutConfig } from '@umijs/max';
import { history, Link } from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import { AvatarDropdown, AvatarName } from './components/RightContent/AvatarDropdown';
import { errorConfig } from './requestConfig';
import { ReactElement, JSXElementConstructor, ReactNode, ReactPortal } from 'react';

const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<InitialState> {
  const defaultState: InitialState = {
    loginUser: undefined,
  };
  try {
    // 获取当前登录用户
    const res = await getLoginUserUsingGet();
    defaultState.loginUser = res.data;
  } catch (error) {
    history.push(loginPath);
  }
  return defaultState;
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout

export const layout: RunTimeLayoutConfig = ({initialState, setInitialState}) => {
  return {
    //actionsRender: () => [<Question key="doc" />],
    avatarProps: {
      src: initialState?.loginUser?.userAvatar,
      title: <AvatarName/>,
      render: (_: any, avatarChildren: string | number | boolean | ReactElement<any, string | JSXElementConstructor<any>> | Iterable<ReactNode> | ReactPortal | null | undefined) => {
        return <AvatarDropdown menu={true}>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.loginUser?.userName,
    },
    footerRender: () => <Footer />,
    onPageChange: () => {
      const { location } = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.loginUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    links: isDev
      ? [
        <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
          <LinkOutlined />
          <span>OpenAPI 文档</span>
        </Link>,
      ]
      : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children: any) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          <SettingDrawer
            disableUrlParams
            enableDarkTheme
            //settings={undefined}
            onSettingChange={(settings) => {
              setInitialState((preInitialState: any) => ({
                ...preInitialState,
                settings,
              }));
            }}
          />
        </>
      );
    },
    ...defaultSettings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  baseURL: isDev ? "http://localhost:8108" : "http://114.55.249.21:8108",
  withCredentials: true,
  timeout: 300000,
  ...errorConfig,
};
