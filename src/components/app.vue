<template>
<f7-app :params="f7params" >

  <!-- Left panel with cover effect-->
  <f7-panel left cover theme-dark>
    <f7-view>
      <f7-page>
        <f7-navbar title="Left Panel"></f7-navbar>
        <f7-block>Left panel content goes here</f7-block>
      </f7-page>
    </f7-view>
  </f7-panel>


  <!-- Right panel with reveal effect-->
  <f7-panel right reveal theme-dark>
    <f7-view>
      <f7-page>
        <f7-navbar title="Right Panel"></f7-navbar>
        <f7-block>Right panel content goes here</f7-block>
      </f7-page>
    </f7-view>
  </f7-panel>

  <!-- f7-list-item link="/about/" title="About"></f7-list-item -->


  <!-- Views/Tabs container -->
  <f7-views tabs class="safe-areas">
    <!-- Tabbar for switching views-tabs -->
    <f7-toolbar tabbar labels bottom>
      <f7-link tab-link="#view-home" tab-link-active icon-ios="f7:house_fill" icon-aurora="f7:house_fill" icon-md="material:home" text="上课"></f7-link>
 <!--     <f7-link tab-link="#view-catalog" icon-ios="f7:square_list_fill" icon-aurora="f7:square_list_fill" icon-md="material:view_list" text="上课"></f7-link> -->
      <!--<f7-link tab-link="#view-settings" icon-ios="f7:gear" icon-aurora="f7:gear" icon-md="material:settings" text="个人中心"></f7-link>-->
    </f7-toolbar>

    <!-- Your main view/tab, should have "view-main" class. It also has "tab-active" class -->
    <f7-view id="view-home" main tab tab-active url="/"></f7-view>

    <!-- Catalog View -->
 <!--   <f7-view id="view-catalog" name="catalog" tab url="/catalog/"></f7-view> -->

    <!-- Settings View -->
   <!-- <f7-view id="view-settings" name="settings" tab url="/settings/"></f7-view>-->

  </f7-views>


  <!-- Popup -->
  <f7-popup id="my-popup">
    <f7-view>
      <f7-page>
        <f7-navbar title="Popup">
          <f7-nav-right>
            <f7-link popup-close>Close</f7-link>
          </f7-nav-right>
        </f7-navbar>
        <f7-block>
          <p>Popup content goes here.</p>
        </f7-block>
      </f7-page>
    </f7-view>
  </f7-popup>

  <f7-login-screen id="my-login-screen">
    <f7-view>
      <f7-page login-screen>
        <f7-login-screen-title>Login</f7-login-screen-title>
        <f7-list form>
          <f7-list-input
            type="text"
            name="username"
            placeholder="Your username"
            :value="username"
            @input="username = $event.target.value"
          ></f7-list-input>
          <f7-list-input
            type="password"
            name="password"
            placeholder="Your password"
            :value="password"
            @input="password = $event.target.value"
          ></f7-list-input>
        </f7-list>
        <f7-list>
          <f7-list-button title="Sign In" @click="processLoginButton"></f7-list-button>
          <f7-block-footer>
            Some text about login information.<br>Click "Sign In" to close Login Screen
          </f7-block-footer>
        </f7-list>
      </f7-page>
    </f7-view>
  </f7-login-screen>
</f7-app>
</template>
<style>
  .setPsd{
    position: absolute;
    top: 0;
    left: -100%;
  }
