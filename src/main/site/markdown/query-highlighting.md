Allgemeines
===========
Diverse Implementierungen von Suchmaschinen bieten eine Funktionalität an, dass man den Inhalt der Suchergebnisse nochmals
besonders aufbereitet und Textstellen markiert, die auf die Suchquery matchen.

Highlighting für Felder definieren
----------------------------------
Um ein oder mehrere Felder für ein Resultat zu markieren, dass diese Extra bearbeitet werden, kann man in den Settings
das Highlighting aktivieren. Ein Beispiel dazu wäre:

    settings.addHighlighting().withField(fieldDef.getExampleMethod());

Wie in diesem Beispiel ersichtlich wird wieder eine Feld-Definition benutzt. Man kann auch weitere Felder definieren, indem
man einfach ***withField*** mehrfach aufruft.
Nachdem man in den Settings das Highlighting aktiviert hat werden die Daten der Resultate verändert, so dass man
den normalen Weg weiter benutzen kann (Iterator vom Resultat erzeugen lassen und durchlaufen).

Spezielles Highlighting definieren
----------------------------------
Man kann mittels Settings auch festlegen, wie die entsprechenden Stellen in den Resultaten markiert werden. Hierfür
kann man sich ein Freitext einfallen lassen. Der Standardwert sind leerer Text, so dass man die markierten Stellen
 nicht wi8rklich unterscheiden kann. Ein Beispiel dazu:

    settings.addHighlighting()
        .withField(fieldDef.getExampleMethod()
        .surroundWith("<highlight>", "</highlight");

In diesem Fall werden alle Stellen mit <highlight> / </highlight> umschlossen. Ein praktischer Anwendungsfall wäre
das Umschließen mittels eines Html-Tags, welches eine bestimmte CSS-Klasse enthält. Wenn man dann die Felder ausgibt
werden diese mit den entsprechenden Stylesheets farbig hervor gehoben.