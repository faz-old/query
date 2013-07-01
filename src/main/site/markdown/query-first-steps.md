Allgemeines
===========
Da die derzeitige Polopoly-Version Solr als Suchengine nutzt und unsere früheren Suchen nah an der eigentlichen Suchengine
entwickelt wurden, kam mit dem Upgrade des Projektes von 10.0 auf 10.6 ein eigenes Suchframework in das Projekt. Dieses
versucht den eigentlichen Code nicht von der Suchengine abhängig zu machen, sondern einen eigenen Ansatz zu verwenden, so dass
man bei einer weiteren Umstellung nicht von der Suchdatenbank abhängt.
Des Weiteren ist durch dieses Framework ein leichteres testen möglich.

Hauptklassen, die man kennen sollte
===================================
Das Framework besteht derzeit aus 3 Hauptklassen, die für die Entwicklung eines Queries und eines Suchergebnisses
gebraucht werden.
* **SearchContext** Dies ist die zentrale Einstiegsklasse. Sie erstellt ein Query und führt ein Suchquery auf der
                    implementierten Suchdatenbank aus.
* **Query**         Diese Klasse repräsentiert eine Suchquery für eine Suchdatenbank. Diese erstellt man mittels einer
                    SearchContext-Instanz. Sie enthält Funktionen wie and(), or() und term() womit man eine Query
                    entwickeln kann.
* **SearchResult**  Die innere Klasse SearchResult repräsentiert das Suchergebnis einer Suchabfrage. Diese enthält
                    die sucheigenen Objekte und dient als Adapter in das neue Framework.


Des Weiteren gibt es natürlich auch noch Klassen wie z.B. **SearchSettings** oder **PreparedQuery**, welcher aber
nicht gebraucht werden, um erfolgreich Suchergebnisse zu erhalten.

Das Interface Mapping und der Datencontainer
============================================
In dem Framework gibt es eine Klasse namens **Mapping**, womit man Klassen markiert, die ein Dokumenten-Objekt darstellt,
welches in der dokumentenbasierten Datenbank gespeichert wird. Das Mapping als Objekttyp selber hat mehrere Aufgabe:

Import der Daten in die Datenbank
---------------------------------
Für den Import und der Aufbereitung der Polopoly Daten in der Datenbank wird eine Klasse mit Interface markiert und in
den entsprechenden .content Dateien referenziert. Alle Funktionen innerhalb dieser Klasse können dann mit der Annotation
***@MapToField*** annotiert werden, wo angegeben wird, welches Feld diese Funktion repräsentiert.

Aufbau der Suchqueries mittels Mappings
---------------------------------------
Wenn man eine Suchquery mittels SearchContext erstellt, wird man bei der Funktion term(...) feststellen, dass dort ein
Funktionsparameter namens ***fieldDefinition*** gefordert wird und diese Funktion auch eine Exception wirft, wenn man
sie nicht korrekt anspricht.
Um sich eine Feld-Definition zu erstellen ruft man die Funktion ***createFielDefinitionFor(Mapping)*** einer SearchContext
Instanz auf und bekommt eine Instanz dieser Mapping-Klasse zurück. Diese ist nun so vorbereitet, dass man es in der Query
nutzen kann.
Eine Verwendung dieses Ansatzes wäre z.B.

    ImplementedMapping fieldDef = searchContext.createFieldDefinitionFor(ImplementedMapping.class);
    query.add(query.term(implementedMapping.getExampleMethod()).value("hello world"));

Suchergebnisse erzeugen mittels Mappings
----------------------------------------
Die dritte Verwendung dieses Mapping findet sich in den Suchergebnissen. Wenn man ein Suchergebnis vorliegen hat enthält
dies eine Funktion ***getResultsForMapping(Mapping.class)***, welche einen ***Iterator<Mapping>*** zurück liefert.
In diesem Fall bekommt man einen Iterator auf alle gefundenen Suchergebnisse (die eine Seiteneinschränkung besitzen) welcher
die Suchresultate in Form von Mapping Instanzen zurück gibt.
Eine Verwendung dieses Ansatzes wäre z.B.

    Iterator<ImplementedMapping> resultIt = result.getResultsForMapping(ImplementedMapping.class);
    while(resultIt.hasNext()) {
        ImplementedMapping resultEntry = resultIt.next();
        System.out.println(resultEntry.getExampleMethod());
    }