/*! author:Mr Qiu , date:2018-12-13 */

"use strict";
(TK = TK || {}).EventDispatcher = function (i) {
    var s = {};
    return i.dispatcher = {}, i.dispatcher.eventListeners = {}, i.dispatcher.backupListerners = {}, s.addEventListener = function (e, o, t) {
      null != e && (void 0 === i.dispatcher.eventListeners[e] && (i.dispatcher.eventListeners[e] = []), i.dispatcher.eventListeners[e].push(o), t && (void 0 === i.dispatcher.backupListerners[t] && (i.dispatcher.backupListerners[t] = []), i.dispatcher.backupListerners[t].push({
        eventType: e,
        listener: o
      })))
    }, s.removeEventListener = function (e, o) {
      var t;
      i.dispatcher.eventListeners[e] ? -1 !== (t = i.dispatcher.eventListeners[e].indexOf(o)) && i.dispatcher.eventListeners[e].splice(t, 1) : L.Logger.info("[tk-fake-sdk]not event type: " + e)
    }, s.removeAllEventListener = function (e) {
      if ((a = e) && "object" == typeof a && "number" == typeof a.length && "function" == typeof a.splice && !a.propertyIsEnumerable("length"))
        for (var o in e) {
          var t = e[o];
          delete i.dispatcher.eventListeners[t]
        } else if ("string" == typeof e) delete i.dispatcher.eventListeners[e];
        else if ("object" == typeof e)
        for (var n in e) {
          t = n;
          var r = e[n];
          s.removeEventListener(t, r)
        }
      var a
    }, s.dispatchEvent = function (e, o) {
      var t;
      for (t in (o = null == o || o) && L.Logger.debug("[tk-fake-sdk]dispatchEvent , event type: " + e.type), i.dispatcher.eventListeners[e.type]) i.dispatcher.eventListeners[e.type].hasOwnProperty(t) && i.dispatcher.eventListeners[e.type][t](e)
    }, s.removeBackupListerner = function (e) {
      if (e && i.dispatcher.backupListerners[e]) {
        for (var o = 0; o < i.dispatcher.backupListerners[e].length; o++) {
          var t = i.dispatcher.backupListerners[e][o];
          s.removeEventListener(t.eventType, t.listener)
        }
        i.dispatcher.backupListerners[e].length = 0, delete i.dispatcher.backupListerners[e]
      }
    }, s
  }, TK.TalkEvent = function (e) {
    var o = {};
    return o.type = e.type, o
  }, TK.RoomEvent = function (e, o) {
    var t = TK.TalkEvent(e);
    if (t.streams = e.streams, t.message = e.message, t.user = e.user, o && "object" == typeof o)
      for (var n in o) t[n] = o[n];
    return t
  }, TK.StreamEvent = function (e, o) {
    var t = TK.TalkEvent(e);
    if (t.stream = e.stream, t.message = e.message, t.bandwidth = e.bandwidth, t.attrs = e.attrs, o && "object" == typeof o)
      for (var n in o) t[n] = o[n];
    return t
  }, TK.PublisherEvent = function (e, o) {
    var t = TK.TalkEvent(e);
    if (o && "object" == typeof o)
      for (var n in o) t[n] = o[n];
    return t
  }, TK.clientSdkEventManager = TK.EventDispatcher({}), TK.clientUICoreEventManager = TK.EventDispatcher({}), TK.mobileSdkEventManager = TK.EventDispatcher({}), TK.mobileUICoreEventManager = TK.EventDispatcher({}), (TK = TK || {}).SDKTYPE = void 0, TK.isOnlyAudioRoom = !1, TK.extendSendInterfaceName = "", TK.SDKNATIVENAME = void 0, TK.global = {
    fakeJsSdkInitInfo: {
      debugLog: !1,
      playback: !1,
      deviceType: void 0,
      mobileInfo: {
        isSendLogMessageToProtogenesis: !1,
        clientType: void 0
      }
    }
  }, TK.QtNativeClientRoom = function (e) {
    return TK.SDKNATIVENAME = "QtNativeClientRoom", TK.SDKTYPE = "pc", TK.Room(e)
  }, TK.MobileNativeRoom = function (e) {
    return TK.SDKNATIVENAME = "MobileNativeRoom", TK.SDKTYPE = "mobile", TK.Room(e)
  }, TK.Room = function (e) {
    if (TK.SDKNATIVENAME) {
      e = e || {}, TK.SDKVERSIONS = "2.2.1", TK.SDKVERSIONSTIME = "2018082114", L.Logger.info("[tk-sdk-version]sdk-version:" + TK.SDKVERSIONS + " , sdk-time: " + TK.SDKVERSIONSTIME);
      var n, m = TK.EventDispatcher({}),
        v = {},
        k = {},
        u = {},
        y = !1,
        E = {},
        h = void 0,
        b = void 0,
        w = void 0,
        S = void 0,
        T = void 0,
        I = void 0,
        R = void 0,
        _ = void 0,
        O = void 0,
        A = void 0,
        a = void 0,
        C = void 0,
        K = void 0,
        U = [],
        N = window.TK.ROOM_MODE.NORMAL_ROOM;
      if (m.socket = TK.fakeScoketIO(m), m.setLogIsDebug = function (e, o) {
          e = e || !1, void 0 !== o && (e = 0 === o);
          var t = {
              debug: e
            },
            n = {
              development: e,
              logLevel: void 0 !== o && "number" == typeof o ? o : e ? L.Constant.LOGLEVEL.DEBUG : L.Constant.LOGLEVEL.INFO
            },
            r = {
              webrtcLogDebug: e
            };
          TK.tkLogPrintConfig(t, n, r)
        }, m.getRoomProperties = function () {
          return u
        }, m.getUsers = function () {
          return k
        }, m.getUser = function (e) {
          if (void 0 !== e) return k[e]
        }, m.getMySelf = function () {
          return v
        }, m.changeExtendSendInterfaceName = function (e) {
          TK.extendSendInterfaceName = e
        }, m.startShareMedia = function (e, o, t, n) {
          if (e) {
            var r = {
              type: "media"
            };
            if (o = void 0 !== o && o, void 0 !== t && (r.toID = t), n && "object" == typeof n)
              for (var a in n) "toID" !== a && "type" !== a && (r[a] = n[a]);
            s("publishNetworkMedia", {
              audio: !0,
              video: o,
              url: e,
              attributes: r
            })
          } else L.Logger.error("[tk-fake-sdk]startShareMedia url can not be empty!")
        }, m.stopShareMedia = function () {
          s("unpublishNetworkMedia")
        }, m.onPageFinished = function () {
          s("onPageFinished")
        }, m.isPlayAudio = function (e, o, t) {
          s("isPlayAudio", {
            audio: !0,
            isPlay: o,
            url: e,
            video: t.type === 'video',
            fileid: t.fileid,
            type: t.type,
            attributes: t
          })
        }, m.pubMsg = function (e) {
          if ("object" == typeof e) {
            var o = {};
            o.name = e.name || e.msgName, o.id = e.id || e.msgId || o.name, o.toID = e.toID || e.toId || "__all", o.data = e.data, e.save || (o.do_not_save = ""), void 0 !== e.associatedMsgID && (o.associatedMsgID = e.associatedMsgID), void 0 !== e.associatedUserID && (o.associatedUserID = e.associatedUserID);
            var t = {};
            for (var n in e) void 0 === o[n] && void 0 !== e[n] && "save" !== n && "name" !== n && "msgName" !== n && "id" !== n && "msgId" !== n && "toID" !== n && "toId" !== n && "data" !== n && "associatedMsgID" !== n && "associatedUserID" !== n && (t[n] = e[n]);
            o.expandParams = t, s("pubMsg", o)
          } else L.Logger.error("[tk-sdk]pubMsg params must is json!")
        }, m.delMsg = function (e) {
          var o, t, n, r;
          r = 1 === arguments.length && e && "object" == typeof e ? (o = e.name || e.msgName, t = e.id || e.msgId, n = e.toID || e.toId, e.data) : (o = e, t = arguments[1], n = arguments[2], arguments[3]);
          var a = {};
          a.name = o, a.id = t || a.name, a.toID = n || "__all", a.data = r, void 0 !== a.name && null !== a.name ? void 0 !== a.id && null !== a.id ? s("delMsg", a) : L.Logger.error("[tk-fake-sdk]delMsg id is must exist!") : L.Logger.error("[tk-fake-sdk]delMsg name is must exist!")
        }, m.changeUserProperty = function (e, o, t) {
          if (!TK.isOnlyAudioRoom || void 0 === t.publishstate || t.publishstate !== TK.PUBLISH_STATE_VIDEOONLY && t.publishstate !== TK.PUBLISH_STATE_BOTH)
            if (void 0 !== t && void 0 !== e) {
              var n = {};
              n.id = e, n.toID = o || "__all", k[e] ? t && "object" == typeof t ? (n.properties = t, s("setProperty", n)) : L.Logger.error("[tk-fake-sdk]properties must be json , user id: " + e + "!") : L.Logger.error("[tk-fake-sdk]user is not exist , user id: " + e + "!")
            } else L.Logger.error("[tk-fake-sdk]changeUserProperty properties or id is not exist!");
          else L.Logger.warning("[tk-fake-sdk]The publishstate of a pure audio room cannot be " + t.publishstate + "!")
        }, m.changeWebPageFullScreen = function (e) {
          s("changeWebPageFullScreen", {
            fullScreen: e
          })
        }, m.leaveroom = function (e) {
          s("leaveroom", {
            force: e = e || !1
          })
        }, m.sendMessage = function (e, o, t) {
          var n = {};
          o = o || "__all", n.toID = o;
          var r = {};
          if ("string" == typeof e) {
            var a = L.Utils.toJsonParse(e);
            "object" != typeof a && (a = e), e = a
          }
          if ("object" == typeof e)
            for (var i in e) r[i] = e[i];
          else r.msg = e;
          if ("string" == typeof t && (t = L.Utils.toJsonParse(t)), t && "object" == typeof t)
            for (var i in t) r[i] = t[i];
          n.message = L.Utils.toJsonStringify(r), s("sendMessage", n)
        }, m.sendActionCommand = function (e, o) {
          e ? s("sendActionCommand", {
            action: e,
            cmd: o
          }) : L.Logger.warning("[tk-fake-sdk]sendActionCommand method action can not be empty!")
        }, m.setLocalStorageItem = function (e, o) {
          s("saveValueByKey", {
            key: "" + e,
            value: "" + o
          })
        }, m.getLocalStorageItem = function (e, o) {
          var t = void 0;
          "function" == typeof o && (t = function (e) {
            null != e && "null" !== e && "undefined" !== e || (e = ""), o("" + e)
          }), s("getValueByKey", {
            key: "" + e
          }, t)
        }, m.exitAnnotation = function (e) {
          "string" == typeof e ? s("exitAnnotation", e) : L.Logger.error("[tk-fake-sdk]state is not a string")
        }, m.registerRoomWhiteBoardDelegate = function (e) {
          if (e && "TKWhiteBoardManager" === e.className && "function" == typeof e.registerRoomDelegate) {
            if (K = e) {
              if (K.registerRoomDelegate) {
                K.registerRoomDelegate(m, function (e, o) {
                  L.Logger.debug("[tk-sdk]receive whiteboard sdk action command（action,cmd）:", e, o), m.sendActionCommand(e, o)
                })
              }
              if (K.changeCommonWhiteBoardConfigration) {
                var o = {};
                if (S && T && void 0 !== I && (o.webAddress = {
                    protocol: S,
                    hostname: T,
                    port: I
                  }), R && _ && void 0 !== O && (o.webAddress = {
                    protocol: R,
                    hostname: _,
                    port: O
                  }), U.length) {
                  for (var t = [], n = 0, r = U.length; n < r; n++) t.push({
                    protocol: A,
                    hostname: U[n],
                    port: C
                  });
                  o.backupDocAddressList = t
                }
                void 0 !== v.id && (o.myUserId = v.id), void 0 !== v.nickname && (o.myName = v.nickname), void 0 !== v.role && (o.myRole = v.role), o.isPlayback = y, o.deviceType = TK.global.fakeJsSdkInitInfo.deviceType, o.clientType = TK.global.fakeJsSdkInitInfo.mobileInfo.clientType, K.changeCommonWhiteBoardConfigration(o)
              }
            }
          } else L.Logger.warning("[tk-sdk]register whiteboardManagerInstance not is a TKWhiteBoardManager instance class , cannot execute registerRoomWhiteBoardDelegate method.")
        }, n = {
          setProperty: function (e) {
            L.Logger.debug("[tk-fake-sdk]setProperty info:", L.Utils.toJsonStringify(e));
            var o = e,
              t = o.id;
            if (o.hasOwnProperty("properties")) {
              var n = o.properties,
                r = k[t];
              if (v.id === t && (r = v), void 0 === r) return void L.Logger.error("[tk-fake-sdk]setProperty user is not exist , userid is " + t + "!");
              for (var a in n) "id" !== a && "watchStatus" !== a && (r[a] = n[a]);
              var i = TK.RoomEvent({
                type: "room-userproperty-changed",
                user: r,
                message: n
              }, {
                fromID: o.fromID
              });
              m.dispatchEvent(i), N === window.TK.ROOM_MODE.BIG_ROOM && t !== v.id && k[t].role !== window.TK.ROOM_ROLE.TEACHER && k[t].role !== window.TK.ROOM_ROLE.ASSISTANT && k[t].publishstate === TK.PUBLISH_STATE_NONE && delete k[t]
            }
          },
          participantLeft: function (e, o) {
            L.Logger.debug("[tk-fake-sdk]participantLeft userid:" + e);
            var t = k[e];
            if (void 0 !== t) {
              if (L.Logger.info("[tk-fake-sdk]user leave room  , user info: " + L.Utils.toJsonStringify(t)), y && void 0 !== o && (t.leaveTs = o), E[t.role] || (E[t.role] = {}), y ? k[e] && (k[e].playbackLeaved = !0) : (delete E[t.role][e], delete k[e]), y && "object" == typeof e) {
                var n = e;
                t.leaveTs = n.ts
              }
              var r = TK.RoomEvent({
                type: "room-participant_leave",
                user: t
              });
              m.dispatchEvent(r)
            } else L.Logger.error("[tk-fake-sdk]participantLeft user is not exist , userid is " + e + "!")
          },
          participantJoined: function (e) {
            L.Logger.debug("[tk-fake-sdk]participantJoined userinfo:" + L.Utils.toJsonStringify(e));
            var o = TK.RoomUser(e);
            L.Logger.info("[tk-fake-sdk]user join room  , user info: " + L.Utils.toJsonStringify(o)), E[o.role] || (E[o.role] = {}), E[o.role][o.id] = o, k[o.id] = o, y && k[o.id] && delete k[o.id].playbackLeaved, y && "object" == typeof e && (o.joinTs = e.ts);
            var t = TK.RoomEvent({
              type: "room-participant_join",
              user: o
            });
            m.dispatchEvent(t)
          },
          participantEvicted: function (e) {
            e = e || {}, L.Logger.info("[tk-fake-sdk]user evicted room  , user info: " + L.Utils.toJsonStringify(v) + " , participantEvicted  messages:" + L.Utils.toJsonStringify(e));
            var o = TK.RoomEvent({
              type: "room-participant_evicted",
              message: e,
              user: v
            });
            m.dispatchEvent(o)
          },
          pubMsg: function (e) {
            if (L.Logger.debug("[tk-fake-sdk]pubMsg info:", L.Utils.toJsonStringify(e)), e && "string" == typeof e && (e = L.Utils.toJsonParse(e)), e.data && "string" == typeof e.data && (e.data = L.Utils.toJsonParse(e.data)), "OnlyAudioRoom" === e.name && M(!0, e.fromID), "BigRoom" === e.name) {
              N = window.TK.ROOM_MODE.BIG_ROOM, D();
              var o = TK.RoomEvent({
                type: "room-mode-changed",
                message: {
                  roomMode: N
                }
              });
              m.dispatchEvent(o)
            }
            o = TK.RoomEvent({
              type: "room-pubmsg",
              message: e
            });
            m.dispatchEvent(o)
          },
          delMsg: function (e) {
            if (L.Logger.debug("[tk-fake-sdk]delMsg info:", L.Utils.toJsonStringify(e)), e && "string" == typeof e && (e = L.Utils.toJsonParse(e)), e.data && "string" == typeof e.data && (e.data = L.Utils.toJsonParse(e.data)), "OnlyAudioRoom" === e.name && M(!1, e.fromID), "BigRoom" === e.name) {
              N = window.TK.ROOM_MODE.NORMAL_ROOM, D();
              var o = TK.RoomEvent({
                type: "room-mode-changed",
                message: {
                  roomMode: N
                }
              });
              m.dispatchEvent(o)
            }
            o = TK.RoomEvent({
              type: "room-delmsg",
              message: e
            });
            m.dispatchEvent(o)
          },
          sendMessage: function (e) {
            if (L.Logger.debug("[tk-fake-sdk]room-text-message info:" + (e && "object" == typeof e ? L.Utils.toJsonStringify(e) : e)), e && e.hasOwnProperty("message")) {
              var o = e.fromID,
                t = v;
              if (10 === u.roomtype) t = void 0;
              else if (void 0 !== o && (t = k[e.fromID]), !t) return void L.Logger.error("[tk-fake-sdk]user is not exist , user id:" + e.fromID + ", message from room-text-message!");
              if (y) {
                var n = !1;
                e && e.message && "string" == typeof e.message && (e.message = L.Utils.toJsonParse(e.message), n = !0), e.message.ts = e.ts, n && "object" == typeof e.message && (e.message = L.Utils.toJsonStringify(e.message))
              }
              e && e.message && "string" == typeof e.message && (e.message = L.Utils.toJsonParse(e.message));
              var r = TK.RoomEvent({
                type: "room-text-message",
                user: t,
                message: e.message
              });
              m.dispatchEvent(r)
            } else L.Logger.error("[tk-fake-sdk]room-text-message messages or messages.message is not exist!")
          },
          roomConnected: function (e, o) {
            if (L.Logger.debug("[tk-fake-sdk]room-connected code is " + e + " , response is :" + L.Utils.toJsonStringify(o)), 0 === e) {
              var t, n = o.roominfo,
                r = o.msglist,
                a = o.userlist;
              for (var i in m.p2p = n.p2p, t = n.id, k = {}, E = {}, o.myself && "object" == typeof o.myself && (v = TK.RoomUser(o.myself)), E[v.role] || (E[v.role] = {}), E[v.role][v.id] = v, k[v.id] = v, a)
                if (a.hasOwnProperty(i)) {
                  var s = a[i],
                    d = TK.RoomUser(s);
                  void 0 !== d && (E[d.role] || (E[d.role] = {}), E[d.role][d.id] = d, k[d.id] = d, y && k[d.id] && delete k[d.id].playbackLeaved, L.Logger.info("[tk-fake-sdk]room-connected --\x3e user info: " + L.Utils.toJsonStringify(d)))
                } L.Logger.info("[tk-fake-sdk]room-connected --\x3e myself info: " + L.Utils.toJsonStringify(v));
              var l = new Array;
              if (r && "string" == typeof r && (r = L.Utils.toJsonParse(r)), r.hasOwnProperty("OnlyAudioRoom")) M(!0, r.OnlyAudioRoom.fromID);
              r.hasOwnProperty("BigRoom") ? (N = window.TK.ROOM_MODE.BIG_ROOM, D()) : N = window.TK.ROOM_MODE.NORMAL_ROOM;
              var g = TK.RoomEvent({
                type: "room-mode-changed",
                message: {
                  roomMode: N
                }
              });
              for (i in m.dispatchEvent(g), r) r.hasOwnProperty(i) && l.push(r[i]);
              if (l.sort(function (e, o) {
                  return void 0 !== e && e.hasOwnProperty("seq") && void 0 !== o && o.hasOwnProperty("seq") ? e.seq - o.seq : 0
                }), m.roomID = t, L.Logger.debug("[tk-fake-sdk]Connected to room " + m.roomID), L.Logger.debug("[tk-fake-sdk]connected response:", L.Utils.toJsonStringify(o)), L.Logger.info("[tk-fake-sdk]room-connected  signalling list length " + l.length), K && K.changeCommonWhiteBoardConfigration) {
                for (var c = [], f = 0, p = U.length; f < p; f++) c.push({
                  protocol: A,
                  hostname: U[f],
                  port: C
                });
                K.changeCommonWhiteBoardConfigration({
                  webAddress: {
                    protocol: S,
                    hostname: T,
                    port: I
                  },
                  docAddress: {
                    protocol: R,
                    hostname: _,
                    port: O
                  },
                  backupDocAddressList: c,
                  myUserId: v.id,
                  myName: v.nickname,
                  myRole: v.role,
                  isConnectedRoom: !0,
                  isPlayback: y,
                  deviceType: TK.global.fakeJsSdkInitInfo.deviceType,
                  clientType: TK.global.fakeJsSdkInitInfo.mobileInfo.clientType
                })
              }
              var u = TK.RoomEvent({
                type: "room-connected",
                streams: [],
                message: l
              });
              return m.dispatchEvent(u), !0
            }
            L.Logger.error("[tk-fake-sdk]connectSocket failure , code is " + e + " , response is " + o)
          },
          disconnect: function (e) {
            L.Logger.debug("[tk-fake-sdk]room-disconnected"),
              function () {
                null != k && (r(E), t(k));
                v && (v.publishstate = TK.PUBLISH_STATE_NONE)
              }(), K && K.changeCommonWhiteBoardConfigration && K.changeCommonWhiteBoardConfigration({
                isConnectedRoom: !1
              });
            var o = TK.RoomEvent({
              type: "room-disconnected",
              message: e || "unexpected-disconnection"
            });
            m.dispatchEvent(o)
          },
          reconnecting: function (e) {
            L.Logger.debug("[tk-fake-sdk]reconnecting info:", e);
            var o = TK.RoomEvent({
              type: "room-reconnecting",
              message: {
                number: e,
                info: "room-reconnecting number:" + e
              }
            });
            m.dispatchEvent(o)
          },
          reconnected: function (e) {
            var o = TK.RoomEvent({
              type: "room-reconnected",
              message: e
            });
            m.dispatchEvent(o)
          },
          leaveroom: function (e) {
            K && K.changeCommonWhiteBoardConfigration && K.changeCommonWhiteBoardConfigration({
              isConnectedRoom: !1
            });
            var o = TK.RoomEvent({
              type: "room-leaveroom",
              message: e
            });
            m.dispatchEvent(o)
          },
          checkroom: function (e) {
            var o, t, n = {},
              r = e.result;
            if (0 == r) {
              o = e.room, t = e.pullinfo, o.roomtype = Number(o.roomtype), o.maxvideo = parseInt(o.maxvideo), e.roomrole = Number(e.roomrole);
              var a = {},
                i = {};
              if (t && t.data && t.data.pullConfigureList) {
                var s = t.data.pullConfigureList;
                for (var d in s) {
                  var l = s[d];
                  a[l.pullProtocol] = l.pullUrlList
                }
              }
              if (t && t.data && t.data.pushConfigureInfo) {
                var g = t.data.pushConfigureInfo;
                for (var d in g) {
                  var c = g[d];
                  i[c.pushProtocol] = c
                }
              }
              o.pullConfigure = a, o.pushConfigure = i, (u = o).roomname, h = o.roomtype, 10 === h, b = o.maxvideo, n.properties = {}, n.properties.role = e.roomrole, n.properties.nickname = e.nickname;
              var f = e.thirdid;
              void 0 !== f && "0" != f && "" != f && (n.id = f), v = TK.RoomUser(n), y ? (w = o.serial + "_" + v.id) && -1 === w.indexOf(":playback") && (w += ":playback") : w = o.serial, L.Logger.info("[tk-fake-sdk]" + (y ? "initPlaybackInfo to checkroom finshed--\x3e" : "") + "_room_max_videocount:" + b, "my id:" + v.id, "room id:" + w, "room properties chairmancontrol is:" + (u.chairmancontrol ? window.__TkSdkBuild__ ? L.Utils.encrypt(u.chairmancontrol) : u.chairmancontrol : void 0))
            } else L.Logger.warning("[tk-fake-sdk]checkroom failure code is " + r);
            var p = TK.RoomEvent({
              type: y ? "room-checkroom-playback" : "room-checkroom",
              message: {
                ret: r,
                userinfo: n,
                roominfo: e
              }
            });
            m.dispatchEvent(p)
          },
          updateWebAddressInfo: function (e) {
            if (S = e.web_protocol || S, T = e.web_host || T, I = e.web_port || I, R = e.doc_protocol || R, _ = e.doc_host || _, O = e.doc_port || O, A = e.backup_doc_protocol || A, a = e.backup_doc_host || a, C = e.backup_doc_port || C, U = e.backup_doc_host_list || U, void 0 === _ && (_ = T), void 0 === O && (O = I), void 0 === a && (a = T), void 0 === C && (C = I), U.length || (U = [a]), K && K.changeCommonWhiteBoardConfigration) {
              for (var o = [], t = 0, n = U.length; t < n; t++) o.push({
                protocol: A,
                hostname: U[t],
                port: C
              });
              K.changeCommonWhiteBoardConfigration({
                webAddress: {
                  protocol: S,
                  hostname: T,
                  port: I
                },
                docAddress: {
                  protocol: R,
                  hostname: _,
                  port: O
                },
                backupDocAddressList: o
              })
            }
            var r = TK.RoomEvent({
              type: "room-serveraddress-update",
              message: {
                web_protocol: S,
                web_host: T,
                web_port: I,
                doc_protocol: R,
                doc_host: _,
                doc_port: O,
                backup_doc_protocol: A,
                backup_doc_host: a,
                backup_doc_port: C,
                backup_doc_host_list: U
              }
            });
            m.dispatchEvent(r)
          },
          updateFakeJsSdkInitInfo: function (e) {
            for (var o in e) {
              var t = e[o];
              if (t && "object" == typeof t)
                for (var n in TK.global.fakeJsSdkInitInfo[o] = TK.global.fakeJsSdkInitInfo[o] || {}, t) TK.global.fakeJsSdkInitInfo[o][n] = t[n];
              else TK.global.fakeJsSdkInitInfo[o] = t
            }
            e.debugLog && m.setLogIsDebug(TK.global.fakeJsSdkInitInfo.debugLog), y = TK.global.fakeJsSdkInitInfo.playback, K && K.changeCommonWhiteBoardConfigration && K.changeCommonWhiteBoardConfigration({
              isPlayback: y,
              deviceType: TK.global.fakeJsSdkInitInfo.deviceType,
              clientType: TK.global.fakeJsSdkInitInfo.mobileInfo.clientType
            });
            var r = TK.RoomEvent({
              type: "room-updateFakeJsSdkInitInfo",
              message: e
            });
            m.dispatchEvent(r)
          },
          receiveActionCommand: function (e, o) {
            var t = TK.RoomEvent({
              type: "room-receiveActionCommand",
              message: {
                action: e,
                cmd: o
              }
            });
            m.dispatchEvent(t)
          },
          playback_clearAll: function () {
            if (y) {
              N = window.TK.ROOM_MODE.NORMAL_ROOM;
              var e = TK.RoomEvent({
                type: "room-mode-changed",
                message: {
                  roomMode: N
                }
              });
              m.dispatchEvent(e);
              var o = TK.RoomEvent({
                type: "room-playback-clear_all"
              });
              m.dispatchEvent(o),
                function () {
                  if (!y) return L.Logger.error("[tk-fake-sdk]No playback environment, no execution playbackClearAll!");
                  null != k && (r(E), t(k));
                  null != v && (v.publishstate = TK.PUBLISH_STATE_NONE)
                }()
            } else L.Logger.warning("[tk-sdk]No playback environment!")
          },
          duration: function (e) {
            if (y) {
              var o = TK.RoomEvent({
                type: "room-playback-duration",
                message: e
              });
              m.dispatchEvent(o)
            } else L.Logger.warning("[tk-sdk]No playback environment!")
          },
          playbackEnd: function () {
            if (y) {
              var e = TK.RoomEvent({
                type: "room-playback-playbackEnd"
              });
              m.dispatchEvent(e)
            } else L.Logger.warning("[tk-sdk]No playback environment!")
          },
          playback_updatetime: function (e) {
            if (y) {
              var o = TK.RoomEvent({
                type: "room-playback-playback_updatetime",
                message: e
              });
              m.dispatchEvent(o)
            } else L.Logger.warning("[tk-sdk]No playback environment!")
          },
          msgList: function (e) {
            L.Logger.debug("[tk-sdk]msgList info:", L.Utils.toJsonStringify(e));
            var o = TK.RoomEvent({
              type: "room-msglist",
              message: e
            });
            m.dispatchEvent(o)
          },
          participantPublished: function (e) {
            if ("string" == typeof e && (e = L.Utils.toJsonParse(e)), L.Logger.debug("[tk-sdk]participantPublished userinfo:" + L.Utils.toJsonStringify(e)), N === window.TK.ROOM_MODE.BIG_ROOM) {
              var o = TK.RoomUser(e),
                t = k[o.id];
              if (t)
                for (var n in o) t[n] = o[n];
              else t = o;
              k[t.id] = t, y && k[t.id] && delete k[t.id].playbackLeaved, y && "object" == typeof e && (t.joinTs = e.ts)
            }
          }
        }, m.setLogIsDebug(TK.global.fakeJsSdkInitInfo.debugLog), "QtNativeClientRoom" === TK.SDKNATIVENAME) try {
        qt && qt.webChannelTransport && new QWebChannel(qt.webChannelTransport, function (e) {
          for (var o in window.qtContentTkClient = e.objects.bridge, n) m.socket.on(o, n[o]);
          m.socket.bindAwitSocketListEvent(), L.Logger.debug("[tk-sdk]qtWebChannel init finshed!");
          var t = TK.RoomEvent({
            type: "room-qtWebChannel-finshed"
          });
          m.dispatchEvent(t)
        })
      } catch (e) {
        L.Logger.error("[tk-fake-sdk]qt or qt.webChannelTransport is not exist  ")
      } else
        for (var o in n) m.socket.on(o, n[o]);
      return m
    }

    function s(e, o, t) {
      L.Logger.debug("[tk-fake-sdk]sendMessageSocket", e, o), m.socket.emit(e, o, t)
    }

    function t(e) {
      if (y)
        for (var o in e) e[o].playbackLeaved = !0;
      else
        for (var o in e) delete e[o]
    }

    function r(e) {
      if (!y)
        for (var o in e) delete e[o]
    }

    function M(e, o) {
      if (TK.isOnlyAudioRoom !== e) {
        TK.isOnlyAudioRoom = e;
        var t = TK.RoomEvent({
          type: "room-audiovideostate-switched",
          message: {
            fromId: o,
            onlyAudio: TK.isOnlyAudioRoom
          }
        });
        m.dispatchEvent(t)
      }
    }

    function D() {
      if (N === window.TK.ROOM_MODE.BIG_ROOM)
        for (var e in k) e !== v.id && k[e].role !== window.TK.ROOM_ROLE.TEACHER && k[e].role !== window.TK.ROOM_ROLE.ASSISTANT && k[e].publishstate === TK.PUBLISH_STATE_NONE && delete k[e]
    }
    L.Logger.error("[tk-fake-sdk]Room is not init!")
  },
  function () {
    var o = !1;
    if (void 0 !== window.__SDKDEV__ && null !== window.__SDKDEV__ && "boolean" == typeof window.__SDKDEV__) try {
      o = window.__SDKDEV__
    } catch (e) {
      o = !1
    }
    var e, t, n, r, a, i, s = o || (e = "debug", t = decodeURIComponent(window.location.href), n = t.indexOf("?"), r = t.substring(n + 1), a = new RegExp("(^|&)" + e + "=([^&]*)(&|$)", "i"), null != (i = r.match(a)) ? i[2] : "");
    if (window.__TkSdkBuild__ = !s, window.localStorage) {
      var d = s ? "*" : "none";
      window.localStorage.setItem("debug", d)
    }
  }(), (TK = TK || {}).PUBLISH_STATE_NONE = 0, TK.PUBLISH_STATE_AUDIOONLY = 1, TK.PUBLISH_STATE_VIDEOONLY = 2, TK.PUBLISH_STATE_BOTH = 3, TK.PUBLISH_STATE_MUTEALL = 4, TK.RoomUser = function (e) {
    if (null != e && void 0 !== e.properties) {
      var o = e.id;
      "string" == typeof e.properties && (e.properties = L.Utils.toJsonParse(e.properties));
      var t = e.properties;
      L.Logger.debug("[tk-fake-sdk]RoomUser", o, t);
      var n = {};
      for (var r in n.id = o, n.watchStatus = 0, t) "id" != r && "watchStatus" != r && (n[r] = t[r]);
      return n.publishstate = n.publishstate || TK.PUBLISH_STATE_NONE, n
    }
    L.Logger.warning("[tk-fake-sdk]Invalidate user info", o, t)
  };
