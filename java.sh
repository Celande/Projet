# !bin/bash

javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Genre.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Localisation.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Document.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Audio.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Livre.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Video.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/CategorieClient.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Client.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/FicheEmprunt.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/CustomTableModel.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar:./resource/lib/swingx-0.9.4.jar:./resource/lib/org.eclipse.ui.workbench_3.7.0.I20110519-0100.jar:./resource/lib/javax.servlet-3.0.0.v201112011016.jar" -d ./build ./source/Mediatheque.java

java -cp "./build:./resource/lib/javax.json-1.0.4.jar:./resource/lib/swingx-0.9.4.jar:./resource/lib/org.eclipse.ui.workbench_3.7.0.I20110519-0100.jar:./resource/lib/javax.servlet-3.0.0.v201112011016.jar" Mediatheque