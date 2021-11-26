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
            panel.append(obj.preClientNickname + " don't rob the landlord!");
        }

        if (turnClientId == client.getClientId()) {
            panel.append("It's your turn. Do you want to rob the landlord? [Y/N] (enter [exit|e] to exit current room)");
            panel.waitInput()
                .then(s => landlordElectResolve(client, panel, s));
        } else {
            panel.append("It's " + obj.nextClientNickname + "'s turn. Please wait patiently for his/her confirmation !");
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
