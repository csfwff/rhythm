;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function GamePokerPlayOrderErrorEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_POKER_PLAY_ORDER_ERROR;
    }

    Utils.extend(GamePokerPlayOrderErrorEventHandler, Handler);

    GamePokerPlayOrderErrorEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append("It is not your turn yet. Please wait for other players!");
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GamePokerPlayOrderErrorEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
