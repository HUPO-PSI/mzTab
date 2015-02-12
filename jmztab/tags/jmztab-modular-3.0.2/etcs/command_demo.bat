# print help
java -jar mzTabCLI.jar -help

# print error/warn message
java -jar mzTabCLI.jar -message code=2000

# check file which no error:
java -Xms2048m -jar mzTabCLI.jar -check inFile=testset/prideq_human.mztab

# check file which exists errors, print errors to screen:
java -Xms2048m -jar mzTabCLI.jar -check inFile=testset/mztab_merged_example.mztab

# check file which exists errors, store errors into another file:
java -Xms2048m -jar mzTabCLI.jar -check inFile=testset/mztab_merged_example.txt -outFile testset/mztab_merged_example.err

# convert PRIDE XML to mzTabFile, print result to screen:
java -Xms2048m -jar mzTabCLI.jar -convert inFile=testset/PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE

# convert PRIDE XML to mzTabFile, store result into another File:
java -Xms2048m -jar mzTabCLI.jar -convert inFile=testset/PRIDE_Exp_Complete_Ac_16649.xml format=PRIDE -outFile testset/PRIDE_Exp_Complete_Ac_16649_xml.mztab

