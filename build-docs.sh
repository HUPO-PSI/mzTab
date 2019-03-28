#!/bin/bash
OPTS="i:o:c:d:f:r"
NAME=`basename $0`
HELP_STRING="Usage: $NAME -i <input_dir> -o <output_dir> -c <commit_hash> -d <build_date> -f <adoc_file> [-r to remove docker containers after running] [-h for help]\nThe script transforms asciidoc input to HTML5 and PDF using the asciidoctor and asciidoctor-pdf utilities from the asciidoctor docker image.\nIt also transforms the asciidoc input to docx using the pandoc library.\nAll arguments that are not in [] brackets are mandatory."
if ( ! getopts "$OPTS" opt); then
	echo -e $HELP_STRING;
	exit $E_OPTERROR;
fi
INPUT_DIR=""
OUTPUT_DIR=""
COMMIT_HASH=""
BUILD_DATE=""
ADOC_FILE=""
RM_DOCKER=""
while getopts "$OPTS" opt; do
  case $opt in
    i)
      echo "INPUT_DIR='$OPTARG'" >&2
      INPUT_DIR="$OPTARG"
      ;;
    o)
      echo "OUTPUT_DIR='$OPTARG'" >&2
      OUTPUT_DIR="$OPTARG"
      ;;
    c)
      echo "COMMIT_HASH='$OPTARG'" >&2
      COMMIT_HASH="$OPTARG"
      ;;
    d)
      echo "BUILD_DATE='$OPTARG'" >&2
      BUILD_DATE="$OPTARG"
      ;;
    f) 
      echo "ADOC_FILE='$OPTARG'" >&2
      ADOC_FILE="$OPTARG"
      ;;
    h)
      echo "$HELP_STRING" >&2
      exit 1
      ;;
    r)
      RM_DOCKER="--rm"
      echo "RM_DOCKER='$RM_DOCKER'" >&2
      docker rm asciidoc-to-html asciidoc-to-pdf asciidoc-to-docbook
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

USER_GROUP="$(id -u ${USER}):$(id -g ${USER})"
echo "Running docker containers with USER:GROUP=$USER_GROUP"

echo "Building HTML5 version of $INPUT_DIR/$ADOC_FILE in $OUTPUT_DIR"
docker run $RM_DOCKER -u $USER_GROUP -v $INPUT_DIR:/documents/ --name asciidoc-to-html asciidoctor/docker-asciidoctor asciidoctor -d book --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" -D /documents/output $ADOC_FILE

ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi

echo "Building PDF version of $INPUT_DIR/$ADOC_FILE in $OUTPUT_DIR"
docker run $RM_DOCKER -u $USER_GROUP -v $INPUT_DIR:/documents/ --name asciidoc-to-pdf asciidoctor/docker-asciidoctor asciidoctor-pdf -d book --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" -D /documents/output $ADOC_FILE

#cp -R $INPUT_DIR/output/* $OUTPUT_DIR/

ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi

mkdir -p $OUTPUT_DIR/img
cp -R $INPUT_DIR/img/* $OUTPUT_DIR/img/

echo "Building DOCX version of $INPUT_DIR/$ADOC_FILE in $OUTPUT_DIR"
OUTPUT_DOCBOOK="${ADOC_FILE%.*}.xml"
OUTPUT_DOCX="${ADOC_FILE%.*}.docx"

echo "Building Docbook version of $INPUT_DIR/$ADOC_FILE in $OUTPUT_DIR"
docker run $RM_DOCKER -u $USER_GROUP -v $INPUT_DIR:/documents/ --name asciidoc-to-docbook asciidoctor/docker-asciidoctor asciidoctor -d book --backend docbook --attribute="commit-hash=$COMMIT_HASH" --attribute="build-date=$BUILD_DATE" -D /documents/output $ADOC_FILE

echo "Running pandoc to convert from $OUTPUT_DOCBOOK to $OUTPUT_DOCX in $OUTPUT_DIR"
CDIR="$(pwd)"
cd $OUTPUT_DIR
pandoc --from docbook --to docx --output $OUTPUT_DOCX $OUTPUT_DOCBOOK
cd $CDIR

ECODE=$?
if [ ! $ECODE -eq 0 ]; then
  echo "Build failed with exit code $ECODE"
  exit $ECODE
fi
