<template>
  <f7-page name="logout">
    <f7-navbar title="返回" back-link="Back" ></f7-navbar>


    <f7-button class="col" large fill raised color="green" @click="processLogoutButton">退出账户</f7-button>

    <!--<f7-link class="hiddenlink" @click="$f7router.navigate('/settings/')"  ref="gotoHome" text="gohome"></f7-link> -->
    <f7-link class="hiddenlink" @click="$f7router.back()"  ref="gotoHome" text="gohome"></f7-link>
    <f7-link class="hiddenlink" href="/login/" ref="gotoLogin" text="abc"></f7-link>

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
        errorMessage: ''
        //jarong add end
      }
    },
    methods: {

      processLogoutButton () {
        // 发起登录请求
        let myThis = this

        myThis.baseFun.ajaxUserLogout({
          success: (response) => {
            // 登出成功。
            console.log('logout success:', JSON.stringify(response.data))

            // 登出成功后，回首页。
            myThis.showSuccess = true
           // myThis.$router.push('/')
           // this.$refs.gotoLogin.$el.click();
            this.$refs.gotoHome.$el.click()
          },
          error: (response) => {
            console.log('logout fail.')

            myThis.showError = false
            if (response.response) {
              // 显示服务器反馈的错误信息
              // myThis.errorMessage = myThis.$t(response.response.data.message)
              // myThis.showError = true
            //  myThis.$router.push('/')
              //this.$refs.gotoLogin.$el.click();
              this.$refs.gotoHome.$el.click()
            }
            else {
              // 网络错误
              myThis.errorMessage = myThis.response.message
              myThis.showError = true
            }
          }
        })
      }

    },
    mounted() {

    }
  }
</script>