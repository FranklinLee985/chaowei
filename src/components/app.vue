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
      <f7-link tab-link="#view-home" tab-link-active icon-ios="f7:book_fill" icon-aurora="f7:book_fill" icon-md="material:book" text="課堂"></f7-link>
      <f7-link tab-link="#view-personal" icon-ios="f7:person_fill" icon-aurora="f7:person_fill" icon-md="material:person" text="個人"></f7-link>


    </f7-toolbar>

    <!-- Your main view/tab, should have "view-main" class. It also has "tab-active" class -->



    <!-- Catalog View -->
 <!--   <f7-view id="view-catalog" name="catalog" tab url="/catalog/"></f7-view> -->

    <!-- Settings View -->


    <f7-view id="view-home" main tab tab-active url="/"></f7-view>

    <f7-view id="view-personal" name="personal" tab url="/personal/"></f7-view>
    <!-- Settings View -->


  </f7-views>





  <f7-login-screen id="my-login-screen"  :opened="loginScreenStatus" @loginscreen:closed="loginClose">
    <f7-view>

      <f7-page login-screen>



          <img :src='imgUrl.default' class="aligncenter" alt="">



       <!-- <f7-login-screen-title>用戶登錄</f7-login-screen-title>-->

        <f7-list simple-list>
          <f7-list-item  style="color:#FF0000" v-if="showError" title="">{{errorMessage}}</f7-list-item>
          <f7-list-item  style="color:#0BB20C" v-if="showSuccess" title="">{{errorMessage}}</f7-list-item>

        </f7-list>

        <f7-list form>
          <f7-list-input
                  type="text"
                  name="username"
                  placeholder="請輸入用戶名"
                  :value="username"
                  @input="username = $event.target.value"
          ></f7-list-input>
          <f7-list-input
                  type="password"
                  name="password"
                  placeholder="請輸入密碼"
                  :value="password"
                  @input="password = $event.target.value"
          ></f7-list-input>
        </f7-list>
        <f7-list>
          <f7-list-button title="登入" @click=" processLoginButton"></f7-list-button>
          <f7-list-button title="取消" @click="cancelLogin"></f7-list-button>
          <f7-block-footer>
            請輸入用戶名和密碼登入
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

  .aligncenter {
    clear: both;
    display: block;
    margin: auto;
  }

  .user-portrait img{
    font-size: 6rem;
    position: absolute;
    height: 6rem;
    width: 6rem;
    left: 50%;
    top: 50%;
    margin-left: -3rem;
    margin-top: -3rem;
    border: 3px solid rgba(255, 255, 255, 0.4);
    border-radius: 6rem;
  }

