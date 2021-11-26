;(function(window, Utils, Protocol, Panel, ClientEventCodes) {
	'use strict';

	var HandlerLoader = Utils.HandlerLoader;
	var log = new Utils.Logger();

	function WsClient(url) {
		this.url = url;
		this.panel = new Panel();
		this.game = {user: {}, room: {lastPokers: null, lastSellClientNickname: null, lastSellClientType: null}, clientId: -1};
	}

	WsClient.version = "1.0.0";
	WsClient.prototype.init = function() {
		return new Promise((resolve, reject) => {
			this.loadHandler()
				.then(() =>
					this.initProtobuf()
						.then(() => this.initWebsocketConnect(resolve, reject)));
		});
	};

	var handlerPath = [
		"./js/handler/clientNicknameSetEventHandler.js",
		"./js/handler/clientExitEventHandler.js",
		"./js/handler/clientKickEventHandler.js",
		"./js/handler/clientConnectEventHandler.js",
		"./js/handler/showOptionsEventHandler.js",
		"./js/handler/showOptionsSettingEventHandler.js",
		"./js/handler/showOptionsPvpEventHandler.js",
		"./js/handler/showOptionsPveEventHandler.js",
		"./js/handler/showRoomsEventHandler.js",
		"./js/handler/showPokersEventHandler.js",
		"./js/handler/roomCreateSuccessEventHandler.js",
		"./js/handler/roomJoinSuccessEventHandler.js",
		"./js/handler/roomJoinFailByFullEventHandler.js",
		"./js/handler/roomJoinFailByInexistEventHandler.js",
		"./js/handler/roomPlayFailByInexist1EventHandler.js",
		"./js/handler/gameStartingEventHandler.js",
		"./js/handler/gameLandlordElectEventHandler.js",
		"./js/handler/gameLandlordConfirmEventHandler.js",
		"./js/handler/gameLandlordCycleEventHandler.js",
		"./js/handler/gamePokerPlayEventHandler.js",
		"./js/handler/gamePokerPlayRedirectEventHandler.js",
		"./js/handler/gamePokerPlayMismatchEventHandler.js",
		"./js/handler/gamePokerPlayLessEventHandler.js",
		"./js/handler/gamePokerPlayPassEventHandler.js",
		"./js/handler/gamePokerPlayCantPassEventHandler.js",
		"./js/handler/gamePokerPlayInvalidEventHandler.js",
		"./js/handler/gamePokerPlayOrderErrorEventHandler.js",
		"./js/handler/gameOverEventHandler.js",
		"./js/handler/pveDifficultyNotSupportEventHandler.js",
		"./js/handler/gameWatchEventHandler.js",
		"./js/handler/gameWatchSuccessfulEventHandler.js"
	]

	WsClient.prototype.loadHandler = function() {
		var loader = new HandlerLoader();
		var promise = loader.load(handlerPath);

		promise.then(() => {
			this.handlerMap = new Map();
			loader.getHandlers().forEach(handler => {
				var code = handler.getCode();
				if (code != null) this.handlerMap.set(code, handler);
			});
		});

		return promise;
	};

	WsClient.prototype.initProtobuf = function() {
		this.protocol = new Protocol();
		return this.protocol.init();
	};

	WsClient.prototype.initWebsocketConnect = function(resolve, reject) {
		if (window.WebSocket) {
			this.socket = new WebSocket(this.url);

			this.socket.onmessage = (event) => {
				this.protocol.decode(event.data).then(v => this.dispatch(v));
			};
			this.socket.onopen = (event) => {
				log.info("websocket ({}) open", this.url);
				resolve();
			};
			this.socket.onclose = (e) => {
				log.info("websocket ({}) close", this.url);
				reject(e);
			};
			this.socket.onerror = (e) => {
				log.error("Occur a error {}", e);
				reject(e);
			};
		} else {
			log.error("current browser not support websocket");
		}
	};

	WsClient.prototype.dispatch = function(serverTransferData) {
		var handler = this.handlerMap.get(serverTransferData.code);

		if (handler == null || typeof handler == 'undefined') {
			log.warn("not found code:{} handler", serverTransferData.code);
			return;
		}

		try {
			handler.handle(this, this.panel, serverTransferData);
		} catch(e) {
			log.error("handle {} error", serverTransferData, e);
		}
	};

	WsClient.prototype.send = function(code, data, info) {
		var transferData = {
			code: code,
			data: typeof data === "undefined" ? null : data,
			info: typeof info === "undefined" ? null : info
		};
		this.protocol.encode(transferData)
			.then(encodeValue => this.socket.send(encodeValue));
	};

	WsClient.prototype.close = function() {
		this.socket.close();
		this.panel.append("Bye.");
		this.panel.hide();
	};

	// --------------- getter/setter ------------------------

	WsClient.prototype.setUserName = function(nickName) {
		this.game.user.nickName = nickName;
	};

	WsClient.prototype.setWatching = function(watching) {
		this.game.user.watching = watching;
	};

	WsClient.prototype.getWatching = function() {
		return this.game.user.watching;
	};

	WsClient.prototype.setClientId = function(clientId) {
		this.game.clientId = clientId;
	};

	WsClient.prototype.getClientId = function() {
		return this.game.clientId;
	};

	WsClient.prototype.setLastPokers = function(lastPokers) {
		this.game.room.lastPokers = lastPokers;
	};

	WsClient.prototype.setLastSellClientNickname = function(lastSellClientNickname) {
		this.game.room.lastSellClientNickname = lastSellClientNickname;
	};

	WsClient.prototype.setLastSellClientType = function(lastSellClientType) {
		this.game.room.lastSellClientType = lastSellClientType;
	};

	WsClient.prototype.getLastPokers = function() {
		return this.game.room.lastPokers;
	};

	WsClient.prototype.getLastSellClientNickname = function() {
		return this.game.room.lastSellClientNickname;
	};

	WsClient.prototype.getLastSellClientType = function() {
		return this.game.room.lastSellClientType;
	};

	window.WsClient = WsClient;
} (this, this.Utils, this.Protocol, this.Panel, this.ClientEventCodes));
