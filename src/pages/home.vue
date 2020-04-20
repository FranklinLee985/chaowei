<template>

  <f7-page pull-to-refresh @ptr:refresh="refreshClass" name="home">


    <!-- Top Navbar -->
    <f7-navbar  title="我的課堂"　:sliding="false" >

        <div class="navbar-inner">


            <div class="left">


            </div>
            <div class = "middle">

            </div>

            <div class="right">

                <div class="user-gravatar32">
                    <a @click="getClassList">

                        <div class="gravatar-wrapper-32">
                            <svg height="16pt" viewBox="0 0 512 512" width="16pt" xmlns="http://www.w3.org/2000/svg"><path d="m61.496094 279.609375c-.988282-8.234375-1.496094-16.414063-1.496094-23.609375 0-107.402344 88.597656-196 196-196 50.097656 0 97 20.199219 131.5 51.699219l-17.300781 17.601562c-3.898438 3.898438-5.398438 9.597657-3.898438 15 1.800781 5.097657 6 9 11.398438 10.199219 3.019531.605469 102.214843 32.570312 95.898437 31.300781 8.035156 2.675781 19.917969-5.894531 17.703125-17.699219-.609375-3.023437-22.570312-113.214843-21.300781-106.902343-1.199219-5.398438-5.101562-9.898438-10.5-11.398438-5.097656-1.5-10.800781 0-14.699219 3.898438l-14.699219 14.398437c-45.300781-42.296875-107.503906-68.097656-174.101562-68.097656-140.699219 0-256 115.300781-256 256v.597656c0 8.457032.386719 14.992188.835938 19.992188.597656 6.625 5.480468 12.050781 12.003906 13.359375l30.816406 6.160156c10.03125 2.007813 19.050781-6.402344 17.839844-16.5zm0 0" fill='#ffffff'/><path d="m499.25 222.027344-30.90625-6.296875c-10.042969-2.046875-19.125 6.371093-17.890625 16.515625 1.070313 8.753906 1.546875 17.265625 1.546875 23.753906 0 107.398438-88.597656 196-196 196-50.097656 0-97-20.199219-131.5-52l17.300781-17.300781c3.898438-3.898438 5.398438-9.597657 3.898438-15-1.800781-5.101563-6-9-11.398438-10.199219-3.019531-.609375-102.214843-32.570312-95.898437-31.300781-5.101563-.898438-10.203125.601562-13.5 4.199219-3.601563 3.300781-5.101563 8.699218-4.203125 13.5.609375 3.019531 22.574219 112.210937 21.304687 105.898437 1.195313 5.402344 5.097656 9.902344 10.496094 11.398437 6.261719 1.570313 11.488281-.328124 14.699219-3.898437l14.402343-14.398437c45.296876 42.300781 107.5 69.101562 174.398438 69.101562 140.699219 0 256-115.300781 256-256v-.902344c0-6.648437-.242188-13.175781-.796875-19.664062-.570313-6.628906-5.433594-12.074219-11.953125-13.40625zm0 0" fill='#ffffff'/></svg>

                        </div>
                    </a>
                </div>


            </div>

        </div>

        <!--<f7-button  ref="toastDemo" @click="toastDemo">"Toast"</f7-button> -->





    <!--
      <f7-nav-right>
        <f7-link icon-ios="f7:menu" icon-aurora="f7:menu" icon-md="material:menu" panel-open="right"></f7-link>
      </f7-nav-right>
      -->

    </f7-navbar>




            <f7-list media-list>




                    <f7-list-item v-for="item in this.classesPending"
                                  :title = "item.title"
                                  :subtitle = "item.subtitle"
                                  :text = "item.datetime"
                                  :after="item.prompt"
                                  v-bind:key="item.id">
                        <f7-button slot="media" ref="joinroom" @click="joinRoomWithItem(item)"   fill  :color="item.color"　round >&nbsp&nbsp&nbsp{{(userInfo.userRole == 'administrator')?'巡課':'上課'}}&nbsp&nbsp&nbsp</f7-button>
                    </f7-list-item>

            </f7-list>

      <f7-block>

      <i class="f7-icons">home</i>
      </f7-block>
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





  </f7-page>
</template>

<style>

  .gravatar-wrapper-32, .gravatar-wrapper-32 img {
      padding-top: 8px;
    width: 64px;
    height: 32px;
  }
  .user-info .user-gravatar32 {
    float: left;
    width: 64px;
    height: 32px;
    border-radius: 1px;
  }


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
    padding-top: 30px;
    padding-bottom: 5px;
  }
	.list .item-title {
	  min-width: 0;
	  flex-shrink: 1;
	  white-space: var(--f7-list-item-title-white-space);
	  position: relative;
	  overflow: hidden;
	  text-overflow: ellipsis;
	  max-width: 100%;
	  font-size: var(--f7-list-item-title-font-size);
	  font-weight: var(--f7-list-item-title-font-weight);
	
	  color: var(--f7-list-item-title-text-color);
	  line-height: var(--f7-list-item-title-line-height);
	}
  .list .item-after {
      white-space: nowrap;
      flex-shrink: 0;
      display: flex;
      font-size: var(--f7-list-item-after-font-size);
      font-weight: var(--f7-list-item-after-font-weight);
      color: #FE6714;
      line-height: var(--f7-list-item-after-line-height);
      margin-left: auto;
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
        'loading',        'fileCacheReady',
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



        classesPending: [],
      classesFinish: [],
          
        // Login screen data
        username: '',
        password: '',
        userid:'',
        dispuser:'',

        loginStr:'Login',
        adminLogined:false,
        lastMsFrom1970: 0,

          timer: null,
         // lessonStr:'上課'
        //jarong add end
      }
    },
    methods: {
      alertMessage(message)
      {
          talkplus.showToast(message)
          /*
        this.$f7.dialog.alert(message, () => {

        })
          */
      },



        sortClass(a,b) {  //排序函数
            return(a.begintime.localeCompare(b.begintime));
        },
        sortClasses()
        {
            this.classesPending.sort(this.sortClass);
            this.classesFinish.sort(this.sortClass);
        },

		joinRoomTeacher()
		{
			talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=1234&serial=601509809&nickname=teacher&userrole=0");
            //talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=9999&serial=601509809&nickname=teacher&userrole=1");
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
            // let datetime =   this.getDatetimeValue(myDate)
            let datetime = myDate.getTime()
            let foundItem = null
            for (let item in this.classesPending)
            {
                let beginTime = new Date(this.classesPending[item].begintime)
                let endTime = new Date(this.classesPending[item].endtime)
                let beginVal = beginTime.getTime()
                let endVal = endTime.getTime()

                //let beginVal = this.getDatetimeValue(beginTime)
                ///let endVal = this.getDatetimeValue(endTime)
                beginVal = beginVal - 15*60*1000
                if (datetime >= beginVal && datetime <= endVal)
                {
                    foundItem = this.classesPending[item]

                    break;
                }
                //diff = items.datetime
            }
            return foundItem

        },
        colorLevelToString(level)
        {
            let str = ''
            switch(level)
            {
                case 1:
                    str  = 'orange'
                    break
                case 2:
                    str  = 'yellow'
                    break
                case 3:
                    str = 'green'
                    break
                default:
                    str = 'gray'
                    break

            }
            return str
        },
        checkItemColorLevel(classBegin, classEnd)
        {
            let myDate = new Date();
            let datetime = myDate.getTime()

            let beginTime = new Date(classBegin)
            let endTime = new Date(classEnd)
            let beginVal = beginTime.getTime()
            let endVal = endTime.getTime()
            let checkVal = beginVal
            let level = 0 //gray


            if (datetime >= checkVal && datetime <= endVal)
            {
                level = 3 //green
            }
            else
            {
                checkVal = beginVal -  10*60*1000
                if (datetime >= checkVal && datetime <= endVal)
                    level = 2 //orange
                else
                {
                    checkVal = beginVal -  15*60*1000
                    if (datetime >= checkVal && datetime <= endVal)
                        level = 1 // yellow
                }
            }
            return level
        },
        checkIfClassCommingSoon(item)
        {
            let myDate = new Date();
             let datetime = myDate.getTime()

                let beginTime = new Date(item.begintime)
                let endTime = new Date(item.endtime)
                let beginVal = beginTime.getTime()
                let endVal = endTime.getTime()

                beginVal = beginVal - 15*60*1000
                if (datetime >= beginVal && datetime <= endVal)
                {
                    return true
                }
                else return false

        },
        promptClass(classBegin, classEnd)
        {
            let myDate = new Date();
            let datetime = myDate.getTime()

            let beginTime = new Date(classBegin)
            let endTime = new Date(classEnd)
            let beginVal = beginTime.getTime()
            let endVal = endTime.getTime()
            let checkVal = beginVal
            let prompt = ''


            if (datetime >= checkVal && datetime <= endVal)
            {
                prompt = '上課中'
            }
            else
            {
                checkVal = beginVal -  30*60*1000
                if (datetime >= checkVal && datetime <= endVal) {
                    let diff =  (beginVal - datetime)/60/1000
                    diff = parseInt(diff)
                    if (diff >0)
                        prompt = diff + '分鍾後上課'
                    else
                        prompt = '馬上上課'
                }
                else
                {
                    prompt = ''
                }
            }
            return prompt
        },
        joinRoomWithItem(item)
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
                this.alertMessage('無法獲得用戶的身份.')
                return
            }

            if (this.userInfo.userRole.indexOf("student") != -1)
                role = 2
            else if (this.userInfo.userRole.indexOf("teacher")!= -1)
                role = 0
            else if (this.userInfo.userRole.indexOf("administrator")!= -1)
                role = 4

            if (role == -1)
            {
                this.alertMessage('該用戶身份不能進入房間.')
                return
            }


            if (this.checkIfClassCommingSoon(item) == false)
            {
               // talkplus.showToast("上課時間沒到");
                this.alertMessage('未到上課時間.')
                return
            }



                str = 'chaoweiclass://?host=global.talk-cloud.net'
                if (role == 0) //teacher need a default password
                {
                    if (item.password =='')
                    {
                        item.password ='1234'

                    }
                    str+='&password='+item.password
                }
                else if (role == 4)
                {
                    item.password ='abcd'
                    str+='&password='+item.password
                }

                str += '&serial='+item.roomid
                str += '&nickname='+nickname
                str += '&userrole='+role
                //str += '&nickname=tom&userrole=2'
                //  talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=56789&serail=1346975754&nickname=tom&userrole=2");
                //talkplus.showToast(str);
               // this.alertMessage(str)
                talkplus.joinRoom(str);

                //  talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=1234&serial=545718172&nickname=teacher&userrole=0");

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
              this.alertMessage('無法獲得用戶的身份.')
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
              talkplus.joinRoom(str);

            //  talkplus.joinRoom("chaoweiclass://?host=global.talk-cloud.net&password=1234&serial=545718172&nickname=teacher&userrole=0");
            }
            else
              this.alertMessage("未到上課時間");
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
       refreshClass()
       {
         this.getClassList()
     //    this.sortClasses()
       },
        getClassList()
        {
            let myThis = this
            let isTeacher= false

            if (this.userInfo.userRole == "teacher")
                isTeacher = true

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
                           let showdate = this.getDateStrFromDateObject(beginTime)
                            let showtime = this.getTimeStrFromDateObject(beginTime)
                            let showdatetime = showdate +' ' + showtime
                            let substitleStr = ''
                            if (isTeacher)
                            {
                                substitleStr = theitem.name
                            }
                          if (classStatus == 1) {
                              let colorlevel =  this.checkItemColorLevel(beginTime,endTime)
                              let colorstr = this.colorLevelToString(colorlevel)
                              let promptstr =  this.promptClass(beginTime,endTime)
                              this.classesPending.push({
                              roomid:  theitem.rID,
                              password: '',
                              title: theitem.course_title,
                              begintime: theitem.start,
                              endtime: theitem.end,
                              date:showdate,
                              time:showtime,
                                datetime:showdatetime,
                               color:colorstr,
                                  prompt:promptstr,
                               subtitle:  substitleStr,
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
                                datetime:showdatetime,
                              status: 0
                            })

                          }
                        }
                    }

                  this.sortClasses()


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
          this.$store.commit('UPDATE_LOGIN_SCREEN_STATUS', true)
          //this.loginScreenOpened = true

        }
          this.adminLogined = false
      },

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
      },
 processDialogs()
      {
        var f7 =  this.$f7;
        const $ = f7.$;
       // if (f7.device.electron) return true;

        if ($('.actions-modal.modal-in').length) {
          f7.actions.close('.actions-modal.modal-in');
        //  e.preventDefault();
          return true;
        }
        if ($('.dialog.modal-in').length) {
          f7.dialog.close('.dialog.modal-in');
        //  e.preventDefault();
          return true;
        }
        if ($('.sheet-modal.modal-in').length) {
          f7.sheet.close('.sheet-modal.modal-in');
         // e.preventDefault();
          return true;
        }
        if ($('.popover.modal-in').length) {
          f7.popover.close('.popover.modal-in');
        //  e.preventDefault();
          return true;
        }
        if ($('.popup.modal-in').length) {
          if ($('.popup.modal-in>.view').length) {
            const currentView = f7.views.get('.popup.modal-in>.view');
            if (currentView && currentView.router && currentView.router.history.length > 1) {
              currentView.router.back();
            //  e.preventDefault();
              return true;
            }
          }
          f7.popup.close('.popup.modal-in');
         // e.preventDefault();
          return true;
        }
        if ($('.login-screen.modal-in').length) {
          f7.loginScreen.close('.login-screen.modal-in');
        //  e.preventDefault();
            this.exitApp();
          return true;
        }

        if($('.searchbar-enabled').length){
          f7.searchbar.disable();
       //   e.preventDefault();
          return true;
        }

        const currentView = f7.views.current;
        if (currentView && currentView.router && currentView.router.history.length > 1) {
          currentView.router.back();
        //  e.preventDefault();
          return true;
        }


        if ($('.panel.panel-in').length) {
          f7.panel.close('.panel.panel-in');
        //  e.preventDefault();
          return true;
        }
        return false
    },

  onBackKeyDown() {
    if (this.processDialogs() == true)
      return
  var d = new Date()
  var nowMsFrom1970 =  d.getTime()
  let diff = nowMsFrom1970 - this.lastMsFrom1970
  if (diff > 2000) {
    this.lastMsFrom1970 = nowMsFrom1970
    talkplus.showToast('再點擊一次退出!')
  }
  else
    this.exitApp()
},
   exitApp(){
    navigator.app.exitApp();
	},
	},
      destroyed () {
          // 每次离开当前界面时，清除定时器
          clearInterval(this.timer)
          this.timer = null
      },

    mounted() {
      let myThis = this
      if (myThis.userInfo.csrfToken!= null)
      {
        this.loginStr = "登出"
         // this.dispuser = myThis.userInfo.user
        this.getClassList()
      //  this.sortClasses()
      }
      else {
          this.loginStr = "登入"
          //this.dispuser = ''
      }

      document.addEventListener('backbutton', this.onBackKeyDown, false)
      document.addEventListener('refreshClass', this.refreshClass, false)

        if (this.timer)
            clearInterval(this.timer)
        else
            {
                this.timer = setInterval(()=>{
                    this.refreshClass();
                },30000)
            }

    }
  }
</script>
