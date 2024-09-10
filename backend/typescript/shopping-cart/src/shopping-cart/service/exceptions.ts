import { HttpException, HttpStatus } from '@nestjs/common';

export class EmailExistsException extends HttpException {
  constructor(readonly email: string) {
    super(
      `The email address: ${email} already exists.`,
      HttpStatus.UNPROCESSABLE_ENTITY,
    );
  }
}

/**
 * Exception class for an invalid version number during update.
 */
export class InvalidVersionException extends HttpException {
  constructor(readonly version: number | undefined) {
    super(
      `Die Versionsnummer ${version} existiert nicht.`,
      HttpStatus.PRECONDITION_FAILED,
    );
  }
}

/**
 * Exception class for an outdated version number during update.
 */
export class OutdatedVersionException extends HttpException {
  constructor(readonly version: number) {
    super(
      `Die Versionsnummer ${version} ist veraltet.`,
      HttpStatus.PRECONDITION_FAILED,
    );
  }
}

export class InvalidPatchOperationException extends HttpException {
  constructor(readonly value: string | undefined) {
    super(`${value}`, HttpStatus.UNPROCESSABLE_ENTITY);
  }
}

export class ContactExistsException extends HttpException {
  constructor({ name, firstName }: { name: string; firstName: string }) {
    super(
      `The authorized person ${name} ${firstName} already exists.`,
      HttpStatus.UNPROCESSABLE_ENTITY,
    );
  }
}

export class VersionInvalidException extends HttpException {
    constructor(readonly version: string | undefined) {
        super(
            `Die Versionsnummer ${version} ist ungueltig.`,
            HttpStatus.PRECONDITION_FAILED,
        );
    }
}

/**
 * Exception-Klasse für eine veraltete Versionsnummer beim Ändern.
 */
export class VersionOutdatedException extends HttpException {
    constructor(readonly version: number) {
        super(
            `Die Versionsnummer ${version} ist nicht aktuell.`,
            HttpStatus.PRECONDITION_FAILED,
        );
    }
}
