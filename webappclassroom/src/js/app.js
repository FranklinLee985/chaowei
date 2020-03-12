// Import Vue
import Vue from 'vue';

// Import Framework7
import Framework7 from 'framework7/framework7-lite.esm.bundle.js';

// Import Framework7-Vue Plugin
import Framework7Vue from 'framework7-vue/framework7-vue.esm.bundle.js';

// Import Framework7 Styles
import 'framework7/css/framework7.bundle.css';

// Import Icons and App Custom Styles
import '../css/icons.css';
import '../css/app.styl';

// Import App Component
import App from '../components/app.vue';

// Init Framework7-Vue Plugin
Framework7.use(Framework7Vue);


//jarong add
//import './locales/locales.js'               // 自定义的国际化
import stores from '../store/store'            // 自定义的全局变量
import Base from '../assets/js/baseFun.js'     // 自定义的公共函数和公共请求方法

//import './cube-ui.js'                         // 全局注册cubeUI组件

Vue.config.productionTip = false
// 注册全局函数和全局常量, 方便各个组件和页面js调用公共函数
Vue.prototype.baseFun = Base.baseFun
Vue.prototype.baseAjax = Base.baseAjax
Vue.prototype.$fileCache = Base.baseFileCache

///jarong add end

// Init App
new Vue({
  el: '#app',
  store: stores, // 全局变量 jarong add
  render: (h) => h(App),

  // Register App Component
  components: {
    app: App
  },
});