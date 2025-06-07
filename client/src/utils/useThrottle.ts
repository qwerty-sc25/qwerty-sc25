import { useEffect, useMemo, useRef } from "react";

export default function useThrottle<T extends (...args: any[]) => any>(
  callback: T,
  delay: number
): T {
  const throttledCallbackRef = useRef<T>(callback);
  const lastCallTimeRef = useRef<number>(0);
  const timeoutIdRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    throttledCallbackRef.current = callback;
  }, [callback]);

  const throttledCallback = useMemo(() => {
    return ((...args: Parameters<T>) => {
      const currentTime = Date.now();

      if (currentTime - lastCallTimeRef.current >= delay) {
        lastCallTimeRef.current = currentTime;
        return throttledCallbackRef.current(...args);
      } else {
        if (timeoutIdRef.current) {
          clearTimeout(timeoutIdRef.current);
        }

        const remainingTime = delay - (currentTime - lastCallTimeRef.current);
        timeoutIdRef.current = setTimeout(() => {
          lastCallTimeRef.current = Date.now();
          throttledCallbackRef.current(...args);
          timeoutIdRef.current = null;
        }, remainingTime);
      }
    }) as T;
  }, [delay]);

  useEffect(() => {
    return () => {
      if (timeoutIdRef.current) {
        clearTimeout(timeoutIdRef.current);
      }
    };
  }, []);

  return throttledCallback;
}
