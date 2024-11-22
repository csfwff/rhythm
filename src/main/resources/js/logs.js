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
var Logs = {
  init: function () {
    Logs.connectWS();
    Logs.more();
  },

  connectWS: function () {
    // 连接WS
    Logs.ws = new WebSocket(logsChannelURL);
    Logs.ws.onopen = function () {
      console.log("Connected to logs channel websocket.");
    };
    Logs.ws.onmessage = function (evt) {
      var data = JSON.parse(evt.data);
      switch (data.type) {
        case "simple":
          Logs.prependLog(data.key1, data.key2, data.key3, data.data);
          break;
      }
    };
    Logs.ws.onclose = function () {
      console.log("Disconnected to logs channel websocket.");
    };
    Logs.ws.onerror = function (err) {
      console.log("ERROR", err);
    };
  },

  appendLog: function (key1, key2, key3, data) {
    let result = Logs.sumResult(key1, key2, key3, data);
    $("#logsContent").append(result);
  },

  prependLog: function (key1, key2, key3, data) {
    let result = Logs.sumResult(key1, key2, key3, data);
    $("#logsContent").prepend(result);
  },

  sumResult: function (key1, key2, key3, data) {
    // let result = '<div style="padding: 5px 0;font-size: 15px;">';
    // result += '【 <span style="color: #696969">' + key1 + '</span> ';
    // result += '<span style="color: #708090">' + key2 + '</span> 】<br>';
    // result += '「<span style="color: #6A5ACD">' + key3 + '</span>」 ';
    // result += '<span style="color: #1E90FF">' + data + '</span> ';
    // result += '</div>';
    // return result;

    /**
     * 根据key3值不同显示不同颜色 增加(tag)
     * 增加积分 绿色 tag:add
     * 扣除积分 红色 tag:reduce
     * 发送弹幕 #6A5ACD tag:post
     *
     * 其他key值统一为 #6A5ACD tag:handle
     */
    let result = "";
    switch (key3) {
      case  "增加积分":
        result = '<div style="padding: 5px 0;font-size: 15px;">';
        result += '【 <span style="color: #696969">' + key1 + "</span> ";
        result += '<span style="color: #708090">' + key2 + "</span> 】<br>";
        result +=
          '「<span style="color: #99CC66">' + "(add)" + key3 + "</span>」 ";
        result += '<span style="color: #1E90FF">' + data + "</span> ";
        result += "</div>";
        break;
      case  "扣除积分":
        result = '<div style="padding: 5px 0;font-size: 15px;">';
        result += '【 <span style="color: #696969">' + key1 + "</span> ";
        result += '<span style="color: #708090">' + key2 + "</span> 】<br>";
        result +=
          '「<span style="color: #FF6666">' + "(reduce)" + key3 + "</span>」 ";
        result += '<span style="color: #1E90FF">' + data + "</span> ";
        result += "</div>";
        break;
      case  "发送弹幕":
        result = '<div style="padding: 5px 0;font-size: 15px;">';
        result += '【 <span style="color: #696969">' + key1 + "</span> ";
        result += '<span style="color: #708090">' + key2 + "</span> 】<br>";
        result +=
          '「<span style="color: #FF9900">' + "(post)" + key3 + "</span>」 ";
        result += '<span style="color: #1E90FF">' + data + "</span> ";
        result += "</div>";
        break;
      default:
        result = '<div style="padding: 5px 0;font-size: 15px;">';
        result += '【 <span style="color: #696969">' + key1 + "</span> ";
        result += '<span style="color: #708090">' + key2 + "</span> 】<br>";
        result +=
          '「<span style="color: #6A5ACD">' + "(handle)" + key3 + "</span>」 ";
        result += '<span style="color: #1E90FF">' + data + "</span> ";
        result += "</div>";
        break;
    }

    return result;
  },

  page: 1,
  more: function () {
    $.ajax({
      url: Label.servePath + "/logs/more?page=" + Logs.page + "&pageSize=30",
      type: "GET",
      async: false,
      success: function (result) {
        if (0 === result.code) {
          let data = result.data;
          if (data.length === 0) {
            $("#loadMoreBtn").html("没有更多内容了");
          } else {
            data.forEach((log) => {
              Logs.appendLog(log.key1, log.key2, log.key3, log.data);
            });
          }
        }
      },
    });
    Logs.page++;
  },
};

$(document).ready(function () {
  Logs.init();
});