</style>
<script>
  import { Device }  from 'framework7/framework7-lite.esm.bundle.js';
  import cordovaApp from '../js/cordova-app.js';
  import routes from '../js/routes.js';

  export default {
    data() {
      return {
        // Framework7 Parameters
        f7params: {
          id: 'io.framework7.classroom', // App bundle ID
          name: 'classroom', // App name
          theme: 'auto', // Automatic theme detection
          // App root data
          data: function () {
            return {

              // Demo products for Catalog section
              products: [
                {
                  id: '1',
                  title: 'Apple iPhone 8',
                  description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Nisi tempora similique reiciendis, error nesciunt vero, blanditiis pariatur dolor, minima sed sapiente rerum, dolorem corrupti hic modi praesentium unde saepe perspiciatis.'
                },
                {
                  id: '2',
                  title: 'Apple iPhone 8 Plus',
                  description: 'Velit odit autem modi saepe ratione totam minus, aperiam, labore quia provident temporibus quasi est ut aliquid blanditiis beatae suscipit odio vel! Nostrum porro sunt sint eveniet maiores, dolorem itaque!'
                },
                {
                  id: '3',
                  title: 'Apple iPhone X',
                  description: 'Expedita sequi perferendis quod illum pariatur aliquam, alias laboriosam! Vero blanditiis placeat, mollitia necessitatibus reprehenderit. Labore dolores amet quos, accusamus earum asperiores officiis assumenda optio architecto quia neque, quae eum.'
                },
              ]
            };
          },

          // App routes
          routes: routes,


          // Input settings
          input: {
            scrollIntoViewOnFocus: Device.cordova && !Device.electron,
            scrollIntoViewCentered: Device.cordova && !Device.electron,
          },
          // Cordova Statusbar settings
          statusbar: {
            iosOverlaysWebView: true,
            androidOverlaysWebView: false,
          },
        },

        // Login screen data
        username: '',
        password: '',

        //jarong add
        pwdType: 'password',
        // 提示数据是否有效
        showInvalid: false,
        // 提示登录成功
        showSuccess: false,
        // 提示出错
        showError: false,
        errorMessage: ''
        //jarong add end
      }
    },
    methods: {
      alertLoginData() {
        this.$f7.dialog.alert('Username: ' + this.username + '<br>Password: ' + this.password, () => {
          this.$f7.loginScreen.close();

        });
      },
      alertLoginResult(result,close) {
        this.$f7.dialog.alert(result, () => {
          if (close == true)
          this.$f7.loginScreen.close();
        });
      },
	  processLoginButton () {
        let myThis = this
		myThis.baseFun.ajaxUserLogin({
          user: myThis.username,
          password: myThis.password,
          success: (response) => {
            // 登录成功。
            console.log('login success:', JSON.stringify(response.data))

            // 获取用户信息，包括用户头像。
            myThis.baseFun.ajaxUserInfoExt({
              uid: response.data.current_user.uid,
              user: response.data.current_user.name,
              password: myThis.password,
              success: (response) => {
                // 登录成功后，回首页
                myThis.showSuccess = true
                myThis.routes.push('/') //jarong modify
                myThis.alertLoginResult('login success',true)
              },
              error: (response) => {
                // 登录成功后，回首页
                myThis.showSuccess = true
               // myThis.routes.push('/')//jarong modify
                myThis.alertLoginResult('login success',true)
              }
            })
          },
          error: (response) => {
            console.log('login fail.')

            myThis.showError = false
            if (response.response) {
              // 显示服务器反馈的错误信息
              let message = response.response.data.message
              if (message.indexOf('anonymous') > -1) {
                // This route can only be accessed by anonymous users.
                myThis.errorMessage = 'Already login.'
              } else {
                myThis.errorMessage = 'Sorry, unrecognized username or password.'
              }

              myThis.showError = true
            }
            else {
              // 网络错误
              myThis.errorMessage = response.message
              myThis.showError = true
            }
            myThis.alertLoginResult('login fail',false)
          }
        })
		}
    },
    mounted() {
      this.$f7ready((f7) => {
        // Init cordova APIs (see cordova-app.js)
        if (Device.cordova) {
          cordovaApp.init(f7);
        }

        // Call F7 APIs here
       // this.$f7router.currentRoute = '/about'
       // var currentView = f7.views.current;
      //  currentView.router.routes.push('/settings/');
       // this.$store.state.

     //  localStorage.removeItem('csrfToken') //debug
       //var token = localStorage.getItem('csrfToken')
        //if (token == null)
          //  this.$refs.setPsd.$el.click();


      //  this.$router.push({path: '/webapp/page', name: 'webappBookPageMath', params: {url: url}})
       // this.$f7route.ur
      });
    }
  }
</script>