;(function(window, Utils) {
	'use strict';

	function Handler() {
		this.code = null;
		this.log = new Utils.Logger();
	}

	Handler.prototype.getCode = function() {
		return this.code;
	};

	Handler.prototype.handle = function(client, panel, clientTransferData) {};

	window.Handler = Handler;
} (this, this.Utils));
