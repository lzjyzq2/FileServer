import Vue from 'vue';
import Router from 'vue-router';
import UploadPanel from './views/UploadPanel';
import Manage from './views/Manage';
import About from './views/About';
import Help from './views/Help';
import Settings from './views/Settings';
Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'uploadpanel',
      component: UploadPanel,
      meta: {
        title: '文件上传'
      }
    },
    {
      path: '/manage',
      name: 'manage',
      component: Manage,//component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
      meta: {
        title: '文件管理'
      }
    },
    {
      path: '/settings',
      name:'settings',
      component:Settings,
      meta:{
        title:'设置'
      }
    },
    {
      path: '/about',
      name: 'about',
      component: About,
      meta: {
        title: '关于'
      }
    },
    {
      path: '/help',
      name: 'help',
      component: Help,
      meta: {
        title: '使用帮助'
      }
    }
  ]
})
