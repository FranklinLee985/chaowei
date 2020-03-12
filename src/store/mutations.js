import {
  UPDATE_FILE_CACHE_READY,
  UPDATE_LANGUAGE,
  UPDATE_HEAD,
  UPDATE_LOADING,
  UPDATE_FOOTER,
  UPDATE_PAGE_TITLE,
  UPDATE_USER_INFO,
  UPDATE_PEN_CONNECTED,
  UPDATE_PEN_STATUS,
  LOGIN,
  LOGOUT,
  HOST
} from './data'

import Vue from 'vue'
//import CubeUI from 'cube-ui'                      // cube-ui界面组件库 //jarong

const state = {
  fileCacheReady: false, // 文件缓存是否就绪
  headShow: true,
  loading: false,
  footerShow: true,
  pageTitle: '首页',
  host: HOST,

  // 保存在localStorage中的信息：
  language: localStorage.getItem('language'), // 当前本地化语言

  penConnected: localStorage.getItem('penConnected'), // 当前连接的笔
  penStatus: false, // 笔是否连接

  // 用户登录信息
  uid: localStorage.getItem('uid'), // 用户ID
  user: localStorage.getItem('user'), // 用户名
  password: localStorage.getItem('password'), // 用户密码
  csrfToken: localStorage.getItem('csrfToken'), // 登录成功后获得的令牌
  logoutToken: localStorage.getItem('logoutToken'), // 登出时的令牌
  loginData: localStorage.getItem('loginData'), // 登录成功后获得的所有数据

  userNickname: localStorage.getItem('userNickname'), // 昵称
  userPicture: localStorage.getItem('userPicture'), // 头像
  userBirthday: localStorage.getItem('userBirthday'), // 生日
  userSex: localStorage.getItem('userSex'), // 性别
  userCity: localStorage.getItem('userCity'), // 城市
  userEmail: localStorage.getItem('userEmail'), // 电邮
  userTimezone: localStorage.getItem('userTimezone'), // 时区
  userLangcode: localStorage.getItem('userLangcode') // 语言
}

