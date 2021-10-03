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
 * @fileoverview Message channel via WebSocket.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="https://ld246.com/member/ZephyrJung">Zephyr</a>
 * @version 1.14.0.3, Mar 17, 2019
 */

/**
 * @description Article channel.
 * @static
 */
var ArticleChannel = {
    /**
     * WebSocket instance.
     *
     * @type WebSocket
     */
    ws: undefined,
    /**
     * @description Initializes message channel
     */
    init: function (channelServer) {
        ArticleChannel.ws = new ReconnectingWebSocket(channelServer)
        ArticleChannel.ws.reconnectInterval = 10000

        ArticleChannel.ws.onopen = function () {
            setInterval(function () {
                ArticleChannel.ws.send('-hb-')
            }, 1000 * 60 * 3)
        }

        ArticleChannel.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data)

            if (Label.articleOId !== data.articleId) { // It's not the current article
                return
            }

            switch (data.type) {
                case 'comment':
                    var cmtCount = parseInt(
                        $('.comments-header .article-cmt-cnt').text()) + 1
                    // 总帖数更新
                    $('.comments-header .article-cmt-cnt').text(cmtCount + ' ' + Label.cmtLabel)

                    // 新增第一条评论时到底部的锚点
                    if ($('#comments .list > ul > li').length === 0) {
                        $('.comment-header > .fn-none').show()
                        // 显示预览模式 & 回到底部
                        $('.comments-header > .fn-none').show()
                        // 显示评论
                        $('#articleCommentsPanel').parent().show()
                    }

                    if (0 === Label.userCommentViewMode) { // tranditional view mode
                        $('#comments > .list > ul').append(data.cmtTpl)
                    } else {
                        $('#comments > .list > ul').prepend(data.cmtTpl)
                    }

                    // ua
                    $('#' + data.commentId + ' .cmt-via').text('via ' + Util.getDeviceByUa(data.commentUA))

                    // 回帖高亮，他人回帖不定位，只有自己回帖才定位
                    if (Label.currentUserName === data.commentAuthorName) {
                        Comment._bgFade($('#' + data.commentId))
                    }

                    // 更新回复的回帖
                    if (data.commentOriginalCommentId !== '') {
                        var $originalComment = $('#' + data.commentOriginalCommentId),
                            $replyBtn = $originalComment.find(
                                '.comment-action > .ft-fade > .fn-pointer')
                        if ($replyBtn.length === 1) {
                            $replyBtn.html(' ' + (parseInt($.trim($replyBtn.text())) + 1)
                                + ' ' + Label.replyLabel + ' <span class="'
                                + $replyBtn.find('span').attr('class') + '"></span>')

                            if ($replyBtn.find('svg').attr('class') === 'icon-chevron-up') {
                                $replyBtn.find('svg').removeClass('icon-chevron-up').addClass('icon-chevron-down').find('use').attr('xlink:href', '#chevron-down')
                                $replyBtn.click()
                            }
                        } else {
                            $originalComment.find('.comment-action > .ft-fade').prepend('<span class="fn-pointer ft-smaller fn-left" onclick="Comment.showReply(\''
                                + data.commentOriginalCommentId +
                                '\', this, \'comment-replies\')" style="opacity: 1;"> 1 '
                                + Label.replyLabel +
                                ' <svg class="icon-chevron-down"><use xlink:href="#chevron-down"></use></svg>')
                        }
                    }
                    Util.parseHljs()
                    Util.parseMarkdown()
                    break
                case 'articleHeat':
                    var $heatBar = $('#heatBar'),
                        $heat = $('.heat')

                    if (data.operation === '+') {
                        $heatBar.append('<i class="point"></i>')
                        setTimeout(function () {
                            $heat.width($('.heat').width() + 1 * 3)
                            $heatBar.find('.point').remove()
                        }, 2000)
                    } else {
                        $heat.width($('.heat').width() - 1 * 3)
                        $heatBar.append('<i class="point-remove"></i>')
                        setTimeout(function () {
                            $heatBar.find('.point-remove').remove()
                        }, 2000)
                    }

                    break
                default:
                    console.error('Wrong data [type=' + data.type + ']')
            }

        }

        ArticleChannel.ws.onclose = function () {
        }

        ArticleChannel.ws.onerror = function (err) {
            console.log(err)
        }
    },
}

/**
 * @description Article list channel.
 * @static
 */
var ArticleListChannel = {
    /**
     * WebSocket instance.
     *
     * @type WebSocket
     */
    ws: undefined,
    /**
     * @description Initializes message channel
     */
    init: function (channelServer) {
        ArticleListChannel.ws = new ReconnectingWebSocket(channelServer)
        ArticleListChannel.ws.reconnectInterval = 10000

        ArticleListChannel.ws.onopen = function () {
            setInterval(function () {
                ArticleListChannel.ws.send('-hb-')
            }, 1000 * 60 * 3)
        }

        ArticleListChannel.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data)
            $('.article-list h2 > a[rel=bookmark]').each(function () {
                var id = $(this).data('id').toString()

                if (data.articleId === id) {
                    var $li = $(this).closest('li'),
                        $heat = $li.find('.heat')

                    if (data.operation === '+') {
                        $li.append('<i class="point"></i>')
                        setTimeout(function () {
                            $heat.width($heat.width() + 1 * 3)
                            $li.find('.point').remove()
                        }, 2000)
                    } else {
                        $heat.width($heat.width() - 1 * 3)
                        $li.append('<i class="point-remove"></i>')
                        setTimeout(function () {
                            $li.find('.point-remove').remove()
                        }, 2000)
                    }
                }
            })
        }

        ArticleListChannel.ws.onclose = function () {
            ArticleListChannel.ws.close()
        }

        ArticleListChannel.ws.onerror = function (err) {
            console.log('ERROR', err)
        }
    },
}

var IdleTalkChannel = {
    /**
     * WebSocket instance.
     *
     * @type WebSocket
     */
    ws: undefined,
    /**
     * @description Initializes message channel
     */
    init: function (channelServer) {
        IdleTalkChannel.ws = new ReconnectingWebSocket(channelServer)
        IdleTalkChannel.ws.reconnectInterval = 10000

        IdleTalkChannel.ws.onopen = function () {
            setInterval(function () {
                IdleTalkChannel.ws.send('-hb-')
            }, 1000 * 60 * 3)
        }

        IdleTalkChannel.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data);

            let cmd = data.command;
            switch (data.youAre) {
                case 'sender':
                    let html2 = '' +
                        '<li id="' + data.mapId + '">\n' +
                        '    <div class=\'fn-flex\'>\n' +
                        '<a href="/member/' + cmd.toUserName + '">' +
                        '        <div class="avatar tooltipped tooltipped-ne"\n' +
                        '             aria-label="' + cmd.toUserName + '"\n' +
                        '             style="background-image:url(\'' + cmd.toUserAvatar + '\')"></div>\n' +
                        '</a>' +
                        '        <div class="fn-flex-1">\n' +
                        '            <h2>\n' +
                        '                <a href="/member/' + cmd.toUserName + '">发送给「' + cmd.toUserName + '」的私信</a>\n' +
                        '                <button class="btn fn-right" style="margin-left: 10px" onclick="IdleTalk.revoke(\'' + data.mapId + '\')">撤回</button>\n' +
                        '            </h2>\n' +
                        '            <span class="ft-fade vditor-reset">\n' +
                        '                ' + timeTrans(parseFloat(data.mapId)) + ' · 主题：' + cmd.theme + '\n' +
                        '            </span>\n' +
                        '        </div>\n' +
                        '    </div>\n' +
                        '</li>';
                    $("#sent").prepend(html2);
                    if ($("#sent").find(".nope")[0] !== undefined) {
                        $("#sent").find(".nope")[0].remove();
                    }
                    break;
                case 'receiver':
                    let html = '' +
                        '<li id="' + data.mapId + '">\n' +
                        '    <div class=\'fn-flex\'>\n' +
                        '<a href="/member/' + cmd.fromUserName + '">' +
                        '        <div class="avatar tooltipped tooltipped-ne"\n' +
                        '             aria-label="' + cmd.fromUserName + '"\n' +
                        '             style="background-image:url(\'' + cmd.fromUserAvatar + '\')"></div>\n' +
                        '</a>' +
                        '        <div class="fn-flex-1">\n' +
                        '            <h2>\n' +
                        '                <a href="/member/' + cmd.fromUserName + '">来自「' + cmd.fromUserName + '」的私信</a>\n' +
                        '                <button class="red fn-right" onclick="IdleTalk.seek(\'' + data.mapId + '\', \'' + cmd.fromUserName + '\', \'' + cmd.theme + '\')">查看并销毁</button>\n' +
                        '            </h2>\n' +
                        '            <span class="ft-fade vditor-reset">\n' +
                        '                ' + timeTrans(parseFloat(data.mapId)) + ' · 主题：' + cmd.theme + '\n' +
                        '            </span>\n' +
                        '        </div>\n' +
                        '    </div>\n' +
                        '</li>';
                    $("#received").prepend(html);
                    if ($("#received").find(".nope")[0] !== undefined) {
                        $("#received").find(".nope")[0].remove();
                    }
                    break;
                case 'destroyIdleChatMessage':
                    $("#" + cmd).remove();
                    if ($("#received").find("li")[0] === undefined && $("#received").find(".nope")[0] === undefined) {
                        $("#received").append('<div class="nope"><svg><use xlink:href="#nope"></use></svg> 没有收到任何来信</div>');
                    }
                    if ($("#sent").find("li")[0] === undefined && $("#sent").find(".nope")[0] === undefined) {
                        $("#sent").append('<div class="nope"><svg><use xlink:href="#nope"></use></svg> 没有未读的发信</div>');
                    }
                    break;
            }
        }

        IdleTalkChannel.ws.onclose = function () {
            IdleTalkChannel.ws.close()
        }

        IdleTalkChannel.ws.onerror = function (err) {
            console.log('ERROR', err)
        }
    },
}

/**
 * 转化时间戳
 *
 * @param date
 * @returns {string}
 */
function timeTrans(date) {
    var date = new Date(date);
    var Y = date.getFullYear() + '年';
    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '月';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + '日 ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y + M + D + h + m + s;
}

/**
 * @description Chatroom channel.
 * @static
 */
var ChatRoomChannel = {
    /**
     * WebSocket instance.
     *
     * @type WebSocket
     */
    ws: undefined,
    /**
     * @description Initializes message channel
     */
    init: function (channelServer) {
        ChatRoomChannel.ws = new ReconnectingWebSocket(channelServer)
        ChatRoomChannel.ws.reconnectInterval = 10000

        ChatRoomChannel.ws.onopen = function () {
            setInterval(function () {
                ChatRoomChannel.ws.send('-hb-')
            }, 1000 * 60 * 3)
        }

        ChatRoomChannel.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data)

            switch (data.type) {
                case 'online':
                    $('#onlineCnt').text(data.onlineChatCnt)
                    $("#chatRoomOnlineCnt").html("");
                    for (var user in data.users) {
                        let userInfo = data.users[user];
                        $("#chatRoomOnlineCnt").append("<a target=\"_blank\" title=\"" + userInfo.userName + "\" data-name=\"" + userInfo.userName + "\"\n" +
                            "href=\"" + userInfo.homePage + "\">\n" +
                            "<img class=\"avatar avatar-small tooltipped__user\" aria-name=\"" + userInfo.userName + "\"\n" +
                            "src=\"" + userInfo.userAvatarURL + "\">\n" +
                            "</a>");
                    }
                    break;
                case 'revoke':
                    $("#chatroom" + data.oId).remove();
                    $("#chatindex" + data.oId).remove();
                    break;
                case 'msg':
                    // Chatroom
                    if ($("#chatRoomIndex").length === 0) {
                        let liHTML = ChatRoom.renderMessage(data.userName, data.userAvatarURL, data.time, data.content, data.oId, Label.currentUser, Label.level3Permitted);
                        $('#chats').prepend(liHTML);
                        $('#chats>div.fn-none').show(200);
                        $('#chats>div.fn-none').removeClass("fn-none");
                        ChatRoom.resetMoreBtnListen();
                    }

                    // index
                    if ($("#chatRoomIndex").has("#emptyChatRoom").length !== 0) {
                        $("#emptyChatRoom").remove();
                    }
                    $("#chatRoomIndex").prepend("" +
                        "<li class=\"fn-flex\" id=\"chatindex" + data.oId + "\" style='display: none; border-bottom: 1px solid #eee;'>\n" +
                        "    <a rel=\"nofollow\" href=\"/member/" + data.userName + "\">\n" +
                        "        <div class=\"avatar tooltipped tooltipped-n\"\n" +
                        "             aria-label=\"" + data.userName + "\"\n" +
                        "             style=\"background-image:url('" + data.userAvatarURL + "')\"></div>\n" +
                        "    </a>\n" +
                        "    <div class=\"fn-flex-1\">\n" +
                        "        <div class=\"ft-smaller\">\n" +
                        "            <a rel=\"nofollow\" href=\"/member/" + data.userName + "\">\n" +
                        "                <span class=\"ft-gray\">" + data.userName + "</span>\n" +
                        "            </a>\n" +
                        "        </div>\n" +
                        "        <div class=\"vditor-reset comment " + Label.chatRoomPictureStatus + "\">\n" +
                        "            " + data.content + "\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</li>");
                    if ($("#chatRoomIndex li").length === 11) {
                        $("#chatRoomIndex li:last").remove();
                    }
                    $("#chatRoomIndex li:first").slideDown(200);
                    Util.listenUserCard();
                    break;
            }
        }

        ChatRoomChannel.ws.onclose = function () {
            ChatRoomChannel.ws.close()
        }

        ChatRoomChannel.ws.onerror = function (err) {
            console.log('ERROR', err)
        }
    },
}

/**
 * @description gobang game channel.
 * @static
 */
var GobangChannel = {
    /**
     * WebSocket instance.
     *
     * @type WebSocket
     */
    ws: undefined,
    /**
     * @description Initializes message channel
     */
    init: function (channelServer) {
        GobangChannel.ws = new ReconnectingWebSocket(channelServer)
        GobangChannel.ws.reconnectInterval = 10000

        GobangChannel.ws.onopen = function () {
            setInterval(function () {
                GobangChannel.ws.send('zephyr test')
            }, 1000 * 60 * 3)
        }

        GobangChannel.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data)

            switch (data.type) {
                case 'gobangPlayer':
                    console.log('data.type:>gobangPlayer')
                    break
                case 'msg':
                    console.log('data.type:>msg')
                    break
            }
        }

        GobangChannel.ws.onclose = function () {
            GobangChannel.ws.close()
        }

        GobangChannel.ws.onerror = function (err) {
            console.log('ERROR', err)
        }
    },
}
