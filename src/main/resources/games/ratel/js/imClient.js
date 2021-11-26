;(function(window, Panel, Utils) {
	'use strict';

	var log = new Utils.Logger();

	function ImClient(url) {
		this.url = url;
		this.id = '0';
		this.ratelRoomId = '0'
		this.chatRoomId = '0'
	}

	ImClient.version = "1.0.0";
	ImClient.prototype.Connect = function(resolve, reject) {
		if (window.WebSocket) {
			this.socket = new WebSocket(this.url);

			this.socket.onmessage = (event) => {
				var data = JSON.parse(JSON.parse(event.data).data)
				console.log(data)
				if(data.code == 0){
					if(data.type == 'id'){
						this.id = data.data;
					}else if(data.type == 'create'){
						this.joinRoom(JSON.parse(data.data).id + '');
					}else if(data.type == 'chats'){
						var chats = JSON.parse(data.data)
						for(var i in chats){
							if(chats[i].name == this.ratelRoomId + ''){
								console.log(data)
								this.joinRoom(chats[i].id + '')
								break
							}
						}
					}else if(data.type == 'broadcast'){
						window.wsClient.panel.append(data.from.name + ': ' + data.data)
					}
				}
			};
			this.socket.onopen = (event) => {
				log.info("websocket ({}) open", this.url);
			};
			this.socket.onclose = (e) => {
				log.info("websocket ({}) close", this.url);
			};
			this.socket.onerror = (e) => {
				log.error("Occur a error {}", e);
			};
		} else {
			log.error("current browser not support websocket");
		}
	};

	ImClient.prototype.createRoom = function(name){
		this.send({
			type: "create",
			data: name,
		})
	}

	ImClient.prototype.joinRoom = function(roomId){
		if(this.chatRoomId == '0'){
			this.send({
				type: "change",
				data: roomId,
			})
			this.chatRoomId = roomId
		}
	}

	ImClient.prototype.roomList = function(){
		this.send({
			type: "chats"
		})
	}

	ImClient.prototype.setNickname = function(name){
		this.send({
			type: "rename",
			data: name,
		})
	}

	ImClient.prototype.leave = function(){
		this.send({
			type: "leave"
		})
		this.chatRoomId = '0'
	}

	ImClient.prototype.sendMsg = function(msg){
		this.send({
			type: "broadcast",
			data: msg
		})
	}

	ImClient.prototype.send = function(data) {
		if(this.socket){
			this.socket.send(JSON.stringify( {
				id: 0,
				data: JSON.stringify(data)
			}))
		}
	};

	ImClient.prototype.close = function() {
		this.socket.close();
	};

	window.ImClient = ImClient;
} (this, this.Panel, this.Utils));