const mutations = {
  [UPDATE_FILE_CACHE_READY] (state, type) {
    state.fileCacheReady = type
  },
  [UPDATE_LANGUAGE] (state, type) {
    // 根据读取到的本地设置，切换语言
    if (/zh-CN/.test(type)) {
      state.language = 'zh-CN'
      Vue.i18n.set('zh-CN')
      //CubeUI.Locale.use('zh-CN')
    } else if (/zh-HK/.test(type)) {
      state.language = 'zh-HK'
      Vue.i18n.set('zh-HK')
      //CubeUI.Locale.use('zh-HK')
    } else if (/en-US/.test(type)) {
      state.language = 'en-US'
      Vue.i18n.set('en-US')
      //CubeUI.Locale.use('en-US')
    } else {
      state.language = 'en-US'
      Vue.i18n.set('en-US')
      //CubeUI.Locale.use('en-US')
    }

    localStorage.setItem('language', state.language)
  },
  // head
  [UPDATE_HEAD] (state, type) {
    state.headShow = type
  },
  // loading
  [UPDATE_LOADING] (state, type) {
    state.loading = type
  },
  // footer
  [UPDATE_FOOTER] (state, type) {
    state.footerShow = type
  },
  // title
  [UPDATE_PAGE_TITLE] (state, type) {
    state.pageTitle = type
  },
  // 用户信息
  [UPDATE_USER_INFO] (state, data) {
    state.userNickname = data.userNickname
    state.userPicture = data.userPicture
    state.userBirthday = data.userBirthday
    state.userSex = data.userSex
    state.userCity = data.userCity
    state.userEmail = data.userEmail
    state.userTimezone = data.userTimezone
    state.userLangcode = data.userLangcode

    // 保存到永久存储
    localStorage.setItem('userNickname', state.userNickname)
    localStorage.setItem('userPicture', state.userPicture)
    localStorage.setItem('userBirthday', state.userBirthday)
    localStorage.setItem('userSex', state.userSex)
    localStorage.setItem('userCity', state.userCity)
    localStorage.setItem('userEmail', state.userEmail)
    localStorage.setItem('userTimezone', state.userTimezone)
    localStorage.setItem('userLangcode', state.userLangcode)
  },
  // 连接的笔信息
  [UPDATE_PEN_CONNECTED] (state, data) {
    state.penConnected = JSON.stringify(data)
    localStorage.setItem('penConnected', state.penConnected)
  },
  // 笔连接状态
  [UPDATE_PEN_STATUS] (state, data) {
    state.penStatus = data
  },
  // 登录
  [LOGIN] (state, data) {
    console.log('store: login')

    state.uid = data.uid
    state.user = data.user
    state.password = data.password
    state.csrfToken = data.csrfToken
    state.logoutToken = data.logoutToken
    state.loginData = data.loginData

    // 保存到永久存储
    localStorage.setItem('uid', data.uid)
    localStorage.setItem('user', data.user)
    localStorage.setItem('password', data.password)
    localStorage.setItem('csrfToken', data.csrfToken)
    localStorage.setItem('logoutToken', data.logoutToken)
    localStorage.setItem('loginData', data.loginData)
  },
  // 登出
  [LOGOUT] (state) {
    console.log('store: logout')

    // 从内存中清除用户信息
    state.uid = null
    state.user = null
    state.password = null
    state.csrfToken = null
    state.logoutToken = null
    state.loginData = null

    state.userNickname = null
    state.userPicture = null
    state.userBirthday = null
    state.userSex = null
    state.userCity = null
    state.userEmail = null
    state.userTimezone = null
    state.userLangcode = null

    state.userNickname = null
    state.userPicture = null
    state.userBirthday = null
    state.userSex = null
    state.userCity = null
    state.userEmail = null
    state.userTimezone = null
    state.userLangcode = null

    // 从永久存储中清除
    localStorage.removeItem('uid')
    localStorage.removeItem('user')
    localStorage.removeItem('password')
    localStorage.removeItem('csrfToken')
    localStorage.removeItem('logoutToken')
    localStorage.removeItem('loginData')

    localStorage.removeItem('userNickname')
    localStorage.removeItem('userPicture')
    localStorage.removeItem('userBirthday')
    localStorage.removeItem('userSex')
    localStorage.removeItem('userCity')
    localStorage.removeItem('userEmail')
    localStorage.removeItem('userTimezone')
    localStorage.removeItem('userLangcode')
  }
}

// 获取全局数据
const getters = {
  fileCacheReady (state) {
    return state.fileCacheReady
  },
  language (state) {
    return state.language
  },
  headShow (state) {
    return state.headShow
  },
  loading (state) {
    return state.loading
  },
  footerShow (state) {
    return state.footerShow
  },
  pageTitle (state) {
    return state.pageTitle
  },
  userInfo (state) {
    return {
      'uid': state.uid,
      'user': state.user,
      'password': state.password,
      'csrfToken': state.csrfToken,
      'logoutToken': state.logoutToken,
      'loginData': state.loginData,
      'userNickname': state.userNickname,
      'userPicture': state.userPicture,
      'userBirthday': state.userBirthday,
      'userSex': state.userSex,
      'userCity': state.userCity,
      'userEmail': state.userEmail,
      'userTimezone': state.userTimezone,
      'userLangcode': state.userLangcode
    }
  },
  penConnected (state) {
    if (state.penConnected == null || state.penConnected == '') {
      state.penConnected = '[]'
    }
    return JSON.parse(state.penConnected)
  },
  penStatus (state) {
    return state.penStatus
  },
  isLogin (state) {
    return state.csrfToken != null
  },
  host(state) {
    return state.host
  }
}

export default {
  state,
  mutations,
  getters
}
