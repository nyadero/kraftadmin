import { resolve } from 'path';

export default {
  base: '/js/',
  build: {
    outDir: '../src/main/resources/static/js',
    emptyOutDir: true,
    rollupOptions: {
      input: {
//        main: resolve(__dirname, 'src/main.js'),
        dropdown: resolve(__dirname, 'src/js/dropdown.js'),
        formUtils: resolve(__dirname, 'src/js/form-utils.js'),
        analytics: resolve(__dirname, 'src/js/analytics.js'),
        bulkActions: resolve(__dirname, 'src/js/bulk-actions.js'),
        datatable: resolve(__dirname, 'src/js/datatable.js'),
        dragDrop: resolve(__dirname, 'src/js/dragdrop.js'),
        script: resolve(__dirname, 'src/js/script.js'),
        websocket: resolve(__dirname, 'src/js/websocket.js'),
        wysiwyg: resolve(__dirname, 'src/js/wysiwyg.js'),
        error: resolve(__dirname, 'src/js/error.js'),
        heatmap: resolve(__dirname, 'src/js/heatmap.js'),
      },
      output: {
        entryFileNames: '[name].js',
        assetFileNames: '[name].[ext]',
      }
    }
  }
};
