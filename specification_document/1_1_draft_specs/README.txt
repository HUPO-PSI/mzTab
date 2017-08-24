1. Conversion of the docx format to asciidoc

- Install pandoc: http://pandoc.org/

- Run the conversion, images will be extracted and placed in the 'ímg/' folder.

> ./transform_to_adoc.sh

*NOTE* The script will transform all *.docx files in the current directory, renaming them to *.adoc in the process, so now data is being overwritten.

*NOTE* Currently, tables, especially nested ones are not proper. 
