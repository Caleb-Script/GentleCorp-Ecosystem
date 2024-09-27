import type pino from 'pino';

import { parentLogger } from '../config/logger.js';

export const getLogger: (
  context: string,
  kind?: string,
) => pino.Logger<string> = (context: string, kind = 'class') => {
  const bindings: Record<string, string> = {};
  // "indexed access" auf eine Property, deren Name als Wert im Argument "kind" uebergeben wird
  bindings[kind] = context;
  // https://getpino.io/#/docs/child-loggers
  return parentLogger.child(bindings);
};
