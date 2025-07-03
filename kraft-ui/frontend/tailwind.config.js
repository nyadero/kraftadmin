
import path from 'path';
import { fileURLToPath } from 'url';
import defaultTheme from 'tailwindcss/defaultTheme';
//import forms from '@tailwindcss/forms';

export default {
  content: [
    '../src/main/resources/templates/**/*.html',
     '../src/main/resources/templates/*.html',
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Figtree', ...defaultTheme.fontFamily.sans],
      },
      backgroundColor: {
        primary: 'blue',
        secondary: 'green',
      },
    },
  },
  plugins: [],
};

