;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ShowRoomsEventHandler() {
        this.code = ClientEventCodes.CODE_SHOW_ROOMS;
    }

    Utils.extend(ShowRoomsEventHandler, Handler);

    var format = "#  {}  |  {}  |  {}  |  {}  #";

    ShowRoomsEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var rooms = JSON.parse(clientTransferData.data);

        if (Array.isArray(rooms) && rooms.length > 0) {
            panel.append(Utils.format(format, "房间号", "房主", "当前人数", "类型"));
            for (var i in rooms) {
                panel.append(Utils.format(format, rooms[i].roomId, rooms[i].roomOwner, rooms[i].roomClientCount, rooms[i].roomType));
            }
        } else {
            panel.append("目前没有任何房间，创建一个吧！");
        }
        client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS_PVP, data: rooms, info: null});
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ShowRoomsEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
