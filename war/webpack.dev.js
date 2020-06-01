const path = require('path');
const merge = require('webpack-merge');

const webpackCommonConfig = require('./webpack.common.js');

module.exports = merge(webpackCommonConfig, {
    module: {
        rules: [{
            test: /.(ttf|otf|eot|svg|woff(2)?)(\?[a-z0-9]+)?$/,
            use: {
                loader: "file-loader",
                options: {
                    name: "[name].[ext]",
                    context: 'css',
                    outputPath: "fonts/"
                }
            }
        }]
    },
    entry: {
        leadsManagement: './src/main/webapp/vue-app/leadsManagement-dev.js',
        mailTemplatesManagement: './src/main/webapp/vue-app/mailTemplatesManagement-dev.js',
        resourcesManagement: './src/main/webapp/vue-app/resourcesManagement-dev.js',
        compaignsManagement: './src/main/webapp/vue-app/compaignsManagement-dev.js',
        leadCaptureSettings: './src/main/webapp/vue-app/leadCaptureSettings-dev.js'
    },
    output: {
        path: path.join(__dirname, 'target/lead-capture/'),
        filename: 'js/[name].bundle.js'
    },
    devServer: {
        contentBase: path.join(__dirname, 'src/main/webapp'),
        port: 3000
    },
    devtool: 'inline-source-map'
});