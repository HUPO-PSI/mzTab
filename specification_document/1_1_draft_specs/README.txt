1. Conversion of the docx format to asciidoc

- Install pandoc: http://pandoc.org/

- Run the conversion, images will be extracted and placed in the 'ímg/' folder.

> pandoc -s -S mzTab_format_specification_1_1draft.docx -t asciidoc --extract-media=img/ -o mzTab_format_specification_1_1draft.adoc
