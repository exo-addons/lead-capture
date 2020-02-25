const path = require('path');
const merge = require('webpack-merge');


const webpackProductionConfig = require('./webpack.prod.js');

module.exports = merge(webpackProductionConfig, {
  output: {
    path: 'D:\\exo\\exo-working\\servers\\platform-6.0.0-M19\\webapps\\lead-capture',
    filename: 'js/[name].bundle.js'
  }
});
