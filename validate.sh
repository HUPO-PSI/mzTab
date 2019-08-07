#!/bin/bash
V_VERSION="1.0.4"
V_URL="http://central.maven.org/maven2/de/isas/mztab/jmztabm-cli/$V_VERSION/jmztabm-cli-$V_VERSION-bin.zip"
V_DIR="../../../examples/2_0-Metabolomics_Release/"
# download and unzip validator
mkdir -p build/validation
cd build/validation
rm -r *
wget $V_URL
if [ $? -ne 0 ]; then
  echo -e "Failed to download validator cli distribution from $V_URL"
  exit 1 
fi
unzip jmztabm-cli-$V_VERSION-bin.zip
cd jmztabm-cli
if [ $? -ne 0 ]; then
  echo "Failed to unzip distribution / cd into directory."
  exit 2
fi
# alternatives: Warn or Error
V_LEVEL="Info"
V_FAILED=()
# run validation for all example files
for i in $(find "$V_DIR" -maxdepth 1 -iname '*.mztab'); do
  echo -e "################################################################################"
  if [[ $i == *"MouseLiver_negative"* ]]; then
    echo -e "# Starting basic validation of $i on level $V_LEVEL"
    java -jar jmztabm-cli-$V_VERSION.jar -check inFile=$i -level $V_LEVEL
  else
    echo -e "# Starting full validation of $i on level $V_LEVEL"
    # semantic validation may take quite some time for larger files  
    java -jar jmztabm-cli-$V_VERSION.jar -check inFile=$i -checkSemantic mappingFile=cv-mapping/mzTab-M-mapping.xml -level $V_LEVEL
  fi
  if [ $? -ne 0 ];
  then
    echo -e "# Validation of file $i failed! Please check console output for errors!"
    V_FAILED+=($i)
  else
    echo -e "# Validation of file $i was successful. Please check console output for hints for improvment!"
  fi
  echo -e "################################################################################"
done

if [ ${#V_FAILED[@]} -ne 0 ]; then
  echo -e "################################################################################"
  echo -e "# Validation failed for ${#V_FAILED[@]} files! Please check the following files:"
  for i in "${V_FAILED[@]}"
  do
   echo "# $i"
  done
  echo -e "################################################################################"
  exit 3
else
  echo -e "################################################################################"
  echo -e "# Validation of all files successful!"
  echo -e "################################################################################"
  exit 0
fi
