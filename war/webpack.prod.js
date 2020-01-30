const path = require('path');
const merge = require('webpack-merge');
const webpackCommonConfig = require('./webpack.common.js');

const config = merge(webpackCommonConfig, {
  mode: 'development',
  module: {
    rules: [
      {
        test: /.(ttf|otf|eot|svg|woff(2)?)(\?[a-z0-9]+)?$/,
        use: {
          loader: "file-loader",
          options: {
            name: "/lead-capture/fonts/[name].[ext]",
            emitFile: false
          }
        }
      }
    ]
  },
  entry: {
    leadsManagement: './src/main/webapp/vue-app/leadsManagement.js',
    mailTemplatesManagement: './src/main/webapp/vue-app/mailTemplatesManagement.js',
    leadCaptureSettings: './src/main/webapp/vue-app/leadCaptureSettings.js'

  },
  output: {
    path: path.join(__dirname, 'target/lead-capture/'),
    filename: 'js/[name].bundle.js'
  },
  externals: {
    jquery: '$',
    vuetify: 'Vuetify'
  }
});

module.exports = config;
