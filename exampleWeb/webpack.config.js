let path = require('path');
let HtmlWebpackPlugin = require('html-webpack-plugin');
let MiniCssExtractPlugin = require('mini-css-extract-plugin');
let webpack = require('webpack');
const devMode = process.env.NODE_ENV !== 'production'
var basePath = __dirname;
const miniCssLoader = new MiniCssExtractPlugin({
    filename: devMode ? '[name].css' : '[name].[hash].css',
    chunkFilename: devMode ? '[id].css' : '[id].[hash].css',
});

module.exports = {
    context: path.join(basePath, 'src'),
    resolve: {
        extensions: ['.js', '.ts', '.tsx', '.less']
    },
    entry: {
        app: './index.tsx',
        //mainStyle : './less/main.less'
        styles:
            './less/main.less'

    },
    output: {
        path: path.join(basePath, 'dist'),
        filename: '[name].js',
    },
    module: {
        rules: [
            {
                test: /\.tsx$/,
                exclude: /node_modules/,
                loader: 'awesome-typescript-loader',
                options: {
                    useBabel: true,
                },
            },

            {
                test: /\.less$/,
                use: [
                    devMode ? 'style-loader' : MiniCssExtractPlugin.loader,
                    {loader: 'css-loader', options : {sourceMap: true}},
                    {loader: 'less-loader', options : {sourceMap: true}},
                ],

            },
            {
                test: /\.(png|jpg|gif|svg)$/,
                loader: 'file-loader',
                options: {
                    name: 'assets/img/[name].[ext]?[hash]'
                }
            },
        ],
    },
    // For development https://webpack.js.org/configuration/devtool/#for-development
    devtool: 'inline-source-map',
    devServer: {
        port: 8080,
        noInfo: true,
    },
    plugins: [
        //Generate index.html in /dist => https://github.com/ampedandwired/html-webpack-plugin
        new HtmlWebpackPlugin({
            filename: 'index.html', //Name of file in ./dist/
            template: 'index.html', //Name of template in ./src
            hash: true,
        }),
        miniCssLoader

    ],
};