# !bin/bash

javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Genre.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Localisation.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Document.java
#javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Audio.java
#javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Livre.java
#javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Video.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/CategorieClient.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Client.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/FicheEmprunt.java
javac -cp "./build:./resource/lib/javax.json-1.0.4.jar" -d ./build ./source/Mediatheque.java

java -cp "./build:./resource/lib/javax.json-1.0.4.jar" Mediatheque