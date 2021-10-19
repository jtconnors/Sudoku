#!/bin/bash

#
# Move to the directory containing this script so we can source the env.sh
# properties that follow
#
cd `dirname $0`

#
# Common properties shared by scripts
#
. env.sh

exec_cmd "java -classpath $TARGET/$MAINJAR --module-path $MODPATH --add-modules=javafx.base,javafx.controls,javafx.graphics,javafx.fxml $MAINCLASS"
