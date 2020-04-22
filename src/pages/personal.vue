<template>
  <f7-page name="personal">
    <f7-navbar title="個人檔案">


	</f7-navbar>
	      <br>
      <f7-block>

      <!--<span style="font-size: large; color:#000000">用戶名稱:</span>&nbsp&nbsp&nbsp -->
          <i  class="icon f7-icons" >person</i>
      <span style="font-size: large; margin-left:24px ;color:#000000">{{isLogin? userInfo.user:''}}</span>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
      </f7-block>
    <f7-list no-hairlines-md>


        <f7-list-item link="/histroyClasses/" title="上課曆史">

            <i slot="media" class="icon f7-icons">square_list</i>
        </f7-list-item>
        <br>
        <f7-button @click="processLogoutButton"  fill color="red" round>退出賬號</f7-button>
        <br>

         <!--f7-list-item link="/about/" title="About"></f7-list-item-->


    </f7-list>

    </f7-page>
</template>

<style>


  .hiddenlink{
    position: absolute;
    top: 0;
    left: -100%;
  }
  .page-content {
      overflow: auto;
      -webkit-overflow-scrolling: touch;
      box-sizing: border-box;
      height: 100%;
      position: relative;
      z-index: 1;
      padding-top: 120px;
      padding-bottom: 5px;
  }

</style>


<script>
  import { Device }  from 'framework7/framework7-lite.esm.bundle.js';
  import {mapGetters} from 'vuex'

  import cordovaApp from '../js/cordova-app.js';
  //import routes from '../js/routes.js';

  export default {
    computed: {

      // 计算属性的 getter
      ...mapGetters([
        'loading',
        'fileCacheReady',
        'userInfo',
        'headShow',
		'isLogin'
      ])
    },

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


        user_role:''

      }
    },
    methods: {
        processLogoutButton () {
            // 发起登录请求
            let myThis = this

            myThis.baseFun.ajaxUserLogout({
                logout_token: myThis.userInfo.logoutToken,

                success: (response) => {
                    // 登出成功。
                    console.log('logout success:', JSON.stringify(response.data))

                    this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', true)
                    // 登出成功后，回首页。

                },
                error: (response) => {
                    console.log('logout fail.')
                    myThis.baseFun.ajaxUserForceLogout({
                        success: (response) => {
                            // 登出成功。
                            console.log('logout success.')
                            this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', true)
                        },
                        error: (response) => {
                            console.log('logout fail.')
                            this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', true)
                        }
                    })
                }
            })
        },

    },
    mounted() {


        }
  }
</script>