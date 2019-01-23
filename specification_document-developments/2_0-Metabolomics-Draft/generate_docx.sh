#!/bin/bash
INPUT_ADOC=mzTab_format_specification_2_0-M_draft.adoc
OUTPUT_DOCX="${INPUT_ADOC%.*}.docx"
asciidoctor --backend docbook --out-file - $INPUT_ADOC | pandoc --from docbook --to docx --output $OUTPUT_DOCX
