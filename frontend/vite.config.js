import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
  build: {
    outDir: 'dist',
    // Keep the browser targets used before the Vite upgrade.
    target: ['chrome87', 'edge88', 'firefox78', 'safari14'],
    sourcemap: false,
  }
})
