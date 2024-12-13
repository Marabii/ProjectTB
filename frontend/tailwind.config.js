/** @type {import('tailwindcss').Config} */
export default {
  purge: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  content: [],
  theme: {
    extend: {
      colors: {
        'custom-blue': '#004AAD',
        secondary: '#ADADAD'
      },
    },
  },
  plugins: [],
}
