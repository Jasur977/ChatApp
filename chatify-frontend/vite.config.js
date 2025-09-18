import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import nodePolyfills from "rollup-plugin-node-polyfills";

export default defineConfig({
  plugins: [react()],
  define: {
    global: "window", // ✅ make "global" available
  },
  build: {
    rollupOptions: {
      plugins: [nodePolyfills()],
    },
  },
});
