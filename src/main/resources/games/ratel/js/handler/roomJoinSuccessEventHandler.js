;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function RoomJoinSuccessEventHandler() {
        this.code = ClientEventCodes.CODE_ROOM_JOIN_SUCCESS;
    }

    Utils.extend(RoomJoinSuccessEventHandler, Handler);

    RoomJoinSuccessEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);

        var joinClientId = obj.clientId;
        if(client.getClientId() == joinClientId) {
            panel.append("你加入了房间：" + obj.roomId + "。目前有 " + obj.roomClientCount + " 名玩家在房间中。");
            panel.append("三缺一，莫急，等人等人~");
            window.imClient.ratelRoomId = obj.roomId + ''
            window.imClient.roomList()
        }else {
            panel.append(obj.clientNickname + " 加入房间，目前房间内有 " + obj.roomClientCount + " 名玩家。");
        }
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new RoomJoinSuccessEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
