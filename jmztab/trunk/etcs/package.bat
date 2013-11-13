# create a zip file including MZTabInspector jar file
mvn -f MZTabInspector.xml clean package

# create a zip file including MZTabCommandLine jar file
mvn -f MZTabCommandLine.xml clean package

# create a full zip file including MZTabInspector and MZTabCommandLine jar file.
mvn -f mztab_gui.xml clean package
mvn -f mztab_full.xml package
