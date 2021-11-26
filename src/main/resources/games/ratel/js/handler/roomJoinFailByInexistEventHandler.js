;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function RoomJoinFailByInexistEventHandler() {
        this.code = ClientEventCodes.CODE_ROOM_JOIN_FAIL_BY_INEXIST;
    }

    Utils.extend(RoomJoinFailByInexistEventHandler, Handler);

    RoomJoinFailByInexistEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);
        panel.append("加入房间失败。房间 " + obj.roomId + " 不存在！");
        client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, info: null, data: null});
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new RoomJoinFailByInexistEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
