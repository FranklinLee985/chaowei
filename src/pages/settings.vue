<template>
  <f7-page name="个人中心">
    <f7-navbar title="个人中心"></f7-navbar>


    <f7-list no-hairlines-md>
    
	 <!-- <f7-list-item  title="修改密码"></f7-list-item> -->
      <f7-list-item  title="">{{user_role}}</f7-list-item>

      <f7-list-item link="/logout/" title="退出该账号"></f7-list-item>

    </f7-list>

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
        'headShow'
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

      getRole (role) {
        let therole = role.toLowerCase()
        if (therole == 'student')
        {
          this.user_role = '学生'
        }
        else if (therole =='school admin')
        {
          this.user_role = '学校管理员'
        }
        else if (therole == 'teacher')
        {
          this.user_role =  '老师 '
        }
        else if (therole == 'administrator')
          this.user_role = '超级管理员'
        else
          this.user_role = ''
        if (this.user_role != '')
          this.user_role = '我是' + this.user_role
      }

    },
    mounted() {
      //this.getRole ('Student')

      let that = this

      let theuserinfo = that.userInfo

      if (theuserinfo != null) {
        if (theuserinfo.loginData !=null)
        {
            let theloginData = JSON.parse(theuserinfo.loginData)
            if (theloginData.current_user != null)
            {
              if (theloginData.current_user.roles!=null)
                that.getRole(theloginData.current_user.roles[1])
            }
        }
      }

        }
  }
</script>