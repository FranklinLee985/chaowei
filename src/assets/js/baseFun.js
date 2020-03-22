import Vue from 'vue'
import stores from '../../store/store'
import {AjaxPlugin, base64, Datetime} from '../../components/vux'

import CordovaPromiseFS from 'cordova-promise-fs' // Wraps the Cordova (and Chrome) File API in convenient functions (that return a Promise)
import CordovaFileCache from 'cordova-file-cache' // Super Awesome File Cache for Cordova Apps
import JsonCache from '../../plugins/jsoncache'   // 文件缓存

//import CryptoJS from 'crypto-js' // 用于计算myScript的hamc
//import Qs from 'qs'

Vue.use(AjaxPlugin)
Vue.use(base64)
Vue.use(JsonCache)

// 初始化文件缓存
let baseFileCache = new CordovaFileCache({
  fs: new CordovaPromiseFS({ // An instance of CordovaPromiseFS is REQUIRED
    Promise: Promise, // <-- your favorite Promise lib (REQUIRED)
    storageSize: 100 * 1024 * 1024
  }),
  mode: 'hash', // 'hash' or 'mirror', optional
  localRoot: 'data', // optional
  serverRoot: 'http://47.244.236.113/oss/?file=', // optional, required on 'mirror' mode
  retry: [], // 失败后重试的间隔毫秒数，数组的长度表示重试次数
  cacheBuster: false  // optional
})

baseFileCache.ready.then(function (list) {
  // Promise when cache is ready.
  // Returns a list of paths on the FileSystem that are cached.
  console.log('fileCache is ready, list:', list)

  // 加大存储空间。在browser中会出现quotas限制，在Android手机上没有出现。
  //window.initPersistentFileSystem(200 * 1024 * 1024)
  //baseFileCache.clear() // 测试

  // 创建json缓存实例
  Vue.prototype.$jsonCache = new Vue.prototype.JsonCache(baseFileCache)

  // 通知文件系统准备就绪
  stores.commit('UPDATE_FILE_CACHE_READY', true)
})

let cacheDownlodQueue = [] // 下载任务栈
let cacheDownlodMaxID = 0 // 目前分配给下载任务的最大ID值

// 用于全局的ajax请求
const baseAjax = function (param) {
  let defaultParam = {
    data: param.data || {},
    params: param.params || {},
    url: param.url,
    type: param.type || 'get',
    auth: param.auth,
    timeout: param.timeout || 50000,
    showLoading: false,
    headers: param.headers,
    baseURL: param.baseURL || stores.getters.host
  }

  if (param.showLoading) {
    stores.commit('UPDATE_LOADING', true)
  }

  AjaxPlugin.$http.request({
    baseURL: defaultParam.baseURL,
    method: defaultParam.type,
    url: defaultParam.url,
    data: defaultParam.data,
    params: defaultParam.params,
    timeout: defaultParam.timeout,
    headers: defaultParam.headers,
    auth: defaultParam.auth
  }).then(function (response) {
    stores.commit('UPDATE_LOADING', false)
    param.success(response)
  }).catch(function (response) {
    stores.commit('UPDATE_LOADING', false)
    param.error(response)
  })
}

// 用于全局的公共函数
const baseFun = {
  // 本地存储
  localSet(obj, key) {
    var newObj = obj
    var str = JSON.stringify(newObj)
    localStorage.setItem(key, str)
  },
  localGet(key) {
    var str = localStorage.getItem(key)
    return JSON.parse(str)
  },
  localClear(key) {
    localStorage.removeItem(key)
  },

  getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
    var r = window.location.search.substr(1).match(reg)
    if (r != null) return decodeURI(r[2])
    return null
  },
  // 日期转年龄
  getAge(birthday) {
    let start = new Date(birthday.replace(/-/g, "/"));
    let dateNow = new Date();
    let year = dateNow.getFullYear() - start.getFullYear() -
        (dateNow.getMonth() < start.getMonth() ||
        (dateNow.getMonth() == start.getMonth() &&
            dateNow.getDate() < start.getDate())
            ? 1 : 0);
    let month = dateNow.getMonth() < start.getMonth()
        ? 12 - (start.getMonth() - dateNow.getMonth()) : dateNow.getMonth() - start.getMonth()

    return [year, month]
  },
  // base64字符串转二进制
  base64toBlob(base64, type) {
    // 将base64转为Unicode规则编码
    let bstr = atob(base64, type)
    let n = bstr.length
    let u8arr = new Uint8Array(n)
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n) // 转换编码后才可以使用charCodeAt 找到Unicode编码
    }
    return new Blob([u8arr], {
      type,
    })
  },

  /////////////////////////////////////////////////////////////////////////
  // 与服务器提供的REST API 进行AJAX通讯。
  /////////////////////////////////////////////////////////////////////////

  // 用户登录服务器
  ajaxUserLogin(param) {
    let user = param.user
    let password = param.password
    let success = param.success
    let error = param.error

    // 从drpual服务器返回的真实数据.
    // {
    //   "current_user": {
    //     "uid": "1",
    //     "roles": ["authenticated",
    //     "administrator"],
    //     "name": "admin"
    //   },
    //   "csrf_token": "y340UkiIwPueKHQ5gIGrFL13oG8mKWOIxz5Hvi8KU4k",
    //   "logout_token": "B8qDRvPmxUazAg7kzuxX8oQH6SmfRsKYgTbWvaqhWjA"
    // }

    // 发起登录请求
    let dataStr = '{"name":"' + user + '","pass":"' + password + '"}'

    baseAjax({
      url: '/user/login?_format=json',
      type: 'post',
      data: dataStr,
      headers: {
        'Content-Type': 'application/json'
      },
      showLoading: true,
      success: (response) => {
        // 保存返回的数据到全局store。
        stores.commit('LOGIN', {
          uid: response.data.current_user.uid,
          user: response.data.current_user.name,
          password: password,
          csrfToken: response.data.csrf_token,
          logoutToken: response.data.logout_token,
          loginData: JSON.stringify(response.data)
        })

        success(response)
      },
      error: error
    })
  },
  // 从服务器获取用户信息
  ajaxUserInfo(param) {
    let uid = param.uid
    let user = param.user
    let password = param.password
    let success = param.success
    let error = param.error

    baseAjax({
      url: '/user/' + uid + '?_format=json',
      type: 'get',
      headers: {
        'Content-Type': 'application/json'
      },
      auth: {
        username: user,
        password: password
      },
      showLoading: true,
      success: success,
      error: error
    })
  },
  // 从服务器获取用户信息，并下载头像。
  ajaxUserInfoExt(param) {
    //if (stores.getters.fileCacheReady) {
    if (true){
      let uid = param.uid
      let user = param.user
      let password = param.password
      let success = param.success
      let error = param.error

      this.ajaxUserInfo({
        uid: uid,
        user: user,
        password: password,
        success: (response) => {
          console.log('User information success:', JSON.stringify(response.data))

          // 保存用户信息数据到全局store。
          let data = response.data
          let userInfo = {
            userNickname: (typeof data.field_nickname[0] != "undefined") ? data.field_nickname[0].value : '',
            userPicture: (typeof data.user_picture[0] != "undefined") ? data.user_picture[0].url : '',
           // userBirthday: (typeof data.field_birthday[0] != "undefined") ? data.field_birthday[0].value : '',
            //userSex: (typeof data.field_sex[0] != "undefined") ? data.field_sex[0].value : '',
            //userCity: (typeof data.field_city[0] != "undefined") ? data.field_city[0].value : '',
            userEmail: (typeof data.mail[0] != "undefined") ? data.mail[0].value : '',
            userTimezone: (typeof data.timezone[0] != "undefined") ? data.timezone[0].value : '',
            userLangcode: (typeof data.langcode[0] != "undefined") ? data.langcode[0].value : ''
          };

          if (userInfo.userPicture != stores.getters.userInfo.userPicture) {
            // TODO: 头像发生变化，清除缓存中的头像。
          }

          // 更新用户信息
          stores.commit('UPDATE_USER_INFO', userInfo)

          // 没有头像数据，直接返回。
          if (userInfo.userPicture === '') {
            param.success(response)
            return
          }

          // 下载用户头像
          let value = userInfo.userPicture // 从服务器拿到已经是URL编码符地址
          let url = baseFileCache.get(value)
          let iUrl = baseFileCache.toInternalURL(url)
          if (url === value) {
            // 头像文件没有缓存，加入队列下载。

            // 清空下载队列
            baseFileCache.getAdded().length = 0
            baseFileCache.abort()

            baseFileCache.add(value)
            baseFileCache.download().then(function (cache) {
              console.log('User picture download success:', cache)

              // 文件已经缓存，替换成内部的url。
              if (window.device.platform === 'browser') {
                userInfo.userPicture = baseFileCache.toURL(url)
              } else {
                // 非浏览器中直接显示本地图片
                userInfo.userPicture = iUrl
              }

              // 更新用户信息(用户头像已缓存)
              stores.commit('UPDATE_USER_INFO', userInfo)

              success(response)
            }, function (failedDownloads) {
              console.log('User picture download fail:', failedDownloads)
              success(response)
            })
          } else {
            // 文件已经缓存，替换成内部的url。
            if (window.device.platform === 'browser') {
              userInfo.userPicture = baseFileCache.toURL(url)
            } else {
              // 非浏览器中直接显示本地图片
              userInfo.userPicture = iUrl
            }

            // 更新用户信息
            stores.commit('UPDATE_USER_INFO', userInfo)

            success(response)
          }
        },
        error: (response) => {
          console.log('User information fail')
          error(response)
        }
      })
    }
  },
  // 从服务器退出登录
  ajaxUserLogout(param) {
    let success = param.success
    let error = param.error
	let logout_token = param.logout_token
    baseAjax({
      url: '/user/logout?_format=json&token='+ logout_token,
       //url: '/user/logout',
      type: 'post',

      headers: {
        'Content-Type': 'application/json'
      },
      showLoading: true,
      success: (response) => {
        // 退出登录。
        stores.commit('LOGOUT')
        success(response)
      },
      error: (response) => {
        if (response.response) {
          stores.commit('LOGOUT')
        }
        error(response)
      }
    })
  },
  // 修改服务器用户信息
  ajaxUserInfoUpload(param) {
    let uid = param.uid
    let user = param.user
    let password = param.password
    let csrfToken = param.csrfToken
    let userInfo = param.userInfo
    let success = param.success
    let error = param.error

    let data = {
      "_links": {
        "type": {
          "href": stores.getters.host + "rest/type/user/user"
        }
      },

      "field_nickname": [
        {
          "value": userInfo.userNickname,
        },
      ],
      "field_sex": [
        {
          "value": userInfo.userSex,
        },
      ],
      "field_birthday": [
        {
          "value": userInfo.userBirthday,
          "format": "Y-m-d"
        },
      ],
      "field_city": [
        {
          "value": userInfo.userCity
        }
      ]
    }
    let dataStr = JSON.stringify(data)

    baseAjax({
      data: dataStr,
      url: '/user/' + uid + '?_format=hal_json',
      type: 'patch',
      headers: {
        'Content-Type': 'application/hal+json',
        'X-CSRF-Token': csrfToken,
        'Authorization': 'Basic ' + base64.encode(user + ':' + password)
      },
      showLoading: true,
      success: (response) => {
        // 更新用户信息
        stores.commit('UPDATE_USER_INFO', userInfo)
        success(response)
      },
      error: error
    })
  },
  // 上传并修改用户头像到服务器。
  ajaxUserPictureUpload(param) {
    let uid = param.uid
    let user = param.user
    let password = param.password
    let csrfToken = param.csrfToken
    let image = param.image
    let success = param.success
    let error = param.error

    // 上传图片
    baseAjax({
      data: image,
      url: '/file/upload/user/user/user_picture?_format=json',
      type: 'post',
      headers: {
        'Content-Type': 'application/octet-stream',
        'Content-Disposition': 'file; filename="user_picture_' + user + '.jpg"',
        'X-CSRF-Token': csrfToken
      },
      auth: {
        username: user,
        password: password
      },
      showLoading: true,
      success: (response) => {
        // 将上传的图片指定为用户的头像图片
        let targetID = response.data.fid[0].value
        let data = {
          "_links": {
            "type": {
              "href": stores.getters.host + "rest/type/user/user"
            }
          },
          "user_picture": [
            {
              "target_id": targetID
            }
          ]
        }
        let dataStr = JSON.stringify(data)

        baseAjax({
          data: dataStr,
          url: '/user/' + uid + '?_format=hal_json',
          type: 'patch',
          headers: {
            'Content-Type': 'application/hal+json',
            'X-CSRF-Token': csrfToken,
            'Authorization': 'Basic ' + base64.encode(user + ':' + password)
          },
          showLoading: true,
          success: (response) => {
            success(response)
          },
          error: error
        })
      },
      error: error
    })
  },
  // 用户注册
  ajaxUserRegister(param) {
    let user = param.user
    let password = param.password
    let email = param.email
    let success = param.success
    let error = param.error
    let data = {
      "_links": {
        "type": {
          "href": stores.getters.host + "rest/type/user/user"
        }
      },
      "langcode": [{"value": "en"}],
      "name": [{"value": user}],
      "mail": [{"value": email}],
      "pass": [{"value": password}]
    }
    let dataStr = JSON.stringify(data)

    baseAjax({
      url: '/user/register?_format=hal_json',
      type: 'post',
      data: dataStr,
      headers: {
        'Content-Type': 'application/hal+json'
      },
      showLoading: true,
      success: success,
      error: error
    })
  },
  // 忘记密码步骤1：向用户邮箱中发送临时密码。
  ajaxUserLostPasswordEmail(param) {
    let email = param.email
    let success = param.success
    let error = param.error
    let data = {"mail": email}
    let dataStr = JSON.stringify(data)

    baseAjax({
      url: '/user/lost-password?_format=json',
      type: 'post',
      data: dataStr,
      headers: {
        'Content-Type': 'application/json'
      },
      showLoading: true,
      success: success,
      error: error
    })
  },
  // 忘记密码步骤2：用邮件中的临时密码来设置新密码
  ajaxUserLostPasswordReset(param) {
    let user = param.user
    let tempPass = param.tempPass
    let newPass = param.newPass
    let success = param.success
    let error = param.error
    let data = {
      "name": user,
      "temp_pass": tempPass,
      "new_pass": newPass
    }
    let dataStr = JSON.stringify(data)

    baseAjax({
      url: '/user/lost-password-reset?_format=json',
      type: 'post',
      data: dataStr,
      headers: {
        'Content-Type': 'application/json'
      },
      showLoading: true,
      success: success,
      error: error
    })
  },



  ajaxUserGetRole(param) {
    let uid = param.uid
    let user = param.user
    let password = param.password
    let success = param.success
    let error = param.error

    baseAjax({
      url: '/api/v1.0/access?_format=json',
     //  url: '/api/v1.0/access?_format=json',
      type: 'get',
      headers: {
        'Content-Type': 'application/json'
      },
      auth: {
        username: user,
        password: password
      },
      showLoading: true,
      success: (response) => {
        stores.commit('UPDATE_USER_ROLE',  response.data.access[1])
        success(response)

      },
      error: error
    })
  },


  ajaxUserGetClassbook(param) {
    let uid = param.uid
    //let user = param.user
    //let password = param.password
    let success = param.success
    let error = param.error

    baseAjax({
      //url: '/api/v1.0/classbook/' + uid + '?_format=json',
      url: '/api/v1.0/classbook/0/?_format=json',
      //  url: '/api/v1.0/access?_format=json',
      type: 'get',
      headers: {
        'Content-Type': 'application/json'
      },
      showLoading: true,
      success: (response) => {
        success(response)

      },
      error: error
    })
  },
}

// 导出模块
  export default {
    baseAjax,
    baseFun,
    baseFileCache
  }
