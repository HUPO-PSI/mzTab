#!/bin/bash
mkdir -p build/validation
cd build/validation
rm -r *
wget http://central.maven.org/maven2/de/isas/mztab/jmztabm-cli/0.9.9/jmztabm-cli-0.9.9-bin.zip
unzip jmztabm-cli-0.9.9-bin.zip
cd jmztabm-cli
# alternatives: Warn or Error
V_LEVEL="Info"
for i in $(find ../../../examples/2_0-Metabolomics_Draft/ -maxdepth 1 -iname '*.mztab'); do
  echo -e "################################################################################"
  echo -e "# Starting validation of $i on level "
  java -jar jmztabm-cli-0.9.9.jar -check inFile=$i -checkSemantic mappingFile=cv-mapping/mzTab-M-mapping.xml -level $V_LEVEL
  if [ $? -ne 0 ];
  then
    echo -e "# Validation of file $i failed! Please check console output for errors!"
    exit 1
  else
    echo -e "# Validation of file $i was successful. Please check console output for hints for improvment!"
  fi
  echo -e "################################################################################"
done
echo -e "Validation of all files successful!"
exit 0
