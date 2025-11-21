#!/bin/bash
java --module-path "$HOME/Descargas/javafx-sdk-25.0.1/lib" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media \
     -jar "$(dirname "$0")/Gestion_Hotel-1.0-SNAPSHOT.jar"

read -p "Presiona ENTER para salir..."

