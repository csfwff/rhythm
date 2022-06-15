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
 * @fileoverview open.
 *
 * @author <a>iwpz</a>
 */

/**
 * @description Open
 * @static
 */
var Open = {
  /**
   * @description Apply
   */
  apply: function () {
    var requestJSONObject = {
      openApiDescription: $("#apiKeyDescription")
        .val()
        .replace(/(^\s*)|(\s*$)/g, ""),
      openApiType: $("input[name=apiKeyType]:checked").val(),
    };
    console.log(requestJSONObject);
    $.ajax({
      url: Label.servePath + "/open/apply",
      type: "POST",
      cache: false,
      data: JSON.stringify(requestJSONObject),
      success: function (result, textStatus) {
        if (0 === result.code) {
          window.location.href = Label.servePath + "/open";
        } else {
          $("#applyTip")
            .addClass("error")
            .html("<ul><li>" + result.msg + "</li></ul>");
        }
      },
    });
  },
  /**
   * 页面回车事件绑定
   */
  init: function () {
    // // 注册回车事件
    // $("#registerCaptcha, #registerInviteCode").keyup(function (event) {
    //     if (event.keyCode === 13) {
    //         Verify.register();
    //     }
    // });
    // // 忘记密码回车事件
    // $("#fpwdSecurityCode").keyup(function (event) {
    //     if (event.keyCode === 13) {
    //         Verify.forgetPwd();
    //     }
    // });
    // // 登录密码输入框回车事件
    // $("#loginPassword, #captchaLogin").keyup(function (event) {
    //     if (event.keyCode === 13) {
    //         $('#loginTip').next().click();
    //     }
    // });
    // // 重置密码输入框回车事件
    // $("#rpwdConfirmPassword").keyup(function (event) {
    //     if (event.keyCode === 13) {
    //         Verify.resetPwd();
    //     }
    // });
  },
};
