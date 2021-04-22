import { defineConfig } from 'umi';
export default defineConfig({
  nodeModulesTransform: {
    type: 'none',
  },
  routes: [
    {
      path: '/',
      component: '@/pages/index',
    },
  ],
  fastRefresh: {},
  request: {
    dataField: 'data',
  },
  proxy: {
    '/api': {
      target: 'http://localhost:8088/',
      changeOrigin: true,
      pathRewrite: {
        '^/api': '',
      },
    },
  },
});
