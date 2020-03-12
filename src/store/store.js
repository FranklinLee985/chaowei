import Vue from 'vue'
import Vuex from 'vuex'
import mutations from './mutations'
import actions from './actions'

Vue.use(Vuex)

// 参考https://vuex.vuejs.org/zh/guide/
// 参考https://www.jianshu.com/p/5624362cd1f4
export default new Vuex.Store({
  modules: {
    mutations
  },
  actions
})
