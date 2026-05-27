import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // ISSO LIBERA O ACESSO NA REDE!
    port: 5173  // (Opcional) garante que a porta será sempre a mesma
  }
})