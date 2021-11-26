;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ClientConnectEventHandler() {
        this.code = ClientEventCodes.CODE_CLIENT_CONNECT;
    }

    Utils.extend(ClientConnectEventHandler, Handler);

    ClientConnectEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append("成功连接到服务器，欢迎你，鱼油！");
        client.setClientId(parseInt(clientTransferData.data));
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ClientConnectEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
