<template>
  <f7-page name="login">
   <!--<f7-navbar title="About" back-link="Back"></f7-navbar>-->
    <f7-button fill raised login-screen-open="#my-login-screen" ref="setPsdlogin">Login Screen</f7-button>


    <!--<f7-view id="view-home" main tab tab-active url="/"></f7-view>-->
    <!-- <f7-link class="hiddenlink" href="/" ref="gotoHome" text="abc"></f7-link --> -->

    <f7-link class="hiddenlink"  @click="$f7router.back()"  ref="gotoHome" text="abc"></f7-link >

    <f7-login-screen id="my-login-screen">
      <f7-view>
        <f7-page login-screen>

          <f7-login-screen-title>用户登录</f7-login-screen-title>

          <f7-list simple-list>
               <f7-list-item  style="color:#FF0000" v-if="showError" title="">{{errorMessage}}</f7-list-item>
            <f7-list-item  style="color:#0BB20C" v-if="showSuccess" title="">{{errorMessage}}</f7-list-item>

          </f7-list>

          <f7-list form>
            <f7-list-input
                    type="text"
                    name="username"
                    placeholder="请输入用户名"
                    :value="username"
                    @input="username = $event.target.value"
            ></f7-list-input>
            <f7-list-input
                    type="password"
                    name="password"
                    placeholder="请输入密码"
                    :value="password"
                    @input="password = $event.target.value"
            ></f7-list-input>
          </f7-list>
          <f7-list>
            <f7-list-button title="登录" @click="processLoginButton"></f7-list-button>
            <f7-list-button title="取消" @click="processCancelButton"></f7-list-button>
            <f7-block-footer>
              请输入用户名和密码登录
            </f7-block-footer>
          </f7-list>
        </f7-page>
      </f7-view>
    </f7-login-screen>

  </f7-page>
</template>
<style>
  .hiddenlink{
    position: absolute;
    top: 0;
    left: -100%;
  }
</style>

<script>
  import { Device }  from 'framework7/framework7-lite.esm.bundle.js';
  import cordovaApp from '../js/cordova-app.js';
  //import routes from '../js/routes.js';

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

                  };
          },

          // App routes
       //   routes: routes,


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
        errorMessage: '',
        errorstyle: 'color:#FF0000',
        correctstyle:'color:#0BB20C',
        showstyle:'color:#FF0000'
        //jarong add end
      }
    },
    methods: {
      alertLoginData() {
        this.$f7.dialog.alert('Username: ' + this.username + '<br>Password: ' + this.password, () => {
          localStorage.setItem('csrfToken','aaaaa')
          this.$f7.loginScreen.close();
          this.$refs.gotoHome.$el.click();

        });
      },
      alertLoginResult(result,close) {
        this.$f7.dialog.alert(result, () => {
          if (close == true)
            this.$f7.loginScreen.close();
        });
      },

      processLogoutButton () {
        // 发起登录请求
        let myThis = this

        myThis.baseFun.ajaxUserLogout({
          success: (response) => {
            // 登出成功。
            console.log('logout success:', JSON.stringify(response.data))
        },
          error: (response) => {
            console.log('logout fail.')

          }
        })
      },

      processCancelButton () {
        this.$f7.loginScreen.close();
       // this.$refs.gotoHome.$el.click();
         this.$refs.gotoHome.$emit('click')
       // this.$emit('cancel',this.value)
      },
      processLoginButton () {
        let myThis = this
        myThis.baseFun.ajaxUserLogin({
          user: myThis.username,
          password: myThis.password,
          success: (response) => {
            // 登录成功。
              alert("登录成功#");
            console.log('login success:', JSON.stringify(response.data))
            myThis.showSuccess = false
            myThis.showError = false
            // 获取用户信息，包括用户头像。
            myThis.baseFun.ajaxUserInfoExt({
              uid: response.data.current_user.uid,
              user: response.data.current_user.name,
              password: myThis.password,
              success: (response) => {
                // 登录成功后，回首页
                  alert("登录成功1！");
                  myThis.showSuccess = true
                myThis.errorMessage = '登录成功'
                //myThis.routes.push('/') //jarong modify
                //myThis.alertLoginResult('login success',true)
                  alert("loginScreen close");
                  myThis.$f7.loginScreen.close();
                  alert("loginScreen click");
                  myThis.$refs.gotoHome.$emit('click')
              },
              error: (response) => {
                // 登录成功后，回首页
                myThis.showSuccess = true
                  alert("登录成功2！");
                 myThis.errorMessage = '登录成功'
                // myThis.routes.push('/')//jarong modify
                //myThis.alertLoginResult('login success',true)
                  alert("loginScreen close");
                  myThis.$f7.loginScreen.close();
                  alert("loginScreen click");
                  myThis.$refs.gotoHome.$emit('click')
              }
            })
          },
          error: (response) => {
              alert("登录失败#");
            console.log('login fail.')
            myThis.showSuccess = false

            myThis.showError = false
            if (response.response) {
              // 显示服务器反馈的错误信息
              let message = response.response.data.message
              if (message.indexOf('anonymous') > -1) {
                // This route can only be accessed by anonymous users.
                //myThis.errorMessage = 'Already login.'
                myThis.errorMessage = '已经登录'
              } else {
                myThis.errorMessage = '用户名或密码错误'
                //myThis.errorMessage = 'Sorry, unrecognized username or password.'
              }

              myThis.showError = true
            }
            else {
              // 网络错误
              // myThis.errorMessage = response.message
              myThis.errorMessage = '网络错误'
              myThis.showError = true
            }
            //myThis.alertLoginResult('login fail',false)
          }
        })
      }
    },
    mounted() {
      this.$refs.setPsdlogin.$el.click();
    }
  }
</script>