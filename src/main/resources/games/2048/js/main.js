require.config({

	paths : {
		'jquery' : 'lib/jquery-1.11.0.min',
		'backbone' : 'lib/backbone-min',
		'underscore' : 'lib/underscore-min',
		'swipe' : 'lib/jquery.touchSwipe.min'
	},

	shim : {
		'underscore' : {
			exports : '_'
		},
		'backbone' : {
			deps : [ 'underscore', 'jquery' ],
			exports : 'Backbone'
		}
	}
});

require([ 'view/application' ], function(ApplicationView) {
	new ApplicationView();
});