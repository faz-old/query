Allgemeines
===========
Das Suchframework erlaubt mittels ***SearchSettings*** das Suchresultat zu beeinflussen. So kann man damit die Sortierung
ändern oder ab einem bestimmten Offset eine bestimmte Anzahl an Ergebnissen zurück geben. Die Settings sind nicht
optional zu betrachten und der ***SearchContext*** hat standardmäßig schon ein Setting vorgegeben.

    searchContext.withSettings();

Dieser Aufruf erstellt eine neue Sucheinstellung und kann dann beim Aufruf von ***execute*** mitgegeben werden. Die Settings
sind als Fluent-Interface definiert, so dass man bei jedem Funktionsaufruf die gleiche Settings Instanz zurück bekommt.

Suchausschnitt anpassen
-----------------------
Um zum Beispiel 30 Ergebnisse ab dem 50. Dokument anzuzeigen, muss man die Settings mit folgenden Optionen füttern:

    settings.startAt(50).withPageSize(30);

Sortierung anpassen
-------------------
Um eine einfache Sortierung eines Feldes zu bewerkstelligen, braucht man wieder eine Feld-Definition. Mit dieser kann man
dann das Feld angeben, das für die Sortierung heran gezogen wird. Ein Beispiel wäre:

    ImplementedMapping fieldDefinition = searchContext.createFieldDefinitionFor(ImplementedMapping.class);
    settings.sortBy(fieldDefinition.getExampleMethod(), Order.ASC);

Um eine Mehrfachsortierung zu benutzen (erst wird nach einem Feld sortiert und innerhalb dieser Sortierung wird nach einem
weiteren Feld sortiert), muss man einfach nur die **sortBy* Funktion mehrfach hintereinander aufrufen.

    ImplementedMapping fieldDefinition = searchContext.createFieldDefinitionFor(ImplementedMapping.class);
    settings
        .sortBy(fieldDefinition.getExampleMethod(), Order.ASC)
        .sortBy(fieldDefinition.getAnotherExampleMethod(), Order.DESC);

Highlighting aktivieren
-----------------------
In manchen Situationen ist es auch hilfreich im Suchergebnis hervorzuheben, wieso gerade dieses Dokument gefunden wurde.
Die bewerkstelligt man mit der Funktion ***addHighlighting*** welches eine ***SearchHighlighter*** Instanz zurück liefert.