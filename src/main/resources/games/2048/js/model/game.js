define([ 'underscore', 'backbone' ], function(_, Backbone) {

	var Game = Backbone.Model.extend({
		defaults : {
			score : 0,
			best : 0,
			won : false
		}
	});

	return Game;
});