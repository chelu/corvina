#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "using DIR = $DIR" 
java -Xmx2G -cp  "$DIR/../target/dependency/*:$DIR/../target/corvina-0.0.1-SNAPSHOT.jar" info.joseluismartin.corvina.Corvina