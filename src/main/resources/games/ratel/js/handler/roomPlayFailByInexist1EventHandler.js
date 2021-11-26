;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function RoomPlayFailByInexist1EventHandler() {
        this.code = ClientEventCodes.CODE_ROOM_JOIN_FAIL_BY_INEXIST;
    }

    Utils.extend(RoomPlayFailByInexist1EventHandler, Handler);

    RoomPlayFailByInexist1EventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append("Play failed. Room already disbanded!");
        client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new RoomPlayFailByInexist1EventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
