const plugin = {
  install (vue, options) {

    vue.prototype.JsonCache = function (fileCache) {
      // 默认参数
      const cacheFileDefaultOptions = {
        id: '',
        json: null, // json数据，cacheKeys保存要下载的文件url保存在哪个键名中。
        cacheKeys: ['ImageURL'], // 要缓存的键名
        suffix: { // 键名的url后缀
          ImageURL: '@68.png'
        },
        urls: null, // 要下载文件的url，或url数组
        force: false, // 是否强制重新下载
        now: true, // 立即下载
        percent: 0, // 当前下载进度的百分比
        success: null, // 成功回调
        error: null, // 失败回调
        progress: null // 进度回调
      }

      let downloadStack = [] // 下载任务栈

      // 实现参数
      let opts = {
        fileCache: fileCache
      }

      function error(item, failedDownloads) {
        downloadStack.pop()

        if(typeof item.error === 'function') {
          item.error(failedDownloads)
        }

        // 当前下载任务完成，继续之前中断的下载任务。
        let nextItem = downloadStack.pop()
        if (typeof nextItem != 'undefined') {
          cacheFile(undefined, nextItem)
        }
      }

      function success(item) {
        if (item.percent != 100) {
          item.percent = 100
          if (typeof item.progress === 'function') {
            item.progress(item.percent)
          }
        }

        // 当前下载任务完成，继续之前中断的下载任务。
        downloadStack.pop()

        // 下载完成
        if(typeof item.success === 'function') {
          item.success(item.urls)
        }

        let nextItem = downloadStack.pop()
        if (typeof nextItem != 'undefined') {
          cacheFile(undefined, nextItem)
        }
      }

      function cacheFile(options, currentItem) {
        let now = (typeof currentItem != 'undefined') || (options.now || false)

        // 现有的下载任务或新的下载任务
        let item = currentItem || null
        if (item == null) {
          // 默认参数
          item = JSON.parse(JSON.stringify(cacheFileDefaultOptions))
          item.id = new Date().getTime()

          // 合并传入的参数
          for(let property in options){
            if (options.hasOwnProperty(property)) {
              item[property] = options[property]
            }
          }

          // 如果urls是字符串，那么将其转成数组
          if(!item.urls) item.urls = []
          if(typeof item.urls === 'string') item.urls = [item.urls]
        }

        // 下载任务立即放入栈顶开始下载
        if (now || downloadStack.length == 0) {
          // 取消当前正在进行的下载任务
          opts.fileCache.abort()
          opts.fileCache.getAdded().length = 0

          downloadStack.push(item)

          // 如果强制重新下载，那么先删除缓存文件
          if (item.force) {
            for (let i=0; i < item.urls.length; i++) {
              let url = opts.fileCache.get(encodeURI(item.urls[i]))
              if (opts.fileCache.isCached(url)) {
                opts.fileCache.remove(url)
              }
            }
          }

          // 添加要下载的文件
          for (let i=0; i < item.urls.length; i++) {
            let url = opts.fileCache.get(encodeURI(item.urls[i])) // 得到缓存路径或网络路径
            // 检查文件是否已经缓存
            if (!opts.fileCache.isCached(url)) {
              // 未缓存，加入下载队列
              opts.fileCache.add(url)
            }
          }

          if (opts.fileCache.isDirty()) {
            // 开始下载
            opts.fileCache.download((e) => {
              // 下载进度
              if (e.type === 'progress') {
                let val = Math.floor(e.percentage * 100)
                if (item.percent != val) {
                  item.percent = val
                  if (typeof item.progress === 'function') {
                    item.progress(item.percent)
                  }
                }
              }
            }, true).then(function (cache) {
              // 下载成功
              console.log('Download success id=(', item.id, '):', cache)
              success(item)
            }, function (failedDownloads) {
                // 下载失败
              console.log('Download fail id=(', item.id, '):', failedDownloads)
              error(item, failedDownloads)
              if(typeof item.error === 'function') {
                item.error(failedDownloads)
              }
            })
          }
          else {
            success(item)
          }
        }
        else {
          // 下载任务放入栈底，等待下载
          downloadStack.unshift(item)
        }

        return item
      }

      // 遍历jsons中所有键名为name的值
      function getJsonValue(json, name, suffix, value) {
        if (!suffix) suffix = {}
        if (!value) value = []
        if (typeof name === 'string') name = [name]

        for (let key in json) {
          if (json[key] instanceof Object) {
            getJsonValue(json[key], name, suffix, value) // 如果是Object则递归
          }
          else {
            for (let i = 0; i < name.length; i++) {
              if (key === name[i]) {
                value.push(suffix.hasOwnProperty(key) ? json[key] + suffix[key] : json[key])
                break
              }
            }
          }
        }

        return value
      }

      // 递归遍历所有键，找出键为img和url的内容，下载并替换为缓存的文件。
      function cache(options) {
        // 默认参数
        let cacheOpt = JSON.parse(JSON.stringify(cacheFileDefaultOptions))

        // 合并传入的参数
        for(let property in options){
          if (options.hasOwnProperty(property)) {
            cacheOpt[property] = options[property]
          }
        }

        if (cacheOpt.jsons != null) {
          cacheOpt.urls = getJsonValue(cacheOpt.jsons, cacheOpt.cacheKeys, cacheOpt.suffix)
        }
        return cacheFile(cacheOpt)
      }

      // 取消已经提交的缓存任务
      function cacheCancel(id) {
        if (downloadStack.length > 0) {
          // 要取消的任务是正在下载的任务。
          if (downloadStack[downloadStack.length - 1].id === id) {
            // 停止当前下载，
            opts.fileCache.abort()
            opts.fileCache.getAdded().length = 0
            downloadStack.pop()

            // 继续被中断的下载任务。
            let nextItem = downloadStack.pop()
            if (typeof nextItem != 'undefined') {
              cacheFile(undefined, nextItem)
            }
          }
          else {
            // 从下载任务栈中删除下载任务。
            for(let i=0; i < downloadStack.length - 1; i++) {
              if (downloadStack[i].id === id) {
                delete downloadStack[i]
                break
              }
            }
          }
        }
      }

      // 递归遍历json所有键，如果name中的URL已经缓存，那么替换URL为内部的URL
      function url2Cache(json, name, suffix) {
        if (!suffix) suffix = {}
        if (typeof name === 'string') name = [name]

        for (let key in json) {
          if (json[key] instanceof Array) {
            // 处理数组
            let done = false
            for (let i = 0; i < name.length; i++) {
              if (key === name[i]) {
                done = true
                for (let j = 0; j < json[key].length; j++) {
                  let value = encodeURI(suffix.hasOwnProperty(key) ? json[key][j] + suffix[key]: json[key][j])
                  let url = opts.fileCache.get(value)
                  let iUrl = opts.fileCache.toInternalURL(url)
                  if (url === iUrl) {
                    // 文件没有缓存，指向网络路径
                    json[key][j] = url
                  } else {
                    // 文件已经缓存，替换成内部的url。

                    // 注：在android上可以将img标签的src属性
                    // 直接指定cdvfile://xxx的路径，但在浏览器上测试时不能显示。
                    // 所以在浏览器上测试时还是使用远程服务器的地址。
                    // 这样两种情况下都可以显示。
                    // 使用了cordova-plugin-device插件来获取设备类型。
                    if (window.device.platform === 'browser') {
                      json[key][j] = opts.fileCache.toURL(url)
                    } else {
                      // 非浏览器中直接显示本地图片
                      json[key][j] = iUrl
                    }
                  }
                }
              }
            }
            if (!done) {
              url2Cache(json[key], name, suffix) // 如果是Object则递归
            }
          }
          else if (json[key] instanceof Object) {
            url2Cache(json[key], name, suffix) // 如果是Object则递归
          }
          else {
            for (let i = 0; i < name.length; i++) {
              if (key === name[i]) {
                let value = encodeURI(suffix.hasOwnProperty(key) ? json[key] + suffix[key]: json[key])
                let url = opts.fileCache.get(value)
                let iUrl = opts.fileCache.toInternalURL(url)
                if (url === iUrl) {
                  // 文件没有缓存，指向网络路径
                  json[key] = url
                } else {
                  // 文件已经缓存，替换成内部的url。

                  // 注：在android上可以将img标签的src属性
                  // 直接指定cdvfile://xxx的路径，但在浏览器上测试时不能显示。
                  // 所以在浏览器上测试时还是使用远程服务器的地址。
                  // 这样两种情况下都可以显示。
                  // 使用了cordova-plugin-device插件来获取设备类型。
                  if (window.device.platform === 'browser') {
                    json[key] = opts.fileCache.toURL(url)
                  } else {
                    // 非浏览器中直接显示本地图片
                    json[key] = iUrl
                  }
                }
              }
            }
          }
        }
      }

      // 是否已经缓存
      function isCached(url) {
        url = opts.fileCache.get(encodeURI(url))
        return opts.fileCache.isCached(url)
      }

      // 删除缓存
      function cacheRemove(url) {
        url = opts.fileCache.get(encodeURI(url))
        if (opts.fileCache.isCached(url)) {
          opts.fileCache.remove(url)
        }
      }

      // 曝露给外部的接口
      this.cache = cache
      this.cacheCancel = cacheCancel
      this.url2Cache = url2Cache
      this.isCached = isCached
      this.cacheRemove = cacheRemove
    }
  }
}

export default plugin
