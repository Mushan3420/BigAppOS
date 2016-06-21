;(function(){
  
   var iBase = {
        Id: function(name){
            return document.getElementById(name);
        },
    //设置元素透明度,透明度值按IE规则计,即0~100
        SetOpacity: function(ev, v){
            ev.filters ? ev.style.filter = 'alpha(opacity=' + v + ')' : ev.style.opacity = v / 100;
        }
    }
  
  function connectWebViewJavascriptBridge(callback) {
  
      if (window.WebViewJavascriptBridge) {
          callback(WebViewJavascriptBridge)
      }
      else {
          document.addEventListener('WebViewJavascriptBridgeReady', function() {
                                callback(WebViewJavascriptBridge)
                                }, false)
      }
  }

  
  //淡入效果(含淡入到指定透明度)
  function fadeIn(elem, speed, opacity){
    /*
     * 参数说明
     * elem==>需要淡入的元素
     * speed==>淡入速度,正整数(可选)
     * opacity==>淡入到指定的透明度,0~100(可选)
     */
      speed = speed || 20;
      opacity = opacity || 100;
    //显示元素,并将元素值为0透明度(不可见)
      elem.style.display = 'block';
      iBase.SetOpacity(elem, 0);
    //初始化透明度变化值为0
      var val = 0;
    //循环将透明值以5递增,即淡入效果
      (function(){
          iBase.SetOpacity(elem, val);
          val += 5;
          if (val <= opacity) {
              setTimeout(arguments.callee, speed)
          }
      })();
  }
  
  //淡出效果(含淡出到指定透明度)
  function fadeOut(elem, speed, opacity){
    /*
     * 参数说明
     * elem==>需要淡入的元素
     * speed==>淡入速度,正整数(可选)
     * opacity==>淡入到指定的透明度,0~100(可选)
     */
      speed = speed || 20;
      opacity = opacity || 0;
      //初始化透明度变化值为0
      var val = 100;
    //循环将透明值以5递减,即淡出效果
      (function(){
          iBase.SetOpacity(elem, val);
          val -= 5;
          if (val >= opacity) {
              setTimeout(arguments.callee, speed);
          }else if (val < 0) {
        //元素透明度为0后隐藏元素
              elem.style.display = 'none';
          }
      })();
  }
  
  
  function imageOnClick(image_src){
      alert(image_src);
  
//    var  bridge = window.WebViewJavascriptBridge;
//      
//     bridge.callHandler('appointImageDownload', {'foo': 'bar'}, function(response) {
//                         alert(response);
//      })

  }
  
  // 初始化 WebViewJavascriptBridge
  connectWebViewJavascriptBridge(function(bridge) {
                                 
                     // 0. 初始化，所有 objc send(data) 的方法都会经过这里处理
                     // FIXME: 测试发现如果不 init，下面的 bridge.registerHandler 不会被 objc 调用到
                    bridge.init(function(data, responseCallback) {
                                 var data = JSON.stringify(data);
                                 // alert("JS Got data from objc: " + data);
                                 if (responseCallback) {
                                     responseCallback("JS reponse for objc data=" + data);
                                 }
                    });
                     
                     // 4. 当 objc 每下载完一张图片时会调用，
                     // 给替换后的 HTML 追加 image.src 属性，赋值为本地图片路径
                     bridge.registerHandler('imageDownloadComplete', function(data) {
                                            
                                    var paths = data.toString().split(",");
                                    var origin_src = paths[0];
                                    var cached_src = paths[1];
                                    
                                    //alert(origin_src);
                                    //alert(cached_src);
                                    
                                    var allImage = document.querySelectorAll("img");
                                    allImage = Array.prototype.slice.call(allImage, 0);
                                    
                                    allImage.forEach(function(image) {
                                             if (image.getAttribute("image_src") == origin_src) {
                                                image.src = cached_src;
                                                fadeIn(image,10,100);
                                             
                                             }
                                                     
                                     });
                     });
                                 
//                     //点击显示图片 appoint
//                     bridge.registerHandler("appointImageShow",function(data){
//                        
//                                            
//                     });
                     
                     
                     // 1. 将当前页面所有 image 下的 image_src 地址收集起来
                     var imageUrlsArray = new Array();
                     var allImage = document.querySelectorAll("img");
                     
                     // 2. 从已经被替换好的 image.image_src 属性中取出图片路径
                     allImage = Array.prototype.slice.call(allImage, 0);
                     allImage.forEach(function(image) {
                              var image_src = image.getAttribute("image_src");
                              var newLength = imageUrlsArray.push(image_src);
                              
                              image.onerror = function () { image.src = 'detail_loading_img.png'; }
                                   
                               //如果父节点是a标签的话，将a标签设置成死链接
                              if(image.parentNode.nodeName == "A"){
                                 image.parentNode.href ="javascript:void(0)";
                              }
                              
                              image.onclick = function(){
                                    if(image.getAttribute("src") == "detail_click_show_img.png"){
                                          image.src = "detail_loading_img.png";
                                          bridge.callHandler('appointImageDownload', image_src, function(cachePath) {
                                                           image.src = cachePath;
                                                           fadeIn(image,10,100);
                                                             
                                          })
                                      }

                              };
                                      
                     });
                     
                     // 3. 将所有图片地址发给 objc 端
                     bridge.send(imageUrlsArray);
});
  
  
  
 
 
 })();