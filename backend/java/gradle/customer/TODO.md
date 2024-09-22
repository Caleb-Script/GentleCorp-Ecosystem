# TODO

- bei filtern mit contact Options die option alle kunden ohne email zu filtern
-  bei versionierung etags sind nur gültig mit ""
- ändere zipCode zu postalCode
- bei konflilkt mit username 409 geben nicht 422
- validierungs nachrichten verbessern
- exception werfen wenn keine addresse gegeben wird
- bei TestGetById und TestGetAll wiederholungen eingrenzen mit allgemeine funktionen wie bei TestCreat
- Test für ungültige passwörte fehlt
- es fehlt ein update für addresse und tests
- bei update 304 wenn es keinen unterschied macht
- admins sollen auch passwörter ändern können
- kontakt liste anzeigen

## IDEEN

- Test für Konfliktfälle: Überprüfen Sie, was passiert, wenn zwei Benutzer gleichzeitig versuchen, denselben Kunden zu aktualisieren.
- Performance-Test: Fügen Sie einen Test hinzu, der eine große Anzahl von Updates in kurzer Zeit durchführt, um die Leistung und Stabilität des Systems zu überprüfen.
- Überprüfen Sie die Konsistenz der Daten nach einem Update: Stellen Sie sicher, dass alle Felder korrekt aktualisiert wurden und keine unerwarteten Änderungen aufgetreten sind.
- Testen Sie das Verhalten bei der Aktualisierung von schreibgeschützten Feldern (falls vorhanden).
- Fügen Sie Tests für die Grenzwerte der Felder hinzu (z.B. maximale Länge von Strings, minimale/maximale Werte für numerische Felder).
- Testen Sie das Verhalten bei der Aktualisierung mit leeren oder null-Werten für optionale Felder.
- Insgesamt haben Sie bereits eine gute Testabdeckung. Die vorgeschlagenen zusätzlichen Tests würden die Robustheit Ihrer Testsuite weiter verbessern.
