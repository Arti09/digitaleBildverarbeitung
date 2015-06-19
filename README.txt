Das vorläufige Projekt liegt unter:

netbeans_project\src\main\java\de\htwkleipzig\dbv\project\SignDetection.java
und baut mit dem package de.htwkleipzig.dbv.project
zusätzlich ist die jar einbindung mit einbezogen.

Wir können das auch gerne wieder hochziehen, sollten uns dann nur für eine Stelle wo es liegt entscheiden. Ich habe das fertig gebaute .jar mal mit eingecheckt. Ich weiß das man es eigentlich nicht macht, beschläunigt aber die ausführbarkeit auf jedem System. Das .jar muss einfach nur in den Plugins Ordner innerhalb der imageJ installation.



MAVEN ::

Das Maven pom.xml File kann zum Bauen genutzt werden. Es baut dann in den Ordner : netbeans_project\target.
Danach wird versucht die Datei zu kopieren. Das ist momentan ein fester Pfard im pom.xml. Das müsste noch
mal angepasst werden. : )



NETBEANS ::

Man kann auch netbeans einfach nutzen. Bei Netbeans 8.0 ist Maven mit drin.

Grüße,
Eike