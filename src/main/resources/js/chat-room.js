/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/**
 * @fileoverview 聊天室
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.3.0.2, Apr 30, 2020
 */

/**
 * @description Add comment function.
 * @static
 */
var ChatRoom = {
  init: function () {
    // 聊天窗口高度设置
    /* if ($.ua.device.type !== 'mobile') {
      $('.list').
        height($('.side').height() -
          $('.chat-room .module:first').outerHeight() - 20)
    } else {
      $('.list').height($(window).height() - 173)
    } */

    // 没用登录就不需要编辑器初始化了
    if ($('#chatContent').length === 0) {
      return false
    }

    ChatRoom.editor = Util.newVditor({
      id: 'chatContent',
      cache: true,
      preview: {
        mode: 'editor',
      },
      resize: {
        enable: true,
        position: 'bottom',
      },
      toolbar: [
        'emoji',
        'headings',
        'bold',
        'italic',
        'link',
        '|',
        'list',
        'ordered-list',
        'check',
        'outdent',
        'indent',
        '|',
        'quote',
        'code',
        'insert-before',
        'insert-after',
        '|',
        'upload',
        'table',
        '|',
        'undo',
        'redo',
        '|',
        {
          name: 'more',
          toolbar: [
            'fullscreen',
            'edit-mode',
            'both',
            'preview',
            'outline',
            'content-theme',
            'code-theme',
            'devtools',
            'info',
            'help',
          ],
        }],
      height: 200,
      counter: 40960,
      placeholder: '聊天室历史记录将永久保存，每天一次撤回机会；在发送消息前请三思而后行，友善是第一原则。',
      ctrlEnter: function () {
        ChatRoom.send()
      },
    })

    // img preview
    var fixDblclick = null
    $('.chat-room').on('dblclick', '.vditor-reset img', function () {
      clearTimeout(fixDblclick)
      if ($(this).hasClass('emoji')) {
        return
      }
      window.open($(this).attr('src'))
    }).on('click', '.vditor-reset img', function (event) {
      clearTimeout(fixDblclick)
      if ($(this).hasClass('emoji')) {
        return
      }
      var $it = $(this),
          it = this
      fixDblclick = setTimeout(function () {
        var top = it.offsetTop,
            left = it.offsetLeft

        $('body').
        append('<div class="img-preview" onclick="$(this).remove()"><img style="transform: translate3d(' +
            Math.max(0, left) + 'px, ' +
            Math.max(0, (top - $(window).scrollTop())) + 'px, 0)" src="' +
            ($it.attr('src').split('?imageView2')[0]) +
            '"></div>')

        $('.img-preview').css({
          'background-color': '#fff',
          'position': 'fixed',
        })
      }, 100)
    })

    // 表情包初始化
    // 加载表情
    ChatRoom.listenUploadEmojis();
    ChatRoom.loadEmojis();
    // 监听按钮
    $("#emojiBtn").on('click', function () {
      if ($("#emojiList").hasClass("showList")) {
        $("#emojiList").removeClass("showList");
      } else {
        $("#emojiList").addClass("showList");
        setTimeout(function () {
          $("body").unbind();
          $("body").click(function() {
            $("#emojiList").removeClass("showList");
            $("body").unbind();
          });
        }, 100);
      }
    });

    // 红包初始化
    $("#redPacketBtn").on('click', function () {
      Util.alert("" +
          "<div class=\"form fn__flex-column\">\n" +
          "<label>\n" +
          "  <div class=\"ft__smaller ft__fade\" style=\"float: left\">积分</div>\n" +
          "  <div class=\"fn-hr5 fn__5\"></div>\n" +
          "  <input type=\"number\" min=\"1\" max=\"20000\" required=\"\" value=\"32\" id=\"redPacketMoney\" onkeypress=\"return(/[\\d]/.test(String.fromCharCode(event.keyCode)))\">\n" +
          "</label>\n" +
          "<label>\n" +
          "  <div class=\"ft__smaller ft__fade\" style=\"float: left\">个数</div>\n" +
          "  <div class=\"fn-hr5 fn__5\"></div>\n" +
          "  <input type=\"number\" min=\"1\" max=\"1000\" required=\"\" value=\"2\" id=\"redPacketCount\" onkeypress=\"return(/[\\d]/.test(String.fromCharCode(event.keyCode)))\">\n" +
          "</label>\n" +
          "<label>\n" +
          "  <div class=\"ft__smaller ft__fade\" style=\"float: left\">留言</div>\n" +
          "  <div class=\"fn-hr5 fn__5\"></div>\n" +
          "  <input type=\"text\" id=\"redPacketMsg\" placeholder=\"摸鱼者，事竟成！\" maxlength=\"20\">\n" +
          "</label>\n" +
          "<div class=\"fn-hr5\"></div>\n" +
          "<div class=\"fn__flex\" style=\"margin-top: 15px\">\n" +
          "  <div class=\"fn__flex-1 fn__flex-center\" style=\"text-align: left;\">总计：<span id=\"redPacketAmount\">32</span> 积分</div>\n" +
          "  <button class=\"btn btn--confirm\" id=\"redPacketConfirm\">发送</button>\n" +
          "</div>\n" +
          "</div>" +
          "", "发红包");

      $("#redPacketMoney").unbind();
      $("#redPacketCount").unbind();

      $("#redPacketMoney").on('change', function () {
        if ($("#redPacketMoney").val() === "") {
          $("#redPacketMoney").val("0");
        }
        if ($("#redPacketMoney").val() > 20000) {
          $("#redPacketMoney").val("20000");
        }
        if ($("#redPacketMoney").val() <= 0) {
          $("#redPacketMoney").val("1");
        }
        $("#redPacketAmount").text($("#redPacketMoney").val());
      });

      $("#redPacketCount").on('change', function () {
        if (Number($("#redPacketCount").val()) > Number($("#redPacketMoney").val())) {
          $("#redPacketCount").val($("#redPacketMoney").val());
        } else {
          console.log()
          console.log($("#redPacketCount").val())
          console.log($("#redPacketMoney").val())
          if ($("#redPacketCount").val() > 1000) {
            $("#redPacketCount").val("1000");
          }
          if ($("#redPacketCount").val() <= 0) {
            $("#redPacketCount").val("1");
          }
        }
      });

      $("#redPacketConfirm").on('click', function () {
        let money = $("#redPacketMoney").val();
        let count = $("#redPacketCount").val();
        let msg = $("#redPacketMsg").val();
        if (msg === '') {
          msg = '摸鱼者，事竟成！';
        }
        let content = {
          money: money,
          count: count,
          msg: msg
        }
        let requestJSONObject = {
          content: "[redpacket]" + JSON.stringify(content) + "[/redpacket]",
        }
        $.ajax({
          url: Label.servePath + '/chat-room/send',
          type: 'POST',
          cache: false,
          data: JSON.stringify(requestJSONObject),
          success: function (result) {
            if (0 !== result.code) {
              $('#chatContentTip').
              addClass('error').
              html('<ul><li>' + result.msg + '</li></ul>')
            }
          },
          error: function (result) {
            $('#chatContentTip').
            addClass('error').
            html('<ul><li>' + result.statusText + '</li></ul>')
          }
        })
        Util.closeAlert();
      })
    });
  },
  delEmoji: function (url) {
    let emojis = ChatRoom.getEmojis();
    for (let i = 0; i < emojis.length; i++) {
      if (emojis[i] === url) {
        emojis.splice(i, 1);
      }
    }
    $.ajax({
      url: Label.servePath + "/api/cloud/sync",
      method: "POST",
      data: JSON.stringify({
        gameId: "emojis",
        data: emojis
      }),
      headers: {'csrfToken': Label.csrfToken},
      async: false,
      success: function (result) {
        if (result.code === 0) {
          Util.notice("success", 1500, "表情包删除成功。");
          ChatRoom.loadEmojis();
        } else {
          Util.notice("warning", 1500, "表情包删除失败：" + result.msg);
        }
      }
    });
  },
  /**
   * 加载表情
   */
  loadEmojis: function () {
    $("#emojis").html("");
    let emojis = ChatRoom.getEmojis();
    for (let i = 0; i < emojis.length; i++) {
      $("#emojis").append("" +
          "<button>\n" +
          "    <div class=\"divX\" onclick='ChatRoom.delEmoji(\"" + emojis[i] + "\")'>\n" +
          "        <svg style=\"width: 15px; height: 15px;\"><use xlink:href=\"#delIcon\"></use></svg>\n" +
          "    </div>" +
          "    <img style='max-height: 50px' onclick=\"ChatRoom.editor.setValue(ChatRoom.editor.getValue() + '![图片表情](" + emojis[i] + ")')\" class=\"vditor-emojis__icon\" src=\"" + emojis[i] + "\">\n" +
          "</button>");
    }
  },
  /**
   * 上传表情
   */
  listenUploadEmojis: function () {
    $('#uploadEmoji').fileupload({
      acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
      maxFileSize: 5242880,
      multipart: true,
      pasteZone: null,
      dropZone: null,
      url: Label.servePath + '/upload',
      paramName: 'file[]',
      add: function (e, data) {
        ext = data.files[0].type.split('/')[1]

        if (window.File && window.FileReader && window.FileList &&
            window.Blob) {
          var reader = new FileReader()
          reader.readAsArrayBuffer(data.files[0])
          reader.onload = function (evt) {
            var fileBuf = new Uint8Array(evt.target.result.slice(0, 11))
            var isImg = isImage(fileBuf)

            if (!isImg) {
              Util.alert('只允许上传图片!')

              return
            }

            if (evt.target.result.byteLength > 1024 * 1024 * 5) {
              Util.alert('图片过大 (最大限制 5M)')

              return
            }

            data.submit()
          }
        } else {
          data.submit()
        }
      },
      formData: function (form) {
        var data = form.serializeArray()
        return data
      },
      submit: function (e, data) {
      },
      done: function (e, data) {
        var result = {
          result: {
            key: data.result.data.succMap[Object.keys(data.result.data.succMap)[0]]
          }
        }
        ChatRoom.addEmoji(result.result.key);
      },
      fail: function (e, data) {
        Util.alert('Upload error: ' + data.errorThrown)
      },
    })
  },
  // 从URL导入表情包
  fromURL: function () {
    Util.alert("" +
        "<div class=\"form fn__flex-column\">\n" +
        "<label>\n" +
        "  <div class=\"ft__smaller ft__fade\" style=\"float: left\">请输入图片的URL</div>\n" +
        "  <div class=\"fn-hr5 fn__5\"></div>\n" +
        "  <input type=\"text\" id=\"fromURL\">\n" +
        "</label>\n" +
        "<div class=\"fn-hr5\"></div>\n" +
        "<div class=\"fn__flex\" style=\"margin-top: 15px; justify-content: flex-end;\">\n" +
        "  <button class=\"btn btn--confirm\" onclick='ChatRoom.addEmoji($(\"#fromURL\").val());Util.closeAlert();'>导入</button>\n" +
        "</div>\n" +
        "</div>" +
        "", "从URL导入表情包");
    $("#fromURL").unbind();
    $("#fromURL").bind('keypress',function(event){
      if (event.keyCode == "13") {
        ChatRoom.addEmoji($("#fromURL").val());
        Util.closeAlert();
      }
    });
  },
  addEmoji: function (url) {
    let emojis = ChatRoom.getEmojis();
    emojis.push(url);
    $.ajax({
      url: Label.servePath + "/api/cloud/sync",
      method: "POST",
      data: JSON.stringify({
        gameId: "emojis",
        data: emojis
      }),
      headers: {'csrfToken': Label.csrfToken},
      async: false,
      success: function (result) {
        if (result.code === 0) {
          Util.notice("success", 1500, "表情包上传成功。");
          ChatRoom.loadEmojis();
        } else {
          Util.notice("warning", 1500, "表情包上传失败：" + result.msg);
        }
      }
    });
  },
  /**
   * 获取表情包
   */
  getEmojis: function () {
    let ret;
    $.ajax({
      url: Label.servePath + "/api/cloud/get",
      method: "POST",
      data: JSON.stringify({
        gameId: "emojis",
      }),
      headers: {'csrfToken': Label.csrfToken},
      async: false,
      success: function (result) {
        if (result.code === 0 && result.data !== "") {
          ret = Util.parseArray(result.data);
        } else {
          ret = [];
        }
      },
    });
    return ret;
  },
  /**
   * 发送聊天内容
   * @returns {undefined}
   */
  send: function () {
    var content = ChatRoom.editor.getValue()
    var requestJSONObject = {
      content: content,
    }

    $.ajax({
      url: Label.servePath + '/chat-room/send',
      type: 'POST',
      cache: false,
      data: JSON.stringify(requestJSONObject),
      beforeSend: function () {
        $('.form button.red').
          attr('disabled', 'disabled').
          css('opacity', '0.3')
      },
      success: function (result) {
        if (0 === result.code) {
          $('#chatContentTip').removeClass('error succ').html('')

          ChatRoom.editor.setValue('')

        } else {
          $('#chatContentTip').
            addClass('error').
            html('<ul><li>' + result.msg + '</li></ul>')
        }
      },
      error: function (result) {
        $('#chatContentTip').
          addClass('error').
          html('<ul><li>' + result.statusText + '</li></ul>')
      },
      complete: function (jqXHR, textStatus) {
        $('.form button.red').removeAttr('disabled').css('opacity', '1')
      },
    })
  },
  /**
   * 获取更多内容
   * @returns {undefined}
   */
  more: function () {
    page++;
    $.ajax({
      url: Label.servePath + '/chat-room/more?page=' + page,
      type: 'GET',
      cache: false,
      async: false,
      success: function(result) {
        if (result.data.length !== 0) {
          for (let i in result.data) {
            let data = result.data[i];
            let liHtml = ChatRoom.renderMessage(data.userNickname, data.userName, data.userAvatarURL, data.time, data.content, data.oId, Label.currentUser, Label.level3Permitted);
            $('#chats').append(liHtml);
            $('#chats>div.fn-none').show(200);
            $('#chats>div.fn-none').removeClass("fn-none");
            ChatRoom.resetMoreBtnListen();
          }
          Util.listenUserCard();
        } else {
          $("#more").removeAttr("onclick");
          $("#more").html("已经到底啦！");
        }
      }
    });
  },
  /**
   * 监听点击更多按钮关闭事件
   */
  resetMoreBtnListen: function () {
    $("body").unbind();
    $("body").click(function() {
      $("details[open]").removeAttr("open");
    });
  },
  /**
   * 撤回聊天室消息
   * @param oId
   */
  revoke: function (oId) {
    $.ajax({
      url: Label.servePath + '/chat-room/revoke/' + oId,
      type: 'DELETE',
      cache: false,
      success: function(result) {
        if (0 === result.code) {
          Util.alert(result.msg);
        } else {
          Util.alert(result.msg);
        }
      }
    });
  },
  /**
   * 艾特某个人
   */
  at: function (userName, id, justAt) {
    if (justAt === true) {
      let value = ChatRoom.editor.getValue();
      if (value !== "\n") {
        ChatRoom.editor.setValue("@" + userName + "  : " + value);
      } else {
        ChatRoom.editor.setValue("@" + userName + "  : ");
      }
    } else {
      let md = '';
      $.ajax({
        url: Label.servePath + '/cr/raw/' + id,
        method: 'get',
        async: false,
        success: function (result) {
          md = result.replace(/(<!--).*/g, "");
          md = md.replace(/\n/g, "\n> ");
        }
      });
      let value = ChatRoom.editor.getValue();
      if (value !== "\n") {
        ChatRoom.editor.setValue("@" + userName + "  引用：\n> " + md + "\n并说：" + value);
      } else {
        ChatRoom.editor.setValue("@" + userName + "  引用：\n> " + md + "\n并说：");
      }
    }
    ChatRoom.editor.focus();
  },
  /**
   * 渲染抢到红包的人的列表
   *
   * @param who
   */
  renderRedPacket: function (usersJSON) {
    for (let i = 0; i < usersJSON.length; i++) {
      let current = usersJSON[i];
      let currentUserMoney = current.userMoney;
      let currentUserName = current.userName;
      if (currentUserName === Label.currentUser) {
        $("#redPacketIGot").text("抢到了 " + currentUserMoney + " 积分");
      }
      let currentUserTime = current.time;
      let currentUser;
      $.ajax({
        url: Label.servePath + "/user/" + currentUserName,
        type: "GET",
        cache: false,
        async: true,
        headers: {'csrfToken': Label.csrfToken},
        success: function (result) {
          currentUser = result;
          let currentUserAvatar = currentUser.userAvatarURL;
          $("#redPacketList").append(
              "            <li class=\"fn__flex menu__item\">\n" +
              "                <img class=\"avatar avatar--mid\" style=\"margin-right: 10px; background-image: none; background-color: transparent;\" src=\"" + currentUserAvatar + "\">\n" +
              "                <div class=\"fn__flex-1\" style=\"text-align: left !important;\">\n" +
              "                    <h2 class=\"list__user\"><a href=\"" + Label.servePath + "/member/" + currentUserName +"\">" + currentUserName + "</a></h2>\n" +
              "                    <span class=\"ft__fade ft__smaller\">" + currentUserTime + "</span>\n" +
              "                </div>\n" +
              "                <div class=\"fn__flex-center\">" + currentUserMoney + " 积分</div>\n" +
              "            </li>\n");
        }
      });
    }
  },
  /**
   * 拆开红包
   */
  unpackRedPacket: function (oId) {
    $.ajax({
      url: Label.servePath + "/chat-room/red-packet/open",
      method: "POST",
      data: JSON.stringify({
        oId: oId
      }),
      success: function (result) {
        let iGot = "抢红包人数较多，加载中...";
        Util.alert("" +
            "<style>" +
            ".dialog-header-bg {" +
            "border-radius: 4px 4px 0px 0px; background-color: rgb(210, 63, 49); color: rgb(255, 255, 255);" +
            "}" +
            ".dialog-main {" +
            "height: 456px;" +
            "overflow: auto;" +
            "}" +
            "</style>" +
            "<div class=\"fn-hr5\"></div>\n" +
            "<div class=\"fn-hr5\"></div>\n" +
            "<div class=\"fn-hr5\"></div>\n" +
            "<div class=\"fn-hr5\"></div>\n" +
            "<div class=\"ft__center\">\n" +
            "    <div class=\"fn__flex-inline\">\n" +
            "        <img class=\"avatar avatar--small\" src=\"" + result.info.userAvatarURL + "\" style=\"background-image: none; background-color: transparent; width: 20px; height: 20px; margin-right: 0px;\">\n" +
            "        <div class=\"fn__space5\"></div>\n" +
            "        <a href=\"" + Label.servePath + "/member/" + result.info.userName + "\">" + result.info.userName + "</a>'s 红包\n" +
            "    </div>\n" +
            "    <div class=\"fn-hr5\"></div>\n" +
            "    <div class=\"ft__smaller ft__fade\">\n" +
            result.info.msg + "\n" +
            "    </div>\n" +
            "    <div class=\"hongbao__count\" id='redPacketIGot'>\n" +
            iGot +
            "    </div>\n" +
            "    <div class=\"ft__smaller ft__fade\">总计 " + result.info.got + "/" + result.info.count + "</div>\n" +
            "</div>\n" +
            "<div class=\"list\"><ul id=\"redPacketList\">\n" +
            "</ul></div>" +
            "", "红包");
        ChatRoom.renderRedPacket(result.who);
      },
      error: function (result) {
        Util.alert(result.msg);
      }
    });
  },
  /**
   * 消息+1
   */
  plusOne: function () {
    ChatRoom.editor.setValue(Label.latestMessage);
    ChatRoom.send();
  },
  /**
   * 渲染聊天室消息
   */
  renderMessage: function (userNickname, userName, userAvatarURL, time, content, oId, currentUser, isAdmin, addPlusOne) {
    let isRedPacket = false;
    try {
      let msgJSON = $.parseJSON(content.replace("<p>", "").replace("</p>", ""));
      if (msgJSON.msgType === "redPacket") {
        isRedPacket = true;
        content = '' +
            '<div class="hongbao__item fn__flex-inline" onclick="ChatRoom.unpackRedPacket(\'' + oId + '\')">\n' +
            '    <svg class="ft__red hongbao__icon">\n' +
            '        <use xlink:href="#redPacketIcon"></use>\n' +
            '    </svg>\n' +
            '    <div>\n' +
            '        <div>' + msgJSON.msg + '</div>\n' +
            '        <div class="ft__smaller ft__fade">\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</div>';
      }
    } catch (err) {}
    try {
      if (addPlusOne === true) {
        content += "<span id='plusOne' onclick='ChatRoom.plusOne()'><svg style='width: 20px; height: 20px; cursor: pointer;'><use xlink:href='#plusOneIcon'></use></svg></span>";
      }
    } catch (err) {}
    let meTag1 = "";
    let meTag2 = "";
    if (userNickname !== undefined && userNickname !== "") {
      userNickname = userNickname + " (" + userName + ")"
    } else {
      userNickname = userName;
    }
    if (currentUser === userName) {
      meTag1 = " chats__item--me";
      meTag2 = "<a onclick=\"ChatRoom.revoke(" + oId + ")\" class=\"item\">撤回</a>\n";
    }
    if (isAdmin) {
      meTag2 = "<a onclick=\"ChatRoom.revoke(" + oId + ")\" class=\"item\">撤回 (使用管理员权限)</a>\n";
    }
    let newHTML = '<div class="fn-none">';
    newHTML += '<div id="chatroom' + oId + '" class="fn__flex chats__item' + meTag1 + '">\n' +
        '    <a href="/member/' + userName + '">\n' +
        '        <div class="avatar tooltipped__user" aria-label="' + userName + '" style="background-image: url(\'' + userAvatarURL + '\');"></div>\n' +
        '    </a>\n' +
        '    <div class="chats__content">\n' +
        '        <div class="chats__arrow"></div>\n';
    if (currentUser !== userName) {
      newHTML += '<div class="ft__fade ft__smaller" style="padding-bottom: 3px;border-bottom: 1px solid #eee">\n' +
          '    <span class="ft-gray">' + userNickname + '</span>\n' +
          '</div>';
    }
    newHTML += '        <div style="margin-top: 4px" class="vditor-reset ft__smaller ' + Label.chatRoomPictureStatus + '">\n' +
        '            ' + content + '\n' +
        '        </div>\n' +
        '        <div class="ft__smaller ft__fade fn__right">\n' +
        '            ' + time + '\n' +
        '                <span class="fn__space5"></span>\n';
    if (!isRedPacket) {
      newHTML += '                <details class="details action__item fn__flex-center">\n' +
          '                    <summary>\n' +
          '                        ···\n' +
          '                    </summary>\n' +
          '                    <details-menu class="fn__layer">\n' +
          '                        <a onclick=\"ChatRoom.at(\'' + userName + '\', \'' + oId + '\', true)\" class="item">@' + userName + '</a>\n' +
          '                        <a onclick=\"ChatRoom.at(\'' + userName + '\', \'' + oId + '\', false)\" class="item">引用</a>\n' +
          meTag2 +
          '                    </details-menu>\n' +
          '                </details>\n';
    }
    newHTML += '        </div>\n' +
        '    </div>\n' +
        '</div></div>';

    return newHTML;
  }
}

