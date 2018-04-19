
== Working with AsciiDoc

* Homepage: http://asciidoctor.org/

=== Prerequisites
If you want to edit the AsciiDoc documents (*.adoc) in this directory,
you can use any text editor application available on your platform.

However, we recommend to use Atom as an editor (https://atom.io/) together 
with the AsciiDoc Preview extension (https://atom.io/packages/asciidoc-preview).

== Editing / Highlighting AsciiDoc

If you want to edit the document, have a look at the admonitions prefixed with

  IMPORTANT: TODO

They also contain the keyword TODO to make automatic finding of TODO items easier.
Unfortunately, GitHub does not highlight text marked with #highlight me#, so that 
TODOs should be close to the text or section that should be updated.

== Conversion from docx to AsciiDoc

- Install pandoc: http://pandoc.org/

- Run the conversion, images will be extracted and placed in the 'ímg/' folder.

> ./transform_to_adoc.sh

*NOTE* The script will transform all *.docx files in the current directory, renaming them to *.adoc in the process, so now data is being overwritten.

*NOTE* Currently, tables, especially nested ones are not properly converted. 


