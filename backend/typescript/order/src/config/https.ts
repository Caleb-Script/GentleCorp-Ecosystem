
import { type HttpsOptions } from '@nestjs/common/interfaces/external/https-options.interface';
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { RESOURCES_DIR } from './order';

// https://nodejs.org/api/path.html
// https://nodejs.org/api/fs.html
// http://2ality.com/2017/11/import-meta.html

const tlsDir = resolve(RESOURCES_DIR, 'tls');
console.debug('tlsDir = %s', tlsDir);

// public/private keys und Zertifikat fuer TLS
export const httpsOptions: HttpsOptions = {
  key: readFileSync(resolve(tlsDir, 'key.pem')),
  cert: readFileSync(resolve(tlsDir, 'certificate.crt')),
};
