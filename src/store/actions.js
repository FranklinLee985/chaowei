import * as data from './data'

export default {

  UPDATE_LANGUAGE: ({commit}) => {
    commit(data.UPDATE_LANGUAGE)
  },
  UPDATE_HEAD: ({commit}) => {
    commit(data.UPDATE_HEAD)
  },
  UPDATE_LOADING: ({commit}) => {
    commit(data.UPDATE_LOADING)
  },
  UPDATE_FOOTER: ({commit}) => {
    commit(data.UPDATE_FOOTER)
  },
  UPDATE_PAGE_TITLE: ({commit}) => {
    commit(data.UPDATE_PAGE_TITLE)
  },
/*  UPDATE_PEN_CONNECTED: ({commit}) => {
    commit(data.UPDATE_CONNECTED_PEN)
  },
  UPDATE_PEN_STATUS: ({commit}) => {
    commit(data.UPDATE_PEN_STATUS)
  },
  */
}
