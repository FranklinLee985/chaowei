<template>
  <f7-page name="home">
    <!-- Top Navbar -->
    <f7-navbar :sliding="false" large>
      <f7-button  ref="loginlogout" @click="processLoginLogout">{{(isLogin?"登出":"登入")}}</f7-button>
      <f7-button  ref="toastDemo" @click="toastDemo">"Toast"</f7-button>

    <f7-button  ref="joinroomTeacher" @click="joinRoomTeacher">"Teacher"</f7-button>
	<f7-button  ref="joinroomStudent" @click="joinRoomStudent">"Student"</f7-button>
	
	
      <f7-nav-right>
        <f7-link icon-ios="f7:menu" icon-aurora="f7:menu" icon-md="material:menu" panel-open="right"></f7-link>
      </f7-nav-right>
      <f7-nav-title-large sliding>classroom</f7-nav-title-large>
    </f7-navbar>

    <f7-link class="hiddenlink" href="/login/" ref="loginLink" text="Login"></f7-link>

      <f7-link href="http://www.baidu.com" external>Google</f7-link>
     <f7-link  href="https://global.talk-cloud.net/1794834527/100211/1/0" ref="teacherLink" text="Teacher" external></f7-link>

      <f7-link  href="https://global.talk-cloud.net/1794834527/100211/1/2" ref="studentLink" text="Student" external></f7-link>
	  
	  
	  <f7-link  href="chaoweiclass://?host=global.talk-cloud.net&password=5678&serial=545718172&nickname=kenny&userrole=2" ref="classRoomLink" text="StudentRoom" external></f7-link>
	  

      <f7-link  href="https://www.talk-cloud.net/replay/1150905677/100211/1581606675329/" ref="playBackLink" text="PlayBack1" external></f7-link>

      <f7-link  href="https://global.talk-cloud.net/replay/1684939835/100211/1581577799121/" ref="playBackLink" text="PlayBack2" external></f7-link>


      <f7-toolbar tabbar top-md>
        <f7-link tab-link="#tab-1" tab-link-active>数学</f7-link>
        <f7-link tab-link="#tab-2">英语</f7-link>
        <f7-link tab-link="#tab-3">语文</f7-link>
      </f7-toolbar>

      <f7-tabs>
        <f7-tab id="tab-1" class="page-content" tab-active>
          <f7-block>
            <p>数学一年级</p>
            <p>数学二年级</p>
            <p>数学三年级</p>
            <p>数学四年级</p>
            <p>数学五年级</p>
            <p>数学六年级</p>
          </f7-block>
        </f7-tab>
        <f7-tab id="tab-2" class="page-content">
          <f7-block>
            <p>英语一年级</p>
            <p>英语二年级</p>
            <p>英语三年级</p>
            <p>英语四年级</p>
            <p>英语五年级</p>
            <p>英语六年级</p>
          </f7-block>
        </f7-tab>
        <f7-tab id="tab-3" class="page-content">
          <f7-block>
            <p>语文一年级</p>
            <p>语文二年级</p>
            <p>语文三年级</p>
            <p>语文四年级</p>
            <p>语文五年级</p>
            <p>语文六年级</p>
          </f7-block>
        </f7-tab>
      </f7-tabs>




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
  import {mapGetters} from 'vuex'

  export default {
    computed: {

      // 计算属性的 getter
      ...mapGetters([
        'loading',
        'fileCacheReady',
        'userInfo',
        'headShow',
        'isLogin'
      ]),

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
        loginStr:'Login'
        //jarong add end
      }
    },
    methods: {
		joinRoomTeacher()
		{
			talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=1234&serial=545718172&nickname=teacher&userrole=0");
		},
		joinRoomStudent()
		{
			talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=5678&serial=545718172&nickname=tom&userrole=2");		
		},
		toastDemo()
		{
			talkplus.showToast("这是Talk plus Toast内容");
		},
      processLoginLogout()
      {
        let myThis = this
        if (myThis.userInfo.csrfToken!= null) //login already
        {
            this.processLogoutButton()
            myThis.loginStr = "登入"
        }
        else
        {
          this.$refs.loginLink.$el.click()
          if (myThis.userInfo.csrfToken!= null) //login already
          {
            myThis.loginStr = "登入"
          }
          else
            myThis.loginStr = "登出"
        }
      },
      processGetRoleButton () {
        // 发起登录请求
        let myThis = this


          myThis.baseFun.ajaxUserGetRole({
          uid: myThis.userInfo.uid,
          user:  myThis.userInfo.user,
          password: myThis.userInfo.password,
          success: (response) => {
            // 登录成功后，回首页
            console.log('get role success:', JSON.stringify(response.data))
            //myThis.routes.push('/') //jarong modify
            //myThis.alertLoginResult('login success',true)
          //  let theRoleData = JSON.parse(response.data)
          //  JSON.to resonse.data.
            if (response.data.access != null)
            {
                return response.data.access[1]
            }

          },
          error: (response) => {
            // 登录成功后，回首页
            console.log('get role failed:')
            // myThis.routes.push('/')//jarong modify
            //myThis.alertLoginResult('login success',true)

            return ''
          }
        })

      },
      processLogoutButton () {
        // 发起登录请求
        let myThis = this

        myThis.baseFun.ajaxUserLogout({
           logout_token: myThis.userInfo.logoutToken,

            success: (response) => {
            // 登出成功。
            console.log('logout success:', JSON.stringify(response.data))

            // 登出成功后，回首页。
           // myThis.showSuccess = true
            // myThis.$router.push('/')
            // this.$refs.gotoLogin.$el.click();
           // this.$refs.gotoHome.$el.click()
          },
          error: (response) => {
            console.log('logout fail.')

            //myThis.showError = false
            if (response.response) {
              // 显示服务器反馈的错误信息
              // myThis.errorMessage = myThis.$t(response.response.data.message)
              // myThis.showError = true
              //  myThis.$router.push('/')
              //this.$refs.gotoLogin.$el.click();
            //  this.$refs.gotoHome.$el.click()
            }
            else {
              // 网络错误
              //myThis.errorMessage = myThis.response.message
             // myThis.showError = true
            }
          }
        })
      }

    },
    mounted() {
      let myThis = this
      if (myThis.userInfo.csrfToken!= null)
      {
        this.loginStr = "登出"
      }
      else
        this.loginStr = "登入"

    }
  }
</script>