</style>
<script>
  import { Device }  from 'framework7/framework7-lite.esm.bundle.js';
  import cordovaApp from '../js/cordova-app.js';
  import routes from '../js/routes.js';
  import {mapGetters} from "vuex";
  import stores from "../store/store";

  export default {

    computed: {

      // 计算属性的 getter
      ...mapGetters([
        'loading',
        'fileCacheReady',
        'userInfo',
        'loginScreenStatus',
        'isLogin'
      ]),

    },
    data() {
      return {
        // Framework7 Parameters
        f7params: {
          id: 'io.framework7.classroom', // App bundle ID
          name: 'Tutor@home', // App name
          theme: 'auto', // Automatic theme detection
          // App root data
          data: function () {
            return {}
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
        userId:'',
        //jarong add
        pwdType: 'password',
        // 提示数据是否有效
        showInvalid: false,
        // 提示登录成功
        showSuccess: false,
        // 提示出错
        showError: false,
        errorMessage: '',
        imgUrl:require("../assets/images/login.png"),
       // loginScreenOpened: false,
        //jarong add end
      }
    },
    methods: {
      loginClose()
      {
        this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', false)
      },

       cancelLogin(){
       // this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', false)
        this.$f7.loginScreen.close()
         navigator.app.exitApp()
      },
        getRole()
        {
          let myThis = this
          myThis.baseFun.ajaxUserGetLoginState({

            success: (response) => {
              // 登录成功后，回首页
              console.log('get role success:', JSON.stringify(response.data))

              var event = document.createEvent('Event');
              event.initEvent('refreshClass', true, true);
              document.dispatchEvent(event)
              myThis.$f7.loginScreen.close();
            },
            error: (response) => {
              // 登录成功后，回首页
              console.log('get role failed:')
              myThis.$f7.loginScreen.close();
            }
          })
        },
        getUserInfo()
        {
          let myThis = this


          // 获取用户信息，包括用户头像。
          myThis.baseFun.ajaxUserInfoExt({
            uid: myThis.userId,
            user: myThis.username,
            password: myThis.password,
            success: (response) => {
              myThis.getRole()
            },
            error: (response) => {
              // 登录成功后，回首页

              myThis.getRole()
            }
          })

        },
        processSecondLogin()
        {
          let myThis = this
          myThis.baseFun.ajaxUserLogin({
            user: myThis.username,
            password: myThis.password,
            success: (response) => {
              // 登录成功。
              // alert("登录成功#");
              console.log('login success:', JSON.stringify(response.data))

              myThis.userId = response.data.current_user.uid

              myThis.showSuccess = false
              myThis.showError = false
              myThis.getUserInfo()

            },
            error: (response) => {
              // alert("登录失败#");
              this.adminLogined = false
              console.log('login fail.')
              myThis.showSuccess = false

              myThis.showError = false
              if (response.response) {
                // 显示服务器反馈的错误信息
                let message = response.response.data.message
                if (message == null) {
                  myThis.errorMessage = '網絡錯誤'
                  myThis.showError = true
                } else {
                  if (message.indexOf('anonymous') > -1) {
                    // This route can only be accessed by anonymous users.
                    //myThis.errorMessage = 'Already login.'
                    myThis.errorMessage = '已經登錄'


                  } else {
                    myThis.errorMessage = '用戶名或密碼錯誤'

                    //myThis.errorMessage = 'Sorry, unrecognized username or password.'
                  }
                  myThis.showError = true

                }
              }
              else
                {
                  // 网络错误
                  // myThis.errorMessage = response.message
                  myThis.errorMessage = '網絡錯誤'
                  myThis.showError = true
                }
              }
              //myThis.alertLoginResult('login fail',false)


          })
        },
        forceLogoutLoginAgain()
        {
          let myThis = this
          myThis.baseFun.ajaxUserForceLogout({
            success: (response) => {
              // 登出成功。
              console.log('logout success.')
              myThis.processSecondLogin()
            },
            error: (response) => {
              console.log('logout fail.')
              myThis.processSecondLogin()
            }
          })

        },
        processLoginButton () {
          let myThis = this
           let retryLogin = false
          let getInfo = false

         myThis.baseFun.ajaxUserLogin({
            user: myThis.username,
            password: myThis.password,
            success: (response) => {
              // 登录成功。
              // alert("登录成功#");
              console.log('login success:', JSON.stringify(response.data))
              myThis.userId = response.data.current_user.uid
             // myThis.dispuser = myThis.username
              myThis.getUserInfo()

            },
            error: (response) => {
              // alert("登录失败#");
              this.adminLogined = false
              console.log('login fail.')
              myThis.showSuccess = false

              myThis.showError = false
              if (response.response) {
                // 显示服务器反馈的错误信息
                let message = response.response.data.message
                if (message == null)
                {
                  myThis.errorMessage = '網絡錯誤'
                 myThis.showError = true
               }
                else
                {
                  if (message.indexOf('anonymous') > -1) {
                    // This route can only be accessed by anonymous users.
                    //myThis.errorMessage = 'Already login.'
                    myThis.errorMessage = '已經登錄'
                    myThis.forceLogoutLoginAgain()

                  } else {
                    myThis.errorMessage = '用戶名或密碼錯誤'
                      myThis.showError = true
                    //myThis.errorMessage = 'Sorry, unrecognized username or password.'
                  }
                }

              }
              else {
                // 网络错误
                // myThis.errorMessage = response.message
                myThis.errorMessage = '網絡錯誤'
                myThis.showError = true
              }
             }
          })
        },

    },
    mounted() {
      this.$f7ready((f7) => {
        // Init cordova APIs (see cordova-app.js)
        if (Device.cordova) {
          cordovaApp.init(f7);
        }


          this.baseFun.ajaxUserGetLoginState({

              success: (response) => {
                  console.log('get login state success:', JSON.stringify(response.data))
                if (this.isLogin == false)
                  this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', true)

                },
              error: (response) => {
                  console.log('get login state failed:')

                this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', true)
              }
          })



        StatusBar.styleLightContent();
        StatusBar.overlaysWebView(false);
      });
    }
  }
</script>