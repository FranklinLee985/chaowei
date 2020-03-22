<template>
  <f7-page name="home">
    <!-- Top Navbar -->
    <f7-navbar :sliding="false" large>
      <f7-button  ref="loginlogout" @click="processLoginLogout">{{(isLogin?"登出":"登入")}}</f7-button>

        <!--<f7-button  ref="toastDemo" @click="toastDemo">"Toast"</f7-button> -->
        <span style="font-size: large; color:#FE6714">{{isLogin? userInfo.user:''}}</span>

      <f7-button  ref="getclassbook" @click="getClassList" round>刷新</f7-button>
    <!--
      <f7-nav-right>
        <f7-link icon-ios="f7:menu" icon-aurora="f7:menu" icon-md="material:menu" panel-open="right"></f7-link>
      </f7-nav-right>
      -->
      <f7-nav-title-large sliding>Tutor@home</f7-nav-title-large>
    </f7-navbar>


      <f7-login-screen id="my-login-screen"  :opened="loginScreenOpened" @loginscreen:closed="loginScreenOpened = false">
      <f7-view>
        <f7-page login-screen>

          <f7-login-screen-title>用戶登錄</f7-login-screen-title>

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
            <f7-list-button title="登錄" @click="processLoginButton"></f7-list-button>
            <f7-list-button title="取消" @click="processCancelButton"></f7-list-button>
            <f7-block-footer>
                請輸入用戶名和密碼登錄
            </f7-block-footer>
          </f7-list>
        </f7-page>
      </f7-view>
    </f7-login-screen>

      
      <f7-toolbar tabbar top-md>
        <f7-link tab-link="#tab-1" tab-link-active>未上課堂</f7-link>
        <f7-link tab-link="#tab-2">已結束</f7-link>

      </f7-toolbar>
    
      <f7-tabs>
        
        <f7-tab id="tab-1" class="page-content-new" tab-active>
    
          <div class="data-table">
            <table>
              <thead>
              <tr>
                <th class="label-cell">日期</th>
                <th class="numeric-cell">時間</th>
                <th class="label-cell">課程</th>
              </tr>
              </thead>
              <tbody>

              <tr v-for="item in this.classesPending" v-bind:class="'id-' + item.id">
                <td class="label-cell">{{ item.date }}</td>

                <td class="numeric-cell">{{ item.time }}</td>
                <td class="label-cell">{{ item.title }}</td>
              </tr>
              </tbody>
            </table>
          </div>
          <f7-button  ref="joinroom" @click="joinRoom" round>上課</f7-button>


          <f7-block>
            <f7-row>
              <f7-col>
          <f7-button  ref="joinroomTeacher" v-if="userInfo.userRole == 'administrator'"  @click="joinRoomTeacher">"Teacher"</f7-button>
              </f7-col>
              <f7-col>
          <f7-button  ref="joinroomStudent"  v-if="userInfo.userRole == 'administrator'"   @click="joinRoomStudent">"Student"</f7-button>
              </f7-col>
            </f7-row>
          </f7-block>
		  
        </f7-tab>


        <f7-tab id="tab-2" class="page-content-new">



          <div class="data-table">
            <table>
              <thead>
              <tr>
                <th class="label-cell">日期</th>
                <th class="numeric-cell">時間</th>
                <th class="label-cell">課程</th>
              </tr>
              </thead>
              <tbody>

              <tr v-for="item in this.classesFinish" v-bind:class="'id-' + item.id">
                <td class="label-cell">{{ item.date }}</td>

                <td class="numeric-cell">{{ item.time }}</td>
                <td class="label-cell">{{ item.title }}</td>
              </tr>
              </tbody>
            </table>
          </div>

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
  .page-content-new {
    overflow: auto;
    -webkit-overflow-scrolling: touch;
    box-sizing: border-box;
    height: 100%;
    position: relative;
    z-index: 1;
    padding-top: 1px;
    padding-bottom: 1px;
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

        loginScreenOpened: false,
        classbook:[],
        classesPending: [],
		
        classesFinish: [],
          
        // Login screen data
        username: '',
        password: '',
        userid:'',
          dispuser:'',
        //jarong add
        pwdType: 'password',
        // 提示数据是否有效
        showInvalid: false,
        // 提示登录成功
        showSuccess: false,
        // 提示出错
        showError: false,
        errorMessage: '',
        loginStr:'Login',
          adminLogined:false
        //jarong add end
      }
    },
    methods: {
      alertMessage(message)
      {
        this.$f7.dialog.alert(message, () => {

        })
      },
      processCancelButton () {
        //this.$f7.loginScreen.close();
        this.$f7.loginScreen.close();
        // this.$refs.gotoHome.$el.click();
        // this.$emit('cancel',this.value)
      },
      processLoginButton () {
        let myThis = this
        myThis.baseFun.ajaxUserLogin({
          user: myThis.username,
          password: myThis.password,
          success: (response) => {
            // 登录成功。
           // alert("登录成功#");
            console.log('login success:', JSON.stringify(response.data))
              myThis.dispuser = myThis.username
            myThis.showSuccess = false
            myThis.showError = false
            myThis.userId = response.data.current_user.uid
            // 获取用户信息，包括用户头像。
            myThis.baseFun.ajaxUserInfoExt({
              uid: response.data.current_user.uid,
              user: response.data.current_user.name,
              password: myThis.password,
              success: (response) => {

              },
              error: (response) => {
                // 登录成功后，回首页
                  myThis.dispuser = ''
              }
            })


            myThis.baseFun.ajaxUserGetRole({
              uid: myThis.userInfo.uid,
              user:  myThis.userInfo.user,
              password: myThis.userInfo.password,
              success: (response) => {
                // 登录成功后，回首页
                console.log('get role success:', JSON.stringify(response.data))


                myThis.getClassList()

              },
              error: (response) => {
                // 登录成功后，回首页
                console.log('get role failed:')

              }
            })


            //myThis.showSuccess = false
            //myThis.errorMessage = '登錄成功'
            myThis.$f7.loginScreen.close();

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
            else {
              // 网络错误
              // myThis.errorMessage = response.message
              myThis.errorMessage = '網絡錯誤'
              myThis.showError = true
            }
            //myThis.alertLoginResult('login fail',false)
          }

        })
      },


        sortClass(a,b) {  //排序函数
            return(a.begintime.localeCompare(b.begintime));
        },
        sortClasses()
        {
            this.classesPending.sort(this.sortClass);
           // this.classesFinish.sort(this.sortClass);
        },

		joinRoomTeacher()
		{
			talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=1234&serial=601509809&nickname=teacher&userrole=0");
		},
		joinRoomStudent()
		{
			talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=5678&serial=601509809&nickname=tom&userrole=2");
		},

        getDatetimeValue(dateObject)
        {
            let year = dateObject.getFullYear()
            let mon =  dateObject.getMonth()+1
            let day = dateObject.getDate()
            let hour = dateObject.getHours()

            let min = dateObject.getMinutes()

            let datetime = year * 100000000 + mon *1000000 + day *10000+  hour *100 + min
            return datetime
        },
        findLastestClass()
        {
            let myDate = new Date();
            let datetime =   this.getDatetimeValue(myDate)
             let foundItem = null
            for (let item in this.classesPending)
            {
                let beginTime = new Date(this.classesPending[item].begintime)
                let endTime = new Date(this.classesPending[item].endtime)
                let beginVal = this.getDatetimeValue(beginTime)
                let endVal = this.getDatetimeValue(endTime)
                beginVal = beginVal - 15
                if (datetime >= beginVal && datetime <= endVal)
                {
                    foundItem = this.classesPending[item]

                    break;
                }
                //diff = items.datetime
            }
            return foundItem

        },
        joinRoom()
        {
            let datetime
            var lastestClass
            var str =''
            var role = -1
            var nickname=this.userInfo.userNickname
            if (nickname == '')
              nickname = this.userInfo.user

            if (this.isLogin == false)
            {
              this.alertMessage('請先登陸.')
              return
            }
            if (this.userInfo.userRole == null)
            {
              this.alertMessage('无法获得用户的身份.')
              return
            }

            if (this.userInfo.userRole.indexOf("student") != -1)
              role = 2
            else if (this.userInfo.userRole.indexOf("teacher")!= -1)
              role = 0
            if (role == -1)
            {
              this.alertMessage('該用戶身份不能進入房間.')
              return
            }




            this.sortClasses()
           // this.findLastestClass()
            lastestClass = this.findLastestClass()
            if (lastestClass != null)
            {
              str = 'chaoweiclass://?host=global.talk-cloud.net'
              if (role == 0) //teacher need a default password
              {
                if (lastestClass.password =='')
                {
                  lastestClass.password ='1234'

                }
                str+='&password='+lastestClass.password
              }

              str += '&serial='+lastestClass.roomid
              str += '&nickname='+nickname
              str += '&userrole='+role
              //str += '&nickname=tom&userrole=2'
            //  talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=56789&serail=1346975754&nickname=tom&userrole=2");
              //talkplus.showToast(str);
              //this.alertMessage(str)
              talkplus.joinRoom(str);

            //  talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=1234&serial=545718172&nickname=teacher&userrole=0");
            }
            else
              talkplus.showToast("上課時間沒到");
        },
		toastDemo()
		{
			talkplus.showToast("这是Talk plus Toast内容");
		},
         getDateStrFromDateObject(dateObject)
        {
          var year = dateObject.getFullYear()       //年
          var month = dateObject.getMonth() + 1     //月
          var day = dateObject.getDate()            //日

          var clock = year + "-"
          if(month < 10)
              clock += "0"
          clock += month + "-"
          if(day < 10)
              clock += "0"
          clock += day
          return clock
        },
        getTimeStrFromDateObject(dateObject)
        {
            var hh = dateObject.getHours()            //时
            var mm = dateObject.getMinutes()          //分
            var clock = ""
            if(hh < 10)
                clock += "0"

            clock += hh + ":"
            if (mm < 10) clock += '0'
            clock += mm
            return clock
        },
        getClassList()
        {
            let myThis = this
          if (myThis.userInfo.csrfToken == null) //not login
          {
            return
          }
            myThis.baseFun.ajaxUserGetClassbook({
                uid: myThis.userInfo.uid,
                success: (response) => {
                    // 登录成功后，回首页
                    console.log('get class success:', JSON.stringify(response.data))
                  let myDate = new Date();
                  let nowtime =   this.getDatetimeValue(myDate)
                  this.classbook = []
                  this.classesPending = []
                  this.classesFinish = []
                    if (response.data.data.length >0)
                    {
                        for (let item in response.data.data) {
                          let theitem = response.data.data[item]
                          let beginTime = new Date(theitem.start)
                          let endTime = new Date(theitem.end)
                          let endVal = this.getDatetimeValue(endTime)
                          let classStatus = nowtime > endVal ? 0 : 1;
                          //let showdate = beginTime.toLocaleDateString()
                           let showdate = this.getDateStrFromDateObject(beginTime)

                            let showtime = this.getTimeStrFromDateObject(beginTime)
                          //let showtime = beginTime.toLocaleTimeString()

                          if (classStatus == 1) {
                            this.classesPending.push({
                              roomid:  theitem.rID,
                              password: '',
                              title: theitem.course_title,
                              begintime: theitem.start,
                              endtime: theitem.end,
                              date:showdate,
                              time:showtime,
                              status: 1
                            })
                          }
                        else {
                            this.classesFinish.push({
                              roomid:  theitem.rID,
                              password: '',
                              title: theitem.course_title,
                              begintime: theitem.start,
                              endtime: theitem.end,
                              date:showdate,
                              time:showtime,
                              status: 0
                            })

                          }
                        }
                    }




                },
                error: (response) => {
                    // 登录成功后，回首页
                    console.log('get role failed:')
                }
            })
        },
      processLoginLogout() {


        let myThis = this
        if (myThis.userInfo.csrfToken != null) //login already
        {
          this.processLogoutButton()
          myThis.loginStr = "登入"
        } else {

          this.loginScreenOpened = true
          /*
          if (myThis.userInfo.csrfToken!= null) //login already
          {
            myThis.loginStr = "登入"
          }
          else
            myThis.loginStr = "登出"
        }
        */

        }
          this.adminLogined = false
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
    onBackKeyDown() {
      alertMessage('再點擊一次退出!')
      //talkplus.showToast('再点击一次退出!');

      window.removeEventListener("backbutton", this.onBackKeyDown); // 注销返回键
      window.addEventListener("backbutton", this.exitApp); // 返回键
    // 3秒后重新注册
    var intervalID = window.setInterval(function() {
      window.clearInterval(intervalID);
      window.removeEventListener("backbutton", this.exitApp); // 注销返回键
      window.addEventListener("backbutton", this.onBackKeyDown); // 返回键
    }, 3000);
  },
   exitApp(){
    navigator.app.exitApp();
  },

    mounted() {
      let myThis = this
      if (myThis.userInfo.csrfToken!= null)
      {
        this.loginStr = "登出"
         // this.dispuser = myThis.userInfo.user
        this.getClassList()
      }
      else {
          this.loginStr = "登入"
          //this.dispuser = ''
      }

        window.addEventListener('backbutton', this.onBackKeyDown)

    }
  }
</script>
