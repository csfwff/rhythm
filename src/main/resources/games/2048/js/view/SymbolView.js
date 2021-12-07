define([ 'jquery', 'underscore', 'backbone', 'model/symbols',
		'text!templates/symbol.html' ], function($, _, Backbone, Symbols,
		SymbolTemplate) {

	var SymbolView = Backbone.View.extend({
		tagName : 'ul',
		className : 'symbols',
		collection : Symbols,
		template : _.template(SymbolTemplate),
		initialize : function() {

		},

		render : function() {
			this.$el.html(this.template({
				symbols : this.collection.toJSON()
			}));
			return this;
		}
	});

	return SymbolView;

});