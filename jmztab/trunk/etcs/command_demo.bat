# print help
java -jar MZTabCommandLine.jar -help

# print error/warn message
java -jar MZTabCommandLine.jar -message code=2000

# check file which no error:
java -Xms2048m -jar MZTabCommandLine.jar -inDir testset -check inFile=prideq_human.txt

# check file which exists errors, print errors to screen:
java -Xms2048m -jar MZTabCommandLine.jar -inDir testset -check inFile=mztab_merged_example.txt

# check file which exists errors, score errors into another file:
java -Xms2048m -jar MZTabCommandLine.jar -inDir testset -check inFile=mztab_merged_example.txt outDir testset -outFile mztab_merged_example.err

# convert PRIDE XML to mzTabFile, print result to screen:
java -Xms2048m -jar MZTabCommandLine.jar -inDir testset -convert inFile=PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE

# convert PRIDE XML to mzTabFile, score result into another File:
java -Xms2048m -jar MZTabCommandLine.jar -inDir testset -convert inFile=PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE -outDir testset -outFile PRIDE_Exp_Complete_Ac_16649_xml.mztab

