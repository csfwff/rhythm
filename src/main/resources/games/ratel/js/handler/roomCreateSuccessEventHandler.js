;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function RoomCreateSuccessEventHandler() {
        this.code = ClientEventCodes.CODE_ROOM_CREATE_SUCCESS;
    }

    Utils.extend(RoomCreateSuccessEventHandler, Handler);

    RoomCreateSuccessEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var roomId = JSON.parse(clientTransferData.data).id
        panel.append("你创建了一个房间，ID为 " + roomId);
        panel.append("请等待其他玩家加入！");
        window.imClient.createRoom(roomId + '')
        window.imClient.ratelRoomId = roomId + ''
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new RoomCreateSuccessEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
