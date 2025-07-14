import { defineConfig } from '@umijs/max';
import MonacoEditorWebpackPlugin from 'monaco-editor-webpack-plugin';
import routes from './config/routes';

export default defineConfig({
  antd: {},
  access: {},
  model: {},
  initialState: {},
  request: {},
  layout: {},
  esbuildMinifyIIFE: true,
  headScripts: [
    // 解决首次加载时白屏的问题
    {
      src: '/scripts/loading.js',
      async: true,
    },
  ],
  presets: [ 'umi-presets-pro' ],
  openAPI: [
    {
      requestLibPath: "import { request } from '@umijs/max'",
      // 或者使用在线的版本
      schemaPath: "http://localhost:8108/api/v2/api-docs",
      projectName: 'aibi',
      mock: false,
    },
  ],
  // @ts-ignore
  // mock: true,
  routes,
  npmClient: 'yarn',
  // https://umijs.org/zh-CN/guide/boost-compile-speed#monaco-editor-%E7%BC%96%E8%BE%91%E5%99%A8%E6%89%93%E5%8C%85
  // @ts-ignore
  chainWebpack: (memo: any) => {
    // 更多配置 https://github.com/Microsoft/monaco-editor-webpack-plugin#options
    memo.plugin('monaco-editor-webpack-plugin').use(MonacoEditorWebpackPlugin, [
      // 按需配置
      { languages: [ 'json', 'javascript' ] },
    ]);
    return memo;
  },
});
