# create a zip per sub-module taking into account the generation of sources, doc and applications
mvn clean package -P jmztab-distribution-build,src,javadoc
