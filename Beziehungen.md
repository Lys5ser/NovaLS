UML Relationships – Explanation

Dieses Dokument erklärt alle Beziehungen in unserem NovaFS-Projekt.
Es ist verständlich formuliert und eignet sich bestens zum Lernen und Präsentieren.

1. Vererbung (Generalization / extends)

Symbol: durchgehende Linie mit leerem Dreieck
Bedeutung: „IST-EIN“-Beziehung

File → FileSystemEntry

File ist ein FileSystemEntry.
Alle grundlegenden Eigenschaften (Name, createdAt, createdBy, Parent) werden vererbt.
Nur getSize() und content sind spezifisch.

Warum dieser Typ?

starke „IST-EIN“-Beziehung

Wiederverwendung von Code

klare Hierarchie

Directory → FileSystemEntry

Directory ist ebenfalls ein FileSystemEntry.
Ordner besitzen dieselben Basiseigenschaften, bieten aber zusätzlich children.

Warum?

Directory ist ein spezieller Eintrag im Filesystem

vererbt gemeinsame Attribute und Methoden

FileSystemEntry → Comparable<FileSystemEntry>

FileSystemEntry ist vergleichbar.
Nötig für Sortierungen, z. B. in der Suche.

Warum?

Comparable ist ein Interface → wird umgesetzt (implements)

Andere Beziehungstypen wären unpassend

2. Interface-Realisierung (implements)

Symbol: gestrichelte Linie mit leerem Dreieck
Bedeutung: „erfüllt Verhalten“

File → Searchable

Files können durchsucht werden (Name/Content matcht ein Keyword).

Directory → Searchable

Directories sind ebenfalls durchsuchbar (Name matcht Keyword).

Warum Interface?

das Verhalten ist gleich, die Umsetzung unterschiedlich

perfekte Anwendung für Polymorphie

3. Aggregation (HAT-Beziehung)

Symbol: leere Raute (◇) beim „Ganzen“
Bedeutung: „hat Teile, aber die Teile leben unabhängig“

Directory ◇── FileSystemEntry (children)

Ein Directory besitzt viele Einträge.
Diese Einträge können Files oder Directories sein.

Warum Aggregation?

Ein Directory enthält Einträge, aber diese können unabhängig existieren

keine starke Lebensabhängigkeit → daher keine Komposition

4. Assoziationen (normale Linien)

Symbol: einfache durchgehende Linie
Bedeutung: „kennt/referenziert“

User ─── FileSystemEntry (createdBy)

Ein User kann viele Einträge erstellt haben.
Jeder Eintrag hat genau einen Ersteller.

Warum Assoziation?

reine Referenz, keine Besitzlogik

kein „User hat die Einträge in sich“

Directory ─── FileSystemEntry (parent)

Jedes FileSystemEntry hat maximal einen Parent (0..1),
z. B. root hat keinen.

Warum?

Parent ist nur eine Rückreferenz

nicht Bestandteil der Aggregation

keine starke Beziehung

5. Abhängigkeiten (Dependency / uses)

Symbol: gestrichelte Linie mit Pfeil
Bedeutung: „benutzt, aber besitzt nicht“

FileSystemExplorer → Directory

Der Explorer benutzt das root-Directory, um Suchoperationen auszuführen.

Warum Dependency?

Explorer hat Directory nicht „als Teil“

nur methodenbezogener Zugriff

NovaLSBasicGui → Directory & User

Die GUI zeigt das Directory an (Baumdarstellung)
und erzeugt Demo-User/Daten.

Warum Dependency?

GUI arbeitet nur auf vorhandenen Objekten

keine strukturelle Beziehung

Zusammenfassung
| Beziehungstyp              | Symbol                              | Bedeutung                                | Beispiel                      |
| -------------------------- | ----------------------------------- | ---------------------------------------- | ----------------------------- |
| **Vererbung**              | Linie + leeres Dreieck              | IST-EIN                                  | File → FileSystemEntry        |
| **Interface-Realisierung** | gestrichelte Linie + leeres Dreieck | erfüllt Verhalten                        | Directory → Searchable        |
| **Aggregation**            | leere Raute                         | hat mehrere, aber Teile leben unabhängig | Directory ◇── FileSystemEntry |
| **Assoziation**            | Linie                               | kennt / referenziert                     | User ─── FileSystemEntry      |
| **Abhängigkeit**           | gestrichelte Linie                  | benutzt                                  | Explorer → Directory          |
