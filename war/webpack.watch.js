const path = require('path');
const merge = require('webpack-merge');


const webpackProductionConfig = require('./webpack.prod.js');

module.exports = merge(webpackProductionConfig, {
    output: {
        path: 'D:\\\eXo\\servers\\platform-6.1.0-M03\\webapps\\lead-capture',
        filename: 'js/[name].bundle.js'
    }
});