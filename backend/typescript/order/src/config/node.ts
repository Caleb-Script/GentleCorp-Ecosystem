import { RESOURCES_DIR, config } from './order';
import { env } from './env';
import { hostname } from 'node:os';
import { httpsOptions } from './https';

const { NODE_ENV } = env;

const computername = hostname();
const port = (config.node?.port as number | undefined) ?? 3000;

export const nodeConfig = {
  host: computername,
  port,
  resourcesDir: RESOURCES_DIR,
  httpsOptions,
  nodeEnv: NODE_ENV as
    | 'development'
    | 'PRODUCTION'
    | 'production'
    | 'test'
    | undefined,
} as const;
