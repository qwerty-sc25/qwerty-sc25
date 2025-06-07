import "@testing-library/jest-dom";
import { vi, beforeAll, afterAll } from "vitest";

// Mock localStorage for both window and global
const mockLocalStorage = {
  store: {} as Record<string, string>,
  getItem(key: string) {
    return this.store[key] || null;
  },
  setItem(key: string, value: string) {
    this.store[key] = String(value);
  },
  removeItem(key: string) {
    delete this.store[key];
  },
  clear() {
    this.store = {};
  },
};

Object.defineProperty(window, "localStorage", {
  value: mockLocalStorage,
  writable: true,
});

Object.defineProperty(global, "localStorage", {
  value: mockLocalStorage,
  writable: true,
});

// Mock IndexedDB
Object.defineProperty(window, "indexedDB", {
  value: {
    open: vi.fn().mockImplementation(() => ({
      onupgradeneeded: vi.fn(),
      onsuccess: vi.fn(),
      onerror: vi.fn(),
      result: {
        createObjectStore: vi.fn(),
        transaction: vi.fn().mockReturnValue({
          objectStore: vi.fn().mockReturnValue({
            get: vi.fn().mockReturnValue({
              onsuccess: vi.fn(),
              onerror: vi.fn(),
            }),
            put: vi.fn().mockReturnValue({
              onsuccess: vi.fn(),
              onerror: vi.fn(),
            }),
          }),
        }),
      },
    })),
  },
  writable: true,
});

// Mock fetch
global.fetch = vi.fn();

// Mock URL.createObjectURL
Object.defineProperty(URL, "createObjectURL", {
  value: vi.fn(() => "mock-object-url"),
  writable: true,
});

// Mock console methods to reduce noise in tests
const originalError = console.error;
beforeAll(() => {
  console.error = (...args) => {
    if (
      typeof args[0] === "string" &&
      args[0].includes("Warning: ReactDOM.render is deprecated")
    ) {
      return;
    }
    originalError.call(console, ...args);
  };
});

afterAll(() => {
  console.error = originalError;
});
