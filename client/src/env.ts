export namespace ENV {
  export const CHAEKIT_API_ENDPOINT = getEnvString("CHAEKIT_API_ENDPOINT");
}

function getEnvString(key: string, defaultValue?: string): string {
  const key_ = `VITE_${key}`;
  const value = import.meta.env[key_];

  if (!value) {
    if (!defaultValue) {
      throw new Error(`env ${key_} is not defined`);
    }
    return defaultValue;
  }

  return value as string;
}
