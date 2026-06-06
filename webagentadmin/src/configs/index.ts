import envDev from "./env/dev";
import envTest from "./env/test";
import envProd from "./env/prod";

export type AppEnv = "dev" | "test" | "prod";

export const compileEnv: AppEnv = (process.env.APP_ENV as AppEnv) || "prod";

const configMap = {
  dev: envDev,
  test: envTest,
  prod: envProd
};

const CONFIG = configMap[compileEnv] ?? envProd;

export default CONFIG;
