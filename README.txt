Das vorl�ufige Projekt liegt unter:

netbeans_project\src\main\java\de\htwkleipzig\dbv\project\SignDetection.java
und baut mit dem package de.htwkleipzig.dbv.project
zus�tzlich ist die jar einbindung mit einbezogen.

Wir k�nnen das auch gerne wieder hochziehen, sollten uns dann nur f�r eine Stelle wo es liegt entscheiden. Ich habe das fertig gebaute .jar mal mit eingecheckt. Ich wei� das man es eigentlich nicht macht, beschl�unigt aber die ausf�hrbarkeit auf jedem System. Das .jar muss einfach nur in den Plugins Ordner innerhalb der imageJ installation.



MAVEN ::

Das Maven pom.xml File kann zum Bauen genutzt werden. Es baut dann in den Ordner : netbeans_project\target.
Danach wird versucht die Datei zu kopieren. Das ist momentan ein fester Pfard im pom.xml. Das m�sste noch
mal angepasst werden. : )



NETBEANS ::

Man kann auch netbeans einfach nutzen. Bei Netbeans 8.0 ist Maven mit drin.

Gr��e,
Eike