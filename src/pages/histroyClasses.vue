<template>

  <f7-page name="histroyClasses">


    <!-- Top Navbar -->
    <f7-navbar  title="上課曆史"　back-link="Back" >



    </f7-navbar>




            <f7-list media-list>


                    <f7-list-item v-for="item in this.classesFinish"
                                  :title = "item.title"
                                  :after = "item.datetime"
                                  v-bind:key="item.id">
                    </f7-list-item>

            </f7-list>



  </f7-page>
</template>

<style>
    .md .icon-back, .md .icon-forward, .md .icon-next, .md .icon-prev {
        width: 24px;
        height: 24px;
        color: white;
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
      color: var(--f7-list-item-title-text-color);
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

        classesFinish: [],
      }
    },
    methods: {
      alertMessage(message)
      {
        this.$f7.dialog.alert(message, () => {

        })
      },

      sortClass(a,b) {  //排序函数
            return(a.begintime.localeCompare(b.begintime));
        },
        sortClasses()
        {

            this.classesFinish.sort(this.sortClass);
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
                            let showdatetime = showdate + ' ' + showtime
                            if (classStatus == 0) {

                            this.classesFinish.push({
                                roomid: theitem.rID,
                                password: '',
                                title: theitem.course_title,
                                begintime: theitem.start,
                                endtime: theitem.end,
                                date: showdate,
                                time: showtime,
                                datetime: showdatetime,
                                status: 0
                            })
                        }

                        }
                    }

                  this.sortClasses()


                },
                error: (response) => {
                    // 登录成功后，回首页
                    console.log('get class book failed:')
                }
            })
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
	},


    mounted() {
        let myThis = this
        if (myThis.userInfo.csrfToken != null) {
            this.getClassList()

        }
    }
  }
</script>
