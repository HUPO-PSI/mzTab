#!/bin/bash

for i in *.docx;
do
  echo "Transforming to asciidoc:"
  fname=${i%.*}
  echo -e "  Input file: $i"
  echo -e "  Output file: $fname"
  pandoc --from=docx --wrap=none --atx-headers --normalize -s -S $i -t asciidoc --extract-media=img/ -o "$fname.adoc"
  echo "Transformation done!"
done
for i in *.adoc;
do
  echo "Tidying adoc file $i"
  # Remove Word's extraneous Toc entries
  sed -i -r -e "s/\[\[_Toc[0-9]+\]\]//g" $i;
  # Remove Word's extaneous Ref entries
  sed -i -r -e "s/\[\[_Ref[0-9]+\]\]//g" $i;
  # Remove long horizontal lines in toc
  sed -i -r -e "s/[_]{2,}$//g" $i
done
echo "Done!"
