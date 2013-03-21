# print help
java -jar jmztab-2.0-SNAPSHOT.jar -help

# print error/warn message
java -jar jmztab-2.0-SNAPSHOT.jar -message code=2000

# check file which no error:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -check inFile=CPTAC_Progenesis_label_free_mzq.txt 

# check file which exists errors, print errors to screen:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -check inFile=mztab_merged_example.txt

# check file which exists errors, score errors into another file:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -check inFile=mztab_merged_example.txt outDir example -outFile mztab_merged_example.err

# convert PRIDE XML to mzTabFile, print result to screen:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -convert inFile=PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE

# convert PRIDE XML to mzTabFile, score result into another File:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -convert inFile=PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE -outDir example -outFile PRIDE_Exp_Complete_Ac_16649_xml.mztab

# merge multiple mzTabFile into one mzTabFile, print result to screen:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -merge inFileList=CPTAC_Progenesis_label_free_mzq.txt,mztab_itraq_example.txt

# merge multiple mzTabFile into one mzTabFile, merge same subsample columns, print result to screen:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -merge inFileList=CPTAC_Progenesis_label_free_mzq.txt,mztab_itraq_example.txt combine=true

# merge multiple mzTabFile into one mzTabFile, score result into another file:
java -jar jmztab-2.0-SNAPSHOT.jar -inDir example -merge inFileList=CPTAC_Progenesis_label_free_mzq.txt,mztab_itraq_example.txt -outDir example -outFile combine_CPTAC_Progenesis_label_free_mzq.txt