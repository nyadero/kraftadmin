//const path = require('path');
//const defaultTheme = require('tailwindcss/defaultTheme');
//const forms = require('@tailwindcss/forms');
//
///** @type {import('tailwindcss').Config} */
//export default {
//content: [
//    resolve(__dirname, './src/css/**/*.css'),
//    path.resolve(__dirname, '../../src/main/resources/templates/**/*.html'),
//  ],
//  theme: {
//    extend: {
//      fontFamily: {
//        sans: ['Figtree', ...defaultTheme.fontFamily.sans],
//      },
//      backgroundColor: {
//        primary: 'blue',
//        secondary: 'green',
//      },
//    },
//  },
//  plugins: [forms],
//};

//import path from 'path';
//import { fileURLToPath } from 'url';
//import defaultTheme from 'tailwindcss/defaultTheme';
//import forms from '@tailwindcss/forms';
//
//const __dirname = path.dirname(fileURLToPath(import.meta.url));
//
//export default {
//  content: [
//    path.resolve(__dirname, '../src/main/resources/templates/**/*.html'),
//  ],
//  theme: {
//    extend: {
//      fontFamily: {
//        sans: ['Figtree', ...defaultTheme.fontFamily.sans],
//      },
//      backgroundColor: {
//        primary: 'blue',
//        secondary: 'green',
//      },
//    },
//  },
//  plugins: [forms],
//};

import path from "path";
import defaultTheme from "tailwindcss/defaultTheme";
import forms from "@tailwindcss/forms";

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    path.resolve(__dirname, "../src/main/resources/templates/**/*.html"),
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Figtree', ...defaultTheme.fontFamily.sans],
      },
    },
  },
  plugins: [forms],
};

