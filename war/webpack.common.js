const path = require('path');
const ExtractTextWebpackPlugin = require('extract-text-webpack-plugin');

const config = {
    context: path.resolve(__dirname, '.'),
    module: {
        rules: [{
                test: /\.css$/,
                use: ['vue-style-loader', 'css-loader']
            },
            {
                test: /\.less$/,
                use: ExtractTextWebpackPlugin.extract({
                    fallback: 'vue-style-loader',
                    use: [{
                            loader: 'css-loader',
                            options: {
                                sourceMap: true,
                                minimize: true
                            }
                        },
                        {
                            loader: 'less-loader',
                            options: {
                                sourceMap: true,
                                minimize: true
                            }
                        }
                    ]
                })
            },
            {
                test: /\.vue$/,
                use: [
                    'vue-loader',
                    'eslint-loader',
                ]
            }
        ]
    },
    externals: {
        vuetify: 'Vuetify',
        vue: 'Vue',
        jquery: '$'
    },
    plugins: [
        new ExtractTextWebpackPlugin('css/leadsManagement.css'),
        new ExtractTextWebpackPlugin('css/mailTemplatesManagement.css'),
        new ExtractTextWebpackPlugin('css/resourcesManagement.css'),
        new ExtractTextWebpackPlugin('css/compaignsManagement.css'),
        new ExtractTextWebpackPlugin('css/leadCaptureSettings.css')
    ]

};

module.exports = config;