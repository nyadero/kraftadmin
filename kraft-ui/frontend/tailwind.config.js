import defaultTheme from 'tailwindcss/defaultTheme';
import forms from '@tailwindcss/forms';

module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html"
  ],
  theme: {
    extend: {
      fontFamily: {
          sans: ['Figtree', ...defaultTheme.fontFamily.sans],
      },      
      backgroundColor:{
          'primary': 'blue',
          'secondary': 'green'
      }
  },
  },
  plugins: {
    "@tailwindcss/postcss": {},
    forms
  }
}
