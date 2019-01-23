#!/bin/bash
TRAVIS_BUILD_DIR=${TRAVIS_BUILD_DIR:-$PWD}
echo "TRAVIS_BUILD_DIR=$TRAVIS_BUILD_DIR"
mkdir -p output
docker pull asciidoctor/docker-asciidoctor
docker run -v $TRAVIS_BUILD_DIR/specification_document-developments/2_0-Metabolomics-Draft/:/documents/ --name asciidoc-to-pdf asciidoctor/docker-asciidoctor asciidoctor-pdf -d book -D /documents/output mzTab_format_specification_2_0-M_draft.adoc
docker rm /asciidoc-to-pdf