var L, TK = TK || {};
(L = L || {}).Logger = function (s) {
    var d, l = "",
      t = !1;
    return d = function (o, e) {
      try {
        switch (e) {
          case s.Logger.DEBUG:
            t ? console.warn.apply(console, o) : console.debug.apply(console, o);
            break;
          case s.Logger.TRACE:
            console.trace.apply(console, o);
            break;
          case s.Logger.INFO:
            t ? console.warn.apply(console, o) : console.info.apply(console, o);
            break;
          case s.Logger.WARNING:
            console.warn.apply(console, o);
            break;
          case s.Logger.ERROR:
            console.error.apply(console, o);
            break;
          case s.Logger.NONE:
            console.warn("log level is none!");
            break;
          default:
            t ? console.warn.apply(console, o) : console.log.apply(console, o)
        }
      } catch (e) {
        console.log.apply(console, o)
      }
    }, {
      DEBUG: 0,
      TRACE: 1,
      INFO: 2,
      WARNING: 3,
      ERROR: 4,
      NONE: 5,
      setLogDevelopment: function (e) {
        t = e
      },
      enableLogPanel: function () {
        s.Logger.panel = document.createElement("textarea"), s.Logger.panel.setAttribute("id", "licode-logs"), s.Logger.panel.setAttribute("style", "position:fixed;left:0;top:0;width: 100%; height: 100%; display: none;z-index:9999;  user-select: text ;-webkit-user-select: text; -moz-user-select: text ; -ms-user-select: text ;"), s.Logger.panel.setAttribute("rows", 20), s.Logger.panel.setAttribute("cols", 20), s.Logger.panel.setAttribute("readOnly", !0), "QtNativeClientRoom" === TK.SDKNATIVENAME && (document.oncontextmenu = null, document.oncontextmenu = function () {
          return !0
        });
        var e = document.createElement("button");
        e.innerHTML = "open log", e.setAttribute("style", "position:fixed;left:0;top:0;z-index:10000;background:gold;"), e.onclick = function () {
          "open log" === e.innerHTML ? (s.Logger.panel.style.display = "block", e.innerHTML = "close log") : (s.Logger.panel.style.display = "none", e.innerHTML = "open log")
        }, document.body.appendChild(e), document.body.appendChild(s.Logger.panel)
      },
      setLogLevel: function (e) {
        e > s.Logger.NONE ? e = s.Logger.NONE : e < s.Logger.DEBUG && (e = s.Logger.DEBUG), s.Logger.logLevel = e
      },
      setOutputFunction: function (e) {
        d = e
      },
      setLogPrefix: function (e) {
        l = e
      },
      print: function (e) {
        var o = l;
        if (!(e < s.Logger.logLevel)) {
          e === s.Logger.DEBUG ? o = o + "DEBUG(" + (new Date).toLocaleString() + ")" : e === s.Logger.TRACE ? o = o + "TRACE(" + (new Date).toLocaleString() + ")" : e === s.Logger.INFO ? o = o + "INFO(" + (new Date).toLocaleString() + ")" : e === s.Logger.WARNING ? o = o + "WARNING(" + (new Date).toLocaleString() + ")" : e === s.Logger.ERROR && (o = o + "ERROR(" + (new Date).toLocaleString() + ")"), o += ":";
          for (var t = [], n = 0; n < arguments.length; n++) t[n] = arguments[n];
          var r = t.slice(1);
          if (t = [o].concat(r), void 0 !== s.Logger.panel) {
            for (var a = "", i = 0; i < t.length; i++) a += "object" == typeof t[i] ? s.Utils.toJsonStringify(t[i]) : t[i];
            s.Logger.panel.value = s.Logger.panel.value + "\n" + a, d.apply(s.Logger, [t, e])
          } else d.apply(s.Logger, [t, e])
        }
      },
      debug: function () {
        for (var e = [], o = 0; o < arguments.length; o++) e[o] = arguments[o];
        s.Logger.print.apply(s.Logger, [s.Logger.DEBUG].concat(e))
      },
      trace: function () {
        for (var e = [], o = 0; o < arguments.length; o++) e[o] = arguments[o];
        s.Logger.print.apply(s.Logger, [s.Logger.TRACE].concat(e))
      },
      info: function () {
        for (var e = [], o = 0; o < arguments.length; o++) e[o] = arguments[o];
        s.Logger.print.apply(s.Logger, [s.Logger.INFO].concat(e))
      },
      warning: function () {
        for (var e = [], o = 0; o < arguments.length; o++) e[o] = arguments[o];
        s.Logger.print.apply(s.Logger, [s.Logger.WARNING].concat(e))
      },
      error: function () {
        for (var e = [], o = 0; o < arguments.length; o++) e[o] = arguments[o];
        s.Logger.print.apply(s.Logger, [s.Logger.ERROR].concat(e))
      }
    }
  }(L), TK.tkLogPrintConfig = function (e, o, t) {
    e = e || {}, t = t || {};
    var n = null == (o = o || {}).development || o.development,
      r = null != o.logLevel ? o.logLevel : 0,
      a = null == e.debug || e.debug,
      i = null == t.webrtcLogDebug || t.webrtcLogDebug;
    if (L.Logger.setLogDevelopment(n), L.Logger.setLogLevel(r), L.Utils.localStorage) {
      var s = a ? "*" : "none";
      L.Utils.localStorage.setItem("debug", s)
    }
    window.webrtcLogDebug = i
  }, (L = L || {}).Constant = {
    clientType: {
      ios: "ios",
      android: "android"
    },
    IOS: "ios",
    ANDROID: "android",
    LOGLEVEL: {
      DEBUG: 0,
      TRACE: 1,
      INFO: 2,
      WARNING: 3,
      ERROR: 4,
      NONE: 5
    }
  }, window.TK = window.TK || {}, window.TK_ERR = {
    DEVICE_ERROR_UnknownError: 1e4,
    DEVICE_ERROR_NotFoundError: 10001,
    DEVICE_ERROR_NotAllowedError: 10002,
    DEVICE_ERROR_NotReadableError: 10003,
    DEVICE_ERROR_OverconstrainedError: 10004,
    DEVICE_ERROR_TypeError: 10005,
    TIMEOUT_ERROR: 10006
  }, window.TK_VIDEO_MODE = {
    ASPECT_RATIO_CONTAIN: 20001,
    ASPECT_RATIO_COVER: 20002
  }, window.TK.ROOM_ROLE = {
    TEACHER: 0,
    ASSISTANT: 1,
    STUDENT: 2,
    AUDIT: 3,
    PATROL: 4,
    SYSTEM_ADMIN: 10,
    ENTERPRISE_ADMIN: 11,
    ADMIN: 12,
    PLAYBACK: -1
  }, window.TK.ROOM_MODE = {
    NORMAL_ROOM: "normalRoom",
    BIG_ROOM: "bigRoom"
  }, window.TK.ERROR_NOTICE = {
    PUBLISH_AUDIO_VIDEO_FAILURE: 40001,
    SHARE_MEDIA_FAILURE: 40003,
    SHARE_FILE_FAILURE: 40004,
    SHARE_SCREEN_FAILURE: 40005,
    SUBSCRIBE_AUDIO_VIDEO_FAILURE: 40007,
    SUBSCRIBE_MEDIA_FAILURE: 40008,
    SUBSCRIBE_FILE_FAILURE: 40009,
    SUBSCRIBE_SCREEN_FAILURE: 40010,
    UNSUBSCRIBE_AUDIO_VIDEO_FAILURE: 40013,
    UNSUBSCRIBE_MEDIA_FAILURE: 40014,
    UNSUBSCRIBE_FILE_FAILURE: 40015,
    UNSUBSCRIBE_SCREEN_FAILURE: 40016,
    UNPUBLISH_AUDIO_VIDEO_FAILURE: 40019,
    STOP_MEDIA_FAILURE: 40020,
    STOP_FILE_FAILURE: 40021,
    STOP_SCREEN_FAILURE: 40022,
    UDP_CONNECTION_FAILED: 40023,
    UDP_CONNECTION_INTERRUPT: 40024
  }, (L = L || {}).aexInstance = void 0,
  function () {
    var b = {
      cipher: function (e, o) {
        for (var t = o.length / 4 - 1, n = [
            [],
            [],
            [],
            []
          ], r = 0; r < 16; r++) n[r % 4][Math.floor(r / 4)] = e[r];
        n = b.addRoundKey(n, o, 0, 4);
        for (var a = 1; a < t; a++) n = b.subBytes(n, 4), n = b.shiftRows(n, 4), n = b.mixColumns(n, 4), n = b.addRoundKey(n, o, a, 4);
        n = b.subBytes(n, 4), n = b.shiftRows(n, 4), n = b.addRoundKey(n, o, t, 4);
        var i = new Array(16);
        for (r = 0; r < 16; r++) i[r] = n[r % 4][Math.floor(r / 4)];
        return i
      },
      keyExpansion: function (e) {
        for (var o = e.length / 4, t = o + 6, n = new Array(4 * (t + 1)), r = new Array(4), a = 0; a < o; a++) {
          var i = [e[4 * a], e[4 * a + 1], e[4 * a + 2], e[4 * a + 3]];
          n[a] = i
        }
        for (a = o; a < 4 * (t + 1); a++) {
          n[a] = new Array(4);
          for (var s = 0; s < 4; s++) r[s] = n[a - 1][s];
          if (a % o == 0) {
            r = b.subWord(b.rotWord(r));
            for (s = 0; s < 4; s++) r[s] ^= b.rCon[a / o][s]
          } else 6 < o && a % o == 4 && (r = b.subWord(r));
          for (s = 0; s < 4; s++) n[a][s] = n[a - o][s] ^ r[s]
        }
        return n
      },
      subBytes: function (e, o) {
        for (var t = 0; t < 4; t++)
          for (var n = 0; n < o; n++) e[t][n] = b.sBox[e[t][n]];
        return e
      },
      shiftRows: function (e, o) {
        for (var t = new Array(4), n = 1; n < 4; n++) {
          for (var r = 0; r < 4; r++) t[r] = e[n][(r + n) % o];
          for (r = 0; r < 4; r++) e[n][r] = t[r]
        }
        return e
      },
      mixColumns: function (e, o) {
        for (var t = 0; t < 4; t++) {
          for (var n = new Array(4), r = new Array(4), a = 0; a < 4; a++) n[a] = e[a][t], r[a] = 128 & e[a][t] ? e[a][t] << 1 ^ 283 : e[a][t] << 1;
          e[0][t] = r[0] ^ n[1] ^ r[1] ^ n[2] ^ n[3], e[1][t] = n[0] ^ r[1] ^ n[2] ^ r[2] ^ n[3], e[2][t] = n[0] ^ n[1] ^ r[2] ^ n[3] ^ r[3], e[3][t] = n[0] ^ r[0] ^ n[1] ^ n[2] ^ r[3]
        }
        return e
      },
      addRoundKey: function (e, o, t, n) {
        for (var r = 0; r < 4; r++)
          for (var a = 0; a < n; a++) e[r][a] ^= o[4 * t + a][r];
        return e
      },
      subWord: function (e) {
        for (var o = 0; o < 4; o++) e[o] = b.sBox[e[o]];
        return e
      },
      rotWord: function (e) {
        for (var o = e[0], t = 0; t < 3; t++) e[t] = e[t + 1];
        return e[3] = o, e
      },
      sBox: [99, 124, 119, 123, 242, 107, 111, 197, 48, 1, 103, 43, 254, 215, 171, 118, 202, 130, 201, 125, 250, 89, 71, 240, 173, 212, 162, 175, 156, 164, 114, 192, 183, 253, 147, 38, 54, 63, 247, 204, 52, 165, 229, 241, 113, 216, 49, 21, 4, 199, 35, 195, 24, 150, 5, 154, 7, 18, 128, 226, 235, 39, 178, 117, 9, 131, 44, 26, 27, 110, 90, 160, 82, 59, 214, 179, 41, 227, 47, 132, 83, 209, 0, 237, 32, 252, 177, 91, 106, 203, 190, 57, 74, 76, 88, 207, 208, 239, 170, 251, 67, 77, 51, 133, 69, 249, 2, 127, 80, 60, 159, 168, 81, 163, 64, 143, 146, 157, 56, 245, 188, 182, 218, 33, 16, 255, 243, 210, 205, 12, 19, 236, 95, 151, 68, 23, 196, 167, 126, 61, 100, 93, 25, 115, 96, 129, 79, 220, 34, 42, 144, 136, 70, 238, 184, 20, 222, 94, 11, 219, 224, 50, 58, 10, 73, 6, 36, 92, 194, 211, 172, 98, 145, 149, 228, 121, 231, 200, 55, 109, 141, 213, 78, 169, 108, 86, 244, 234, 101, 122, 174, 8, 186, 120, 37, 46, 28, 166, 180, 198, 232, 221, 116, 31, 75, 189, 139, 138, 112, 62, 181, 102, 72, 3, 246, 14, 97, 53, 87, 185, 134, 193, 29, 158, 225, 248, 152, 17, 105, 217, 142, 148, 155, 30, 135, 233, 206, 85, 40, 223, 140, 161, 137, 13, 191, 230, 66, 104, 65, 153, 45, 15, 176, 84, 187, 22],
      rCon: [
        [0, 0, 0, 0],
        [1, 0, 0, 0],
        [2, 0, 0, 0],
        [4, 0, 0, 0],
        [8, 0, 0, 0],
        [16, 0, 0, 0],
        [32, 0, 0, 0],
        [64, 0, 0, 0],
        [128, 0, 0, 0],
        [27, 0, 0, 0],
        [54, 0, 0, 0]
      ],
      Ctr: {}
    };
    b.Ctr.encrypt = function (e, o, t) {
      o = o || "talk_2018_@beijing_20180310_talk_2018_@beijing";
      if (128 != (t = t || 256) && 192 != t && 256 != t) return "";
      e = S.encode(e), o = S.encode(o);
      for (var n = t / 8, r = new Array(n), a = 0; a < n; a++) r[a] = isNaN(o.charCodeAt(a)) ? 0 : o.charCodeAt(a);
      var i = b.cipher(r, b.keyExpansion(r));
      i = i.concat(i.slice(0, n - 16));
      var s = new Array(16),
        d = (new Date).getTime(),
        l = d % 1e3,
        g = Math.floor(d / 1e3),
        c = Math.floor(65535 * Math.random());
      for (a = 0; a < 2; a++) s[a] = l >>> 8 * a & 255;
      for (a = 0; a < 2; a++) s[a + 2] = c >>> 8 * a & 255;
      for (a = 0; a < 4; a++) s[a + 4] = g >>> 8 * a & 255;
      var f = "";
      for (a = 0; a < 8; a++) f += String.fromCharCode(s[a]);
      for (var p = b.keyExpansion(i), u = Math.ceil(e.length / 16), m = new Array(u), v = 0; v < u; v++) {
        for (var k = 0; k < 4; k++) s[15 - k] = v >>> 8 * k & 255;
        for (k = 0; k < 4; k++) s[15 - k - 4] = v / 4294967296 >>> 8 * k;
        var L = b.cipher(s, p),
          y = v < u - 1 ? 16 : (e.length - 1) % 16 + 1,
          E = new Array(y);
        for (a = 0; a < y; a++) E[a] = L[a] ^ e.charCodeAt(16 * v + a), E[a] = String.fromCharCode(E[a]);
        m[v] = E.join("")
      }
      var h = f + m.join("");
      return h = w.encode(h)
    }, b.Ctr.decrypt = function (e, o, t) {
      o = o || "talk_2018_@beijing_20180310_talk_2018_@beijing";
      if (128 != (t = t || 256) && 192 != t && 256 != t) return "";
      e = w.decode(e), o = S.encode(o);
      for (var n = t / 8, r = new Array(n), a = 0; a < n; a++) r[a] = isNaN(o.charCodeAt(a)) ? 0 : o.charCodeAt(a);
      var i = b.cipher(r, b.keyExpansion(r));
      i = i.concat(i.slice(0, n - 16));
      var s = new Array(8),
        d = "";
      d = e.slice(0, 8);
      for (a = 0; a < 8; a++) s[a] = d.charCodeAt(a);
      for (var l = b.keyExpansion(i), g = Math.ceil((e.length - 8) / 16), c = new Array(g), f = 0; f < g; f++) c[f] = e.slice(8 + 16 * f, 8 + 16 * f + 16);
      e = c;
      var p = new Array(e.length);
      for (f = 0; f < g; f++) {
        for (var u = 0; u < 4; u++) s[15 - u] = f >>> 8 * u & 255;
        for (u = 0; u < 4; u++) s[15 - u - 4] = (f + 1) / 4294967296 - 1 >>> 8 * u & 255;
        var m = b.cipher(s, l),
          v = new Array(e[f].length);
        for (a = 0; a < e[f].length; a++) v[a] = m[a] ^ e[f].charCodeAt(a), v[a] = String.fromCharCode(v[a]);
        p[f] = v.join("")
      }
      var k = p.join("");
      return k = S.decode(k)
    };
    var w = {
        code: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
        encode: function (e, o) {
          o = void 0 !== o && o;
          var t, n, r, a, i, s, d, l, g = [],
            c = "",
            f = w.code;
          if (0 < (s = (d = o ? e.encodeUTF8() : e).length % 3))
            for (; s++ < 3;) c += "=", d += "\0";
          for (s = 0; s < d.length; s += 3) n = (t = d.charCodeAt(s) << 16 | d.charCodeAt(s + 1) << 8 | d.charCodeAt(s + 2)) >> 18 & 63, r = t >> 12 & 63, a = t >> 6 & 63, i = 63 & t, g[s / 3] = f.charAt(n) + f.charAt(r) + f.charAt(a) + f.charAt(i);
          return l = (l = g.join("")).slice(0, l.length - c.length) + c
        },
        decode: function (e, o) {
          o = void 0 !== o && o;
          var t, n, r, a, i, s, d, l, g = [],
            c = w.code;
          l = o ? e.decodeUTF8() : e;
          for (var f = 0; f < l.length; f += 4) t = (s = c.indexOf(l.charAt(f)) << 18 | c.indexOf(l.charAt(f + 1)) << 12 | (a = c.indexOf(l.charAt(f + 2))) << 6 | (i = c.indexOf(l.charAt(f + 3)))) >>> 16 & 255, n = s >>> 8 & 255, r = 255 & s, g[f / 4] = String.fromCharCode(t, n, r), 64 == i && (g[f / 4] = String.fromCharCode(t, n)), 64 == a && (g[f / 4] = String.fromCharCode(t));
          return d = g.join(""), o ? d.decodeUTF8() : d
        }
      },
      S = {
        encode: function (e) {
          var o = e.replace(/[\u0080-\u07ff]/g, function (e) {
            var o = e.charCodeAt(0);
            return String.fromCharCode(192 | o >> 6, 128 | 63 & o)
          });
          return o = o.replace(/[\u0800-\uffff]/g, function (e) {
            var o = e.charCodeAt(0);
            return String.fromCharCode(224 | o >> 12, 128 | o >> 6 & 63, 128 | 63 & o)
          })
        },
        decode: function (e) {
          var o = e.replace(/[\u00e0-\u00ef][\u0080-\u00bf][\u0080-\u00bf]/g, function (e) {
            var o = (15 & e.charCodeAt(0)) << 12 | (63 & e.charCodeAt(1)) << 6 | 63 & e.charCodeAt(2);
            return String.fromCharCode(o)
          });
          return o = o.replace(/[\u00c0-\u00df][\u0080-\u00bf]/g, function (e) {
            var o = (31 & e.charCodeAt(0)) << 6 | 63 & e.charCodeAt(1);
            return String.fromCharCode(o)
          })
        }
      };
    L.aexInstance = b.Ctr
  }(), L.Utils = function () {
    var n = void 0,
      t = {
        localStorage: !1,
        sessionStorage: !1
      };
    return n = {
      handleMediaPlayOnEvent: function (o, t) {
        try {
          if (L.Utils.removeEvent(o, "canplay", n.handleMediaPlayOnEvent.bind(null, o, t)), L.Utils.removeEvent(o, "loadedmetadata", n.handleMediaPlayOnEvent.bind(null, o, t)), L.Utils.removeEvent(o, "loadeddata", n.handleMediaPlayOnEvent.bind(null, o, t)), o && o.play && "function" == typeof o.play) {
            var e = o.play();
            e && e.catch && "function" == typeof e.catch && e.catch(function (e) {
              L.Logger.error("[tk-fake-sdk]media play err:", L.Utils.toJsonStringify(e), t ? " , media element id is " + t : " media element:", t ? "" : o)
            })
          }
        } catch (e) {
          L.Logger.error("[tk-fake-sdk]media play error:", L.Utils.toJsonStringify(e), t ? " , media element id is " + t : " media element:", t ? "" : o)
        }
      },
      handleMediaPauseOnEvent: function (o, t) {
        try {
          if (L.Utils.removeEvent(o, "canplay", n.handleMediaPauseOnEvent.bind(null, o, t)), L.Utils.removeEvent(o, "loadedmetadata", n.handleMediaPauseOnEvent.bind(null, o, t)), L.Utils.removeEvent(o, "loadeddata", n.handleMediaPauseOnEvent.bind(null, o, t)), o && o.pause && "function" == typeof o.pause) {
            var e = o.pause();
            e && e.catch && "function" == typeof e.catch && e.catch(function (e) {
              L.Logger.error("[tk-fake-sdk]media pause err:", L.Utils.toJsonStringify(e), t ? " , media element id is " + t : " media element:", t ? "" : o)
            })
          }
        } catch (e) {
          L.Logger.error("[tk-fake-sdk]media pause error:", L.Utils.toJsonStringify(e), t ? " , media element id is " + t : " media element:", t ? "" : o)
        }
      }
    }, {
      addEvent: function (e, o, t, n) {
        n = null != n && null != n && n, e.addEventListener ? e.addEventListener(o, t, n) : e.attachEvent ? e.attachEvent("on" + o, t) : e["on" + o] = t
      },
      removeEvent: function (e, o, t, n) {
        n = null != n && null != n && n, e.removeEventListener ? e.removeEventListener(o, t, n) : e.detachEvent ? e.detachEvent("on" + o, t) : e["on" + o] = null
      },
      toJsonStringify: function (e, o) {
        if (!(o = null == o || o)) return e;
        if (!e) return e;
        try {
          if ("object" != typeof e) return e;
          var t = JSON.stringify(e);
          t ? e = t : L.Logger.debug("[tk-fake-sdk]toJsonStringify:data is not json!")
        } catch (e) {
          L.Logger.debug("[tk-fake-sdk]toJsonStringify:data is not json!")
        }
        return e
      },
      toJsonParse: function (e, o) {
        if (!(o = null == o || o)) return e;
        if (!e) return e;
        try {
          if ("string" != typeof e) return e;
          var t = JSON.parse(e);
          t ? e = t : L.Logger.debug("[tk-fake-sdk]toJsonParse:data is not json string!")
        } catch (e) {
          L.Logger.debug("[tk-fake-sdk]toJsonParse:data is not json string!")
        }
        return e
      },
      encrypt: function (e, o, t, n) {
        if (!e) return e;
        t = t || TK.hexEncryptDecryptKey, n = n || TK.hexEncryptDecryptBit, o = null != o ? o : "talk_2018_@beijing";
        var r = L.aexInstance.encrypt(e, t, n);
        return r = o + r + o
      },
      decrypt: function (e, o, t, n) {
        if (!e) return e;
        t = t || TK.hexEncryptDecryptKey, n = n || TK.hexEncryptDecryptBit, o = null != o ? o : "talk_2018_@beijing";
        var r = new RegExp(o, "gm");
        return e = e.replace(r, ""), L.aexInstance.decrypt(e, t, n)
      },
      mediaPlay: function (e) {
        var o = void 0;
        e && "string" == typeof e ? e = document.getElementById(e) : e && /(audio|video)/g.test(e.nodeName.toLowerCase()) && e.getAttribute && "function" == typeof e.getAttribute && (o = e.getAttribute("id")), e && /(audio|video)/g.test(e.nodeName.toLowerCase()) && (0 !== e.readyState ? n.handleMediaPlayOnEvent(e, o) : (L.Utils.removeEvent(e, "canplay", n.handleMediaPlayOnEvent.bind(null, e, o)), L.Utils.removeEvent(e, "loadedmetadata", n.handleMediaPlayOnEvent.bind(null, e, o)), L.Utils.removeEvent(e, "loadeddata", n.handleMediaPlayOnEvent.bind(null, e, o)), L.Utils.addEvent(e, "canplay", n.handleMediaPlayOnEvent.bind(null, e, o)), L.Utils.addEvent(e, "loadedmetadata", n.handleMediaPlayOnEvent.bind(null, e, o)), L.Utils.addEvent(e, "loadeddata", n.handleMediaPlayOnEvent.bind(null, e, o))))
      },
      mediaPause: function (e) {
        var o = void 0;
        e && "string" == typeof e ? e = document.getElementById(e) : e && /(audio|video)/g.test(e.nodeName.toLowerCase()) && e.getAttribute && "function" == typeof e.getAttribute && (o = e.getAttribute("id")), e && /(audio|video)/g.test(e.nodeName.toLowerCase()) && (0 !== e.readyState ? n.handleMediaPauseOnEvent(e, o) : (L.Utils.removeEvent(e, "canplay", n.handleMediaPauseOnEvent.bind(null, e, o)), L.Utils.removeEvent(e, "loadedmetadata", n.handleMediaPauseOnEvent.bind(null, e, o)), L.Utils.removeEvent(e, "loadeddata", n.handleMediaPauseOnEvent.bind(null, e, o)), L.Utils.addEvent(e, "canplay", n.handleMediaPauseOnEvent.bind(null, e, o)), L.Utils.addEvent(e, "loadedmetadata", n.handleMediaPauseOnEvent.bind(null, e, o)), L.Utils.addEvent(e, "loadeddata", n.handleMediaPauseOnEvent.bind(null, e, o))))
      },
      localStorage: {
        setItem: function (e, o) {
          try {
            window.localStorage ? window.localStorage.setItem ? window.localStorage.setItem(e, o) : L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage.setItem , key is " + e + " , value is " + o + "!") : t.localStorage || (t.localStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage!"))
          } catch (e) {
            t.localStorage || (t.localStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage , error info:", L.Utils.toJsonStringify(e)))
          }
        },
        getItem: function (e) {
          try {
            return window.localStorage ? window.localStorage.getItem ? window.localStorage.getItem(e) : (L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage.getItem , key is " + e + " !"), "") : (t.localStorage || (t.localStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage!")), "")
          } catch (e) {
            return t.localStorage || (t.localStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage , error info:", L.Utils.toJsonStringify(e))), ""
          }
        },
        removeItem: function (e) {
          try {
            return window.localStorage ? window.localStorage.removeItem ? window.localStorage.removeItem(e) : (L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage.removeItem , key is " + e + " !"), "") : (t.localStorage || (t.localStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage!")), "")
          } catch (e) {
            return t.localStorage || (t.localStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support localStorage , error info:", L.Utils.toJsonStringify(e))), ""
          }
        }
      },
      sessionStorage: {
        setItem: function (e, o) {
          try {
            window.sessionStorage ? window.sessionStorage.setItem ? window.sessionStorage.setItem(e, o) : L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage.setItem , key is " + e + " , value is " + o + "!") : t.sessionStorage || (t.sessionStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage!"))
          } catch (e) {
            t.sessionStorage || (t.sessionStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage , error info:", L.Utils.toJsonStringify(e)))
          }
        },
        getItem: function (e) {
          try {
            return window.sessionStorage ? window.sessionStorage.getItem ? window.sessionStorage.getItem(e) : (L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage.getItem , key is " + e + " !"), "") : (t.sessionStorage || (t.sessionStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage!")), "")
          } catch (e) {
            return t.sessionStorage || (t.sessionStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage , error info:", L.Utils.toJsonStringify(e))), ""
          }
        },
        removeItem: function (e) {
          try {
            return window.sessionStorage ? window.sessionStorage.removeItem ? window.sessionStorage.removeItem(e) : (L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage.removeItem , key is " + e + " !"), "") : (t.sessionStorage || (t.sessionStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage!")), "")
          } catch (e) {
            return t.sessionStorage || (t.sessionStorage = !0, L.Logger.warning("[tk-fake-sdk]Browser does not support sessionStorage , error info:", L.Utils.toJsonStringify(e))), ""
          }
        }
      }
    }
  }();
var JsSocket = {};
(TK = TK || {}).fakeScoketIO = function (e) {
  var a = {},
    d = {},
    l = 0,
    t = {};
  return a.bindAwitSocketListEvent = function () {
    for (var e in t) {
      var o = t[e];
      delete t[e], a.on(e, o)
    }
  }, a.on = function (n, r) {
    if (n)
      if (r) {
        var e = function () {
          for (var e = [], o = {}, t = 0; t < arguments.length; t++) e[t] = "string" == typeof arguments[t] ? L.Utils.toJsonParse(arguments[t]) : arguments[t], o[t] = e[t];
          L.Logger.debug("[tk-fake-sdk]logMessage info: receive event(" + n + ") callback arguments json is " + L.Utils.toJsonStringify(o)), TK.global.fakeJsSdkInitInfo.mobileInfo.isSendLogMessageToProtogenesis && "MobileNativeRoom" === TK.SDKNATIVENAME && "printLogMessage" !== n && "JsSocketCallback" !== n && a.emit("printLogMessage", L.Utils.toJsonStringify({
            eventType: n,
            receiveData: e
          })), r.apply(r, e)
        };
        switch (TK.SDKNATIVENAME) {
          case "QtNativeClientRoom":
            if (window.qtContentTkClient) try {
              L.Logger.debug("[tk-fake-sdk]Bind qt event , event name is " + n), window.qtContentTkClient[n].connect(e)
            } catch (e) {
              L.Logger.error("[tk-fake-sdk]Bind qt event fail , event name is " + n, e)
            } else L.Logger.warning("[tk-fake-sdk]window.qtContentTkClient is not exist , save event to list awit socket bind , event name is " + n + " !"), t[n] = e;
            break;
          case "MobileNativeRoom":
            L.Logger.debug("[tk-fake-sdk]Bind mobile event , event name is " + n), JsSocket[n] = e;
            break;
          default:
            L.Logger.error("[tk-fake-sdk]socket.on:room is not init , sdkNativeName is " + TK.SDKNATIVENAME + "!")
        }
      } else L.Logger.error("[tk-fake-sdk]socket bind event callback is must exist!");
    else L.Logger.error("[tk-fake-sdk]socket bind event name is must exist!")
  }, a.emit = function (e, o, t) {
    if ("printLogMessage" !== e || "MobileNativeRoom" === TK.SDKNATIVENAME) {
      var n = {};
      if (L.Logger.debug("[tk-fake-sdk]socket.emit event name is " + e), t && "function" == typeof t) {
        var r = ++l;
        d[r] = t, n.callbackID = r
      }
      if ("object" == typeof o)
        for (var a in o) "callbackID" !== a && (n[a] = o[a]);
      else if ("string" == typeof o) {
        var i = L.Utils.toJsonParse(o);
        if ("object" == typeof i)
          for (var a in i) "callbackID" !== a && (n[a] = i[a]);
        else void 0 !== n.callbackID ? n.params = o : n = o
      } else void 0 !== n.callbackID ? n.params = o : n = o;
      switch (TK.SDKNATIVENAME) {
        case "QtNativeClientRoom":
          window.qtContentTkClient ? window.qtContentTkClient["onWeb_" + e] ? (L.Logger.debug("[tk-fake-sdk]socket.emit:window.qtContentTkClient.onWeb_" + e + " has been performed!"), void 0 === n ? window.qtContentTkClient["onWeb_" + e]("") : ("object" == typeof n && (n = L.Utils.toJsonStringify(n)), window.qtContentTkClient["onWeb_" + e](n))) : L.Logger.error("[tk-fake-sdk]socket.emit:window.qtContentTkClient.onWeb_" + e + " is not exist!") : L.Logger.error("[tk-fake-sdk]window.qtContentTkClient is not exist!");
          break;
        case "MobileNativeRoom":
          var s = TK.global.fakeJsSdkInitInfo.mobileInfo.clientType;
          switch (null == s && (window.JSWhitePadInterface || window.JSVideoWhitePadInterface ? s = L.Constant.clientType.android : window.webkit && window.webkit.messageHandlers && (s = L.Constant.clientType.ios)), s) {
            case L.Constant.clientType.ios:
              window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers[e] ? (L.Logger.debug("[tk-fake-sdk]socket.emit:window.webkit.messageHandlers." + e + ".postMessage has been performed!"), void 0 === n ? window.webkit.messageHandlers[e].postMessage({
                data: ""
              }) : ("object" == typeof n && (n = L.Utils.toJsonStringify(n)), window.webkit.messageHandlers[e].postMessage({
                data: n
              }))) : L.Logger.error("[tk-fake-sdk]socket.emit:window.webkit.messageHandlers." + e + ".postMessage is not exist!");
              break;
            case L.Constant.clientType.android:
              "_videoWhiteboardPage" === TK.extendSendInterfaceName ? window.JSVideoWhitePadInterface && window.JSVideoWhitePadInterface[e] ? (L.Logger.debug("[tk-fake-sdk]socket.emit:window.JSVideoWhitePadInterface." + e + " has been performed!"), void 0 === n ? window.JSVideoWhitePadInterface[e]("") : ("object" == typeof n && (n = L.Utils.toJsonStringify(n)), window.JSVideoWhitePadInterface[e](n))) : L.Logger.error("[tk-fake-sdk]socket.emit:window.JSVideoWhitePadInterface." + e + " is not exist!") : window.JSWhitePadInterface && window.JSWhitePadInterface[e] ? (L.Logger.debug("[tk-fake-sdk]socket.emit:window.JSWhitePadInterface." + e + " has been performed!"), void 0 === n ? window.JSWhitePadInterface[e]("") : ("object" == typeof n && (n = L.Utils.toJsonStringify(n)), window.JSWhitePadInterface[e](n))) : L.Logger.error("[tk-fake-sdk]socket.emit:window.JSWhitePadInterface." + e + " is not exist!");
              break;
            default:
              L.Logger.error("[tk-fake-sdk]clientType is undefinable , will not be able to execute method " + e + " , clientType is " + s)
          }
          break;
        default:
          L.Logger.error("[tk-fake-sdk]socket.emit:room is not init , sdkNativeName is " + TK.SDKNATIVENAME + "!")
      }
    } else L.Logger.warning("[tk-fake-sdk]socket.emit event name is printLogMessage , not emit event , because event triggering must be in the mobile app environment.")
  }, a.on("JsSocketCallback", function (e, o) {
    d[e] && ("function" == typeof d[e] && d[e](o), delete d[e])
  }), a
};