var path = require('path');
var BUILD_DIR = path.resolve(__dirname, 'public');
var APP_DIR = path.resolve(__dirname, 'src/main/js');
var config = {
	entry : APP_DIR + '/index.js',
	devtool : 'sourcemaps',
	cache : true,
	output : {
		path : BUILD_DIR,
		filename : 'bundle.js'
	},
	resolve : {
		extensions : [ ".js", ".jsx" ]
	},
	module : {
		rules : [ {
			test : /\.js?/,
			include : APP_DIR,
			use : {
				loader : 'babel-loader',
				options : {
					presets : [ '@babel/preset-react', '@babel/preset-flow' ]
				}
			}
		}, {
			test : /\.scss$/,
			loaders : [ "style-loader", "css-loader", "sass-loader" ]
		} ]
	}
};

module.exports = config;
