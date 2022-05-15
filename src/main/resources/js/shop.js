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
 * @shop controller.
 *
 * @author <a href="https://github.com/adlered">adlered</a>
 * @version 1.0.0.0, May 15, 2022
 */

/**
 * @description Shop
 * @static
 */
var Shop = {
    init: function () {
        Shop.log("Client", "正在连接服务器，请稍等...");
        Shop.resetInput();

        $('body').on('keypress', function(e) {
            if (e.keyCode === 13) {
                let cmd = $(".i-cmd").text();
                Shop.log("Input", cmd);
                $.ajax({
                    url: Label.servePath + "/shop",
                    method: 'post',
                    data: JSON.stringify(
                        {
                            "command": cmd
                        }
                    ),
                    cache: false,
                    async: true
                });
                setTimeout(function () {
                    Shop.resetInput();
                }, 100);
            }
        })

        $('.subInput').on('click', function () {
            setTimeout(function () {
                console.log("Reset cursor")
                $(".i-cmd").focus();
            }, 100);
        });
    },

    log: function (prefix, log) {
        if (prefix === undefined) {
            prefix = 'N/A';
        }
        if (prefix === 'Input') {
            $(".logs").append(
                "\n" +
                "<div class='subLog'>" +
                ">&nbsp;" + log +
                "</div>"
            );
        } else {
            let date = new Date();
            prefix = '[' + ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2) + ':' + ('0' + date.getSeconds()).slice(-2) + '&nbsp;' + prefix + ']&nbsp;';
            $(".logs").append(
                "\n" +
                "<div class='subLog'>" +
                prefix + log +
                "</div>"
            );
        }
    },

    resetInput: function (location, cmd) {
        if (cmd === undefined) {
            cmd = '';
        }
        if (location === undefined) {
            location = '~';
        }
        $(".input").html(
            "<div class='subInput'>" +
            "<span class='i-name'>" + Label.currentUserName + "</span>" +
            "<span class='i-at'>@</span>" +
            "<span class='i-host'>" + Label.servePath.replace("http://", "").replace("https://", "") + "</span>" +
            "<span class='i-colon'>:</span>" +
            "<span class='i-location'>" + location + "</span>" +
            "<span class='i-user-as'>$</span>" +
            "<span class='i-space'>&nbsp;</span>" +
            "<span class='i-cmd' contenteditable='true'>" + cmd + "</span>" +
            "</div>"
        );
        $(".i-cmd").focus();
    }
}

var ShopChannel = {
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
        ShopChannel.ws = new WebSocket(channelServer)

        ShopChannel.ws.onopen = function () {
            Shop.log('Client', '连接成功，已启动命令通道。')
            setInterval(function () {
                ShopChannel.ws.send('-hb-')
            }, 1000 * 55)
        }

        ShopChannel.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data);
            Shop.log('Server', data.message);
        }

        ShopChannel.ws.onclose = function () {
            Shop.log('Client', '与服务端的连接已断开，正在尝试重连...');
            setInterval(function () {
                $.ajax({
                    url: Label.servePath + "/",
                    method: "get",
                    success: function() {
                        location.reload();
                    }
                })
            }, 5000);
        }

        ShopChannel.ws.onerror = function (info) {
            Shop.log('Client', '连接出错，正在尝试重连...');
            setInterval(function () {
                $.ajax({
                    url: Label.servePath + "/",
                    method: "get",
                    success: function() {
                        location.reload();
                    }
                })
            }, 5000);
        }
    },
}
