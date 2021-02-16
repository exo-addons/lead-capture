const path = require('path');
const merge = require('webpack-merge');


const webpackProductionConfig = require('./webpack.prod.js');

module.exports = merge(webpackProductionConfig, {
    output: {
        path: 'D:\\\eXo\\servers\\platform-6.2.0-M06\\webapps\\lead-capture',
        filename: 'js/[name].bundle.js'
    }
});