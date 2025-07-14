export default [
  {
    path: '/',
    redirect: '/chart/add',
  },
  {
    path: '/user',
    layout: false,
    routes: [
      {
        name: '登录',
        path: '/user/login',
        component: 'User/Login',
      },
      {
        name: '注册',
        path: '/user/register',
        component: 'User/Register',
      },
    ],
  },
  {
    name: '用户信息',
    path: '/user/info',
    component: 'User/Info'
  },
  {
    path: '/chart',
    icon: 'barChart',
    name: '图表页面',
    routes: [
      {
        path: '/chart/add',
        name: '实时智能分析(请勿退出)',
        icon: 'barChart',
        component: 'Chart/AddChart',
      },
      {
        path: '/chart/add/async',
        name: '智能分析（后台运行）',
        icon: 'barChart',
        component: 'Chart/AddChartSync',
      },
      {
        path: '/chart/add/async/mq',
        name: '智能分析（后台排队运行）',
        icon: 'barChart',
        component: 'Chart/AddChartSyncMq',
      },
      {
        path: '/chart/list',
        name: '我的图表',
        icon: 'pieChart',
        component: 'Chart/ChartList',
      },
    ],
  },
  {
    path: '/admin',
    name: '管理页面',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {
        name: '用户管理',
        path: '/admin/user',
        component: 'Admin/User',
      },
      {
        name: '图表管理',
        path: '/admin/chart',
        component: 'Admin/Chart',
      },
      {
        name: '所有图表',
        path: '/admin/chartList',
        component: 'Admin/ChartList',
      },
    ],
  },
  {
    path: '*',
    layout: false,
    component: './404',
  },
];
