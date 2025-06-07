import { defineConfig } from "vitest/config";
import { resolve } from "path";

export default defineConfig({
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["./src/test/setup.ts"],
    include: ["src/**/*.{test,spec}.{js,ts,tsx}"],
    exclude: ["node_modules", "dist", "cdk/**"],
    coverage: {
      provider: "v8",
      reporter: ["text", "json", "html"],
      exclude: [
        "node_modules/",
        "src/test/",
        "**/*.d.ts",
        "src/api/api.gen.ts",
        "src/routeTree.gen.ts",
      ],
    },
    pool: "forks",
    poolOptions: {
      forks: {
        singleFork: true,
      },
    },
    testTimeout: 10000,
    hookTimeout: 10000,
    teardownTimeout: 10000,
    silent: false,
    passWithNoTests: true,
  },
  resolve: {
    alias: {
      "@": resolve(__dirname, "./src"),
    },
  },
});
