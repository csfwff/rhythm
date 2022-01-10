define([ 'backbone' ], function(Backbone) {

	var Symbol = Backbone.Model.extend({
		defaults : {
			'name' : '',
			'value' : 0
		},

		addValue : function(value) {
			this.set('value', this.get('value') + value);
		},

		toString : function() {
			return this.get('name') + ' ' + this.get('value');
		}
	});

	return Symbol;

});