module.exports = {
  css: {
    loaderOptions: {
      less: {
        modifyVars: {
          'primary-color': '#1890ff',
          'link-color': '#1890ff',
          'border-radius-base': '2px',
          'border-color-base':'#d9d9d9'
        },
        javascriptEnabled: true
      }
    }
  },
  productionSourceMap: false,
  publicPath: '',
  assetsDir: 'static'
}