/* Thickbox 3 - One Box To Rule Them All.
 	 * Copyright (c) 2007 Cody Lindley (http://www.codylindley.com)
  	 * Licensed under the MIT License: http://www.opensource.org/licenses/mit-license.php */
	//on page load call tb_init
	function tb_init(e){$(e).unbind("click"),$(e).click(function(){return tb_show(this.title||this.name||null,this.href||this.alt,this.rel||!1),this.blur(),!1})}function tb_show(r,T,_){try{var e;void 0===document.body.style.maxHeight?($("body","html").css({height:"100%",width:"100%"}),$("html").css("overflow","hidden"),null===document.getElementById("TB_HideSelect")&&($("body").append("<iframe id='TB_HideSelect'></iframe><div id='TB_overlay'></div><div id='TB_window'></div>"),$("#TB_overlay").click(tb_remove))):null===document.getElementById("TB_overlay")&&($("body").append("<div id='TB_overlay'></div><div id='TB_window'></div>"),$("#TB_overlay").click(tb_remove)),tb_detectMacXFF()?$("#TB_overlay").addClass("TB_overlayMacFFBGHack"):$("#TB_overlay").addClass("TB_overlayBG"),null===r&&(r=""),e=-1!==T.indexOf("?")?T.substr(0,T.indexOf("?")):T;var t=/\.jpg$|\.jpeg$|\.png$|\.gif$|\.bmp$/,n=e.toLowerCase().match(t);if(".jpg"==n||".jpeg"==n||".png"==n||".gif"==n||".bmp"==n){if(TB_PrevCaption="",TB_PrevURL="",TB_PrevHTML="",TB_NextCaption="",TB_NextURL="",TB_NextHTML="",TB_imageCount="",TB_FoundURL=!1,_)for(TB_TempArray=$("a[rel="+_+"]").get(),TB_Counter=0;TB_Counter<TB_TempArray.length&&""===TB_NextHTML;TB_Counter++){TB_TempArray[TB_Counter].href.toLowerCase().match(t);TB_TempArray[TB_Counter].href!=T?TB_FoundURL?(TB_NextCaption=TB_TempArray[TB_Counter].title,TB_NextURL=TB_TempArray[TB_Counter].href,TB_NextHTML="<span id='TB_next'>&nbsp;&nbsp;<a href='#'>Next &gt;</a></span>"):(TB_PrevCaption=TB_TempArray[TB_Counter].title,TB_PrevURL=TB_TempArray[TB_Counter].href,TB_PrevHTML="<span id='TB_prev'>&nbsp;&nbsp;<a href='#'>&lt; Prev</a></span>"):(TB_FoundURL=!0,TB_imageCount="Image "+(TB_Counter+1)+" of "+TB_TempArray.length)}imgPreloader=new Image,imgPreloader.onload=function(){imgPreloader.onload=null;var e=tb_getPageSize(),t=e[0]-150,n=e[1]-150,i=imgPreloader.width,o=imgPreloader.height;if(t<i?(o*=t/i,i=t,n<o&&(i*=n/o,o=n)):n<o&&(i*=n/o,o=n,t<i&&(o*=t/i,i=t)),TB_WIDTH=i,TB_HEIGHT=o+15,$("#TB_window").append("<a href='' id='TB_ImageOff' title='Close'><center><img id='TB_Image' src='"+T+"' width='"+i+"' height='"+o+"' alt='"+r+"'/></center></a>"),$("#TB_window").append("<table width='100%'><tr><td id = 'T1'></td><td id='T2'></td><td id='T3'></td></tr></table>"),$("#T1").append("<div id='TB_left'>"+r+TB_imageCount+TB_PrevHTML+TB_NextHTML+"</div>"),$("#T2").append("<center><div id='TB_center'><a href='"+T+"' id='TB_ImageLink'>Show full-size image</a></div></center>"),$("#T3").append("<div id='TB_right'><a href='#' id='TB_closeWindowButton' title='Close'>close</a> or Esc Key</div>"),$("#TB_closeWindowButton").click(tb_remove),""!==TB_PrevHTML){function d(){return $(document).unbind("click",d)&&$(document).unbind("click",d),$("#TB_window").remove(),$("body").append("<div id='TB_window'></div>"),tb_show(TB_PrevCaption,TB_PrevURL,_),!1}$("#TB_prev").click(d)}if(""!==TB_NextHTML){function a(){return $("#TB_window").remove(),$("body").append("<div id='TB_window'></div>"),tb_show(TB_NextCaption,TB_NextURL,_),!1}$("#TB_next").click(a)}document.onkeydown=function(e){keycode=null==e?event.keyCode:e.which,27==keycode?tb_remove():190==keycode?""!=TB_NextHTML&&(document.onkeydown="",a()):188==keycode&&""!=TB_PrevHTML&&(document.onkeydown="",d())},tb_position(),$("#TB_load").remove(),$("#TB_ImageOff").click(tb_remove),$("#TB_window").css({display:"block"})},imgPreloader.src=T}else{var i=tb_parseQuery(T.replace(/^[^\?]+\??/,""));TB_WIDTH=+i.width+30||630,TB_HEIGHT=+i.height+40||440,ajaxContentW=TB_WIDTH-30,ajaxContentH=TB_HEIGHT-45,-1!=T.indexOf("TB_iframe")?(urlNoQuery=T.split("TB_"),$("#TB_iframeContent").remove(),"true"!=i.modal?$("#TB_window").append("<div id='TB_title'><div id='TB_ajaxWindowTitle'>"+r+"</div><div id='TB_closeAjaxWindow'><a href='#' id='TB_closeWindowButton' title='Close'>close</a> or Esc Key</div></div><iframe frameborder='0' hspace='0' src='"+urlNoQuery[0]+"' id='TB_iframeContent' name='TB_iframeContent"+Math.round(1e3*Math.random())+"' onload='tb_showIframe()' style='width:"+(ajaxContentW+29)+"px;height:"+(ajaxContentH+17)+"px;' > </iframe>"):($("#TB_overlay").unbind(),$("#TB_window").append("<iframe frameborder='0' hspace='0' src='"+urlNoQuery[0]+"' id='TB_iframeContent' name='TB_iframeContent"+Math.round(1e3*Math.random())+"' onload='tb_showIframe()' style='width:"+(ajaxContentW+29)+"px;height:"+(ajaxContentH+17)+"px;'> </iframe>"))):"block"!=$("#TB_window").css("display")?"true"!=i.modal?$("#TB_window").append("<div id='TB_title'><div id='TB_ajaxWindowTitle'>"+r+"</div><div id='TB_closeAjaxWindow'><a href='#' id='TB_closeWindowButton'>close</a> or Esc Key</div></div><div id='TB_ajaxContent' style='width:"+ajaxContentW+"px;height:"+ajaxContentH+"px'></div>"):($("#TB_overlay").unbind(),$("#TB_window").append("<div id='TB_ajaxContent' class='TB_modal' style='width:"+ajaxContentW+"px;height:"+ajaxContentH+"px;'></div>")):($("#TB_ajaxContent")[0].style.width=ajaxContentW+"px",$("#TB_ajaxContent")[0].style.height=ajaxContentH+"px",$("#TB_ajaxContent")[0].scrollTop=0,$("#TB_ajaxWindowTitle").html(r)),$("#TB_closeWindowButton").click(tb_remove),-1!=T.indexOf("TB_inline")?($("#TB_ajaxContent").append($("#"+i.inlineId).children()),$("#TB_window").unload(function(){$("#"+i.inlineId).append($("#TB_ajaxContent").children())}),tb_position(),$("#TB_load").remove(),$("#TB_window").css({display:"block"})):-1!=T.indexOf("TB_iframe")?(tb_position(),$.browser.safari&&($("#TB_load").remove(),$("#TB_window").css({display:"block"}))):$("#TB_ajaxContent").load(T+="&random="+(new Date).getTime(),function(){tb_position(),$("#TB_load").remove(),tb_init("#TB_ajaxContent a.thickbox"),$("#TB_window").css({display:"block"})})}i&&!i.modal&&(document.onkeyup=function(e){keycode=null==e?event.keyCode:e.which,27==keycode&&tb_remove()})}catch(e){}}function tb_showIframe(){$("#TB_load").remove(),$("#TB_window").css({display:"block"})}function tb_remove(){return $("#TB_imageOff").unbind("click"),$("#TB_closeWindowButton").unbind("click"),$("#TB_window").fadeOut("fast",function(){$("#TB_window,#TB_overlay,#TB_HideSelect").trigger("unload").unbind().remove()}),$("#TB_load").remove(),void 0===document.body.style.maxHeight&&($("body","html").css({height:"auto",width:"auto"}),$("html").css("overflow","")),document.onkeydown="",document.onkeyup="",!1}function tb_position(){$("#TB_window").css({marginLeft:"-"+parseInt((TB_WIDTH+28)/2,10)+"px",marginTop:"-"+parseInt((TB_HEIGHT+28)/2,10)+"px",width:TB_WIDTH+"px",height:TB_HEIGHT+"px"})}function tb_parseQuery(e){var t={};if(!e)return t;for(var n=e.split(/[;&]/),i=0;i<n.length;i++){var o=n[i].split("=");if(o&&2==o.length){var d=unescape(o[0]),a=unescape(o[1]);a=a.replace(/\+/g," "),t[d]=a}}return t}function tb_getPageSize(){var e=document.documentElement,t=window.innerWidth||self.innerWidth||e&&e.clientWidth||document.body.clientWidth,n=window.innerHeight||self.innerHeight||e&&e.clientHeight||document.body.clientHeight;return arrayPageSize=[t,n],arrayPageSize}function tb_detectMacXFF(){var e=navigator.userAgent.toLowerCase();if(-1!=e.indexOf("mac")&&-1!=e.indexOf("firefox"))return!0}$(document).ready(function(){tb_init("a.thickbox, area.thickbox, input.thickbox"),imgLoader=new Image});