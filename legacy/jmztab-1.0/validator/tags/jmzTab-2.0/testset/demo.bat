# print help
java -jar jmztab-2.0-SNAPSHOT.jar -help

# print error/warn message
java -jar jmztab-2.0-SNAPSHOT.jar -message code=2000

# check file which no error:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -check inFile=CPTAC_Progenesis_label_free_mzq.txt

# check file which exists errors, print errors to screen:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -check inFile=mztab_merged_example.txt

# check file which exists errors, score errors into another file:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -check inFile=mztab_merged_example.txt outDir testset -outFile mztab_merged_example.err

# convert PRIDE XML to mzTabFile, print result to screen:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -convert inFile=PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE

# convert PRIDE XML to mzTabFile, score result into another File:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -convert inFile=PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE -outDir testset -outFile PRIDE_Exp_Complete_Ac_16649_xml.mztab

# merge multiple mzTabFile into one mzTabFile, print result to screen:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -merge inFileList=CPTAC_Progenesis_label_free_mzq.txt,mztab_itraq_example.txt

# merge multiple mzTabFile into one mzTabFile, merge same subsample columns, print result to screen:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -merge inFileList=CPTAC_Progenesis_label_free_mzq.txt,mztab_itraq_example.txt combine=true

# merge multiple mzTabFile into one mzTabFile, score result into another file:
java -Xms2048m -jar jmztab-2.0-SNAPSHOT.jar -inDir testset -merge inFileList=CPTAC_Progenesis_label_free_mzq.txt,mztab_itraq_example.txt -outDir testset -outFile combine_CPTAC_Progenesis_label_free_mzq.txt