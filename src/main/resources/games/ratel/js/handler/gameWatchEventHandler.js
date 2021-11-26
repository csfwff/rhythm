;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes, Poker) {
    'use strict';

    function GameWatchEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_WATCH;
    }

    Utils.extend(GameWatchEventHandler, Handler);

    GameWatchEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        if (!client.getWatching()) {
            return;
        }

        var rawObj = JSON.parse(clientTransferData.data);
        var rawData = rawObj.data;

        switch (rawObj.code) {
            case ClientEventCodes.CODE_ROOM_JOIN_SUCCESS:
                printJoinPlayerInfo(panel, rawData);
                break;

            // 游戏开始
            case ClientEventCodes.CODE_GAME_STARTING:
                printGameStartInfo(panel, rawData);
                break;

            // 抢地主
            case ClientEventCodes.CODE_GAME_LANDLORD_ELECT:
                printRobLandlord(panel, rawData);
                break;

            // 地主确认
            case ClientEventCodes.CODE_GAME_LANDLORD_CONFIRM:
                printConfirmLandlord(panel, rawData);
                break;

            // 出牌
            case ClientEventCodes.CODE_SHOW_POKERS:
                printPlayPokers(panel, rawData);
                break;

            // 不出（过）
            case ClientEventCodes.CODE_GAME_POKER_PLAY_PASS:
                printPlayPass(panel, rawData);
                break;

            // 玩家退出（此时可以退出观战，修改User.isWatching状态）
            case ClientEventCodes.CODE_CLIENT_EXIT:
                printPlayerExit(client, panel, rawData);
                break;

            // 玩家被提出房间
            case ClientEventCodes.CODE_CLIENT_KICK:
                printKickInfo(panel, rawData);
                break;

            // 游戏结束（此时可以退出观战，修改User.isWatching状态）
            case ClientEventCodes.CODE_GAME_OVER:
                printGameResult(client, panel, rawData);
                break;

            // 其他事件忽略
            default:
                break;
        }
    };

    function printJoinPlayerInfo(panel, rawData) {
        panel.append(Utils.format("Player [{}] joined the room", rawData));
    }

    function printGameStartInfo(panel, rawData) {
        var obj = JSON.parse(rawData);

        panel.append("Game starting");
        panel.append("Player1 : " + obj.player1);
        panel.append("Player2 : " + obj.player2);
        panel.append("Player3 : " + obj.player3);
    }

    function printRobLandlord(panel, rawData) {
        panel.append(Utils.format("Player [{}] didn't choose to become the landlord.", rawData));
    }

    function printConfirmLandlord(panel, rawData) {
        var obj = JSON.parse(rawData);

        panel.append(Utils.format("Player [{}] has become the landlord and gotten three extra cards:", obj.landlord));
        panel.append(Poker.toString(obj.additionalPokers));
    }

    function printPlayPokers(panel, rawData) {
        var obj = JSON.parse(rawData);

        panel.append(Utils.format("Player [{}] 出牌了：:", obj.clientNickname));
        panel.append(Poker.toString(obj.pokers));
    }

    function printPlayPass(panel, rawData) {
        panel.append(Utils.format("Player [{}] passed", rawData));
    }

    function printPlayerExit(client, panel, rawData) {
        panel.append(Utils.format("Player [{}] left the room", rawData));
        quitWatch(client, panel);
    }

    function quitWatch(client, panel) {
        panel.append("This room will be closed!");
        panel.append("Spectating ended. Bye.");
        panel.append("");
        panel.append("");

        client.setWatching(false);
        client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
    }

    function printGameResult(client, panel, rawData) {
        var obj = JSON.parse(rawData);

        panel.append(Utils.format("Player [{}]({}) won the game.", obj.winnerNickname, obj.winnerType));
        quitWatch(client, panel);
    }

    function printKickInfo(panel, rawData) {
        panel.append(Utils.format("Player [{}] has been kicked out for being idle.", rawData));
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GameWatchEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes, this.Poker));
