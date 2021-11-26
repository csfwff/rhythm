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
            panel.append("You have joined room：" + obj.roomId + ". There are " + obj.roomClientCount + " players in the room now.");
            panel.append("Please wait for other players to join. The game would start at three players!");
            window.imClient.ratelRoomId = obj.roomId + ''
            window.imClient.roomList()
        }else {
            panel.append(obj.clientNickname + " joined room, there are currently " + obj.roomClientCount + " in the room.");
        }
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new RoomJoinSuccessEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
