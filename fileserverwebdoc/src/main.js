import Vue from "vue";
import { Layout,Menu,Icon,Button,List,Row,Col,Checkbox,Breadcrumb,Popover,Modal,Input,Spin,Tree,Message,Divider } from 'ant-design-vue';
import App from "./App";
import axios from 'axios';
import router from './router';
Vue.use(Layout);
Vue.use(Menu);
Vue.use(Icon);
Vue.use(Button);
Vue.use(List);
Vue.use(Row);
Vue.use(Col);
Vue.use(Checkbox);
Vue.use(Breadcrumb);
Vue.use(Popover);
Vue.use(Modal);
Vue.use(Input);
Vue.use(Spin);
Vue.use(Tree);
Vue.use(Divider);
axios.defaults.withCredentials=true;
Vue.prototype.axios = axios;
Vue.prototype.$message = Message;
Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
router.beforeEach(
  (to, from, next) => {
    /* 路由发生变化修改页面title */
    if (to.meta.title) {
      document.title = to.meta.title
    }
    next()
  }
)