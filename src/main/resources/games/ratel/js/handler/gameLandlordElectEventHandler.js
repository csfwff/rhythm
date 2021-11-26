;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function GameLandlordElectEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_LANDLORD_ELECT;
    }

    Utils.extend(GameLandlordElectEventHandler, Handler);

    GameLandlordElectEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);
        var turnClientId = obj.nextClientId;

        if ("preClientNickname" in obj) {
            panel.append(obj.preClientNickname + "：不抢");
        }

        if (turnClientId == client.getClientId()) {
            panel.append("到你了。你是否要抢地主？【输入 Y 抢地主，输入 N 不抢】 (输入 exit 或 e 离开当前房间)");
            panel.waitInput()
                .then(s => landlordElectResolve(client, panel, s));
        } else {
            panel.append("现在是 " + obj.nextClientNickname + " 出牌，请耐心等待！");
        }
    };

    function landlordElectResolve(client, panel, s) {
        s = s.toLowerCase();
        if (s == "exit" || s == "e") {
            client.send(ServerEventCodes.CODE_CLIENT_EXIT, null, null);
        } else if (s == "y") {
            client.send(ServerEventCodes.CODE_GAME_LANDLORD_ELECT, "TRUE", null);
        } else if (s == "n") {
            client.send(ServerEventCodes.CODE_GAME_LANDLORD_ELECT, "FALSE", null);
        } else {
            panel.append("Invalid options");
            panel.waitInput()
                .then(s => landlordElectResolve(client, panel, s))
        }
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GameLandlordElectEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
