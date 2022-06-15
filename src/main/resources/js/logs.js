var Logs = {

    init: function () {
        Logs.connectWS();
    },

    connectWS: function () {
        // 连接WS
        Logs.ws = new WebSocket(logsChannelURL);
        Logs.ws.onopen = function () {
            console.log("Connected to logs channel websocket.");
        }
        Logs.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data);
            console.log(data);
        }
        Logs.ws.onclose = function () {
            console.log("Disconnected to logs channel websocket.");
        }
        Logs.ws.onerror = function (err) {
            console.log('ERROR', err);
        }
    }

}

$(document).ready(function () {
    Logs.init();
});
