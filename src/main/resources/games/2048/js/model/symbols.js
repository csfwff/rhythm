define([ 'backbone', 'model/symbol' ], function(Backbone, Symbol) {

	var Symbols = Backbone.Collection.extend({
		model : Symbol,

		sum : function() {
			return this.reduce(function(memo, symbol) {
				return memo + symbol.get('value');
			}, 0);
		},

		byName : function(name) {
			return this.findWhere({
				'name' : name
			});
		}
	});

	return Symbols;

});