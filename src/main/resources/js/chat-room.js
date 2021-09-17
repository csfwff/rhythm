/*
 * Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
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
      counter: 4096,
      placeholder: '聊天室历史记录将永久保存，在发送消息前请三思而后行。',
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
      success: function(result) {
        if (result.data.length !== 0) {
          for (let i in result.data) {
            let data = result.data[i];
            let avatarPart = '<a rel="nofollow" href="/member/' + data.userName +
                '">'
                + '<div class="avatar tooltipped tooltipped-se" aria-label="' +
                data.userName
                + '" style="background-image:url(' + data.userAvatarURL +
                ')"></div>'
                + '</a>'

            let namePart = '<a rel="nofollow" href="/member/' + data.userName +
                '"><span class="ft-gray">' + data.userName +
                '</span></a> <span class="ft-fade"> • ' + data.time + '</span>'

            let liHTML = '<li class="fn-none">'
                + '<div class="fn-flex">'
                + avatarPart
                + '<div class="fn-flex-1">'
                + '<div class="ft-smaller">'
                + namePart
                + '</div>'
                + '<div class="vditor-reset comment' + chatRoomPictureStatus + '">'
                + data.content
                + '</div>'
                + '</div>'
                + '</div>'
                + '</li>'
            $('.list ul li:last').after(liHTML);
            $('.list li:last').slideDown(200)
          }
        } else {
          $("#more").removeAttr("onclick");
          $("#more").html("已经到底啦！");
        }
      }
    });
  }
}

