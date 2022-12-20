## mzTab - _Reporting MS-based Proteomics and Metabolomics Results_

[![mzTab-M 2.0 Spec Build Workflow](https://github.com/HUPO-PSI/mzTab/actions/workflows/ci.yml/badge.svg)](https://github.com/HUPO-PSI/mzTab/actions/workflows/ci.yml)
[![mzTab-M 2.0 Example File Validation Workflow](https://github.com/HUPO-PSI/mzTab/actions/workflows/validate.yml/badge.svg)](https://github.com/HUPO-PSI/mzTab/actions/workflows/validate.yml)

## General
mzTab has been designed to act as a lightweight, tab-delimited file format for mass spec-derived omics data. It was originally designed for proteomics with limited support for metabolomics (version 1.0). The metabolomics aspects have been further refined and extended in the mzTab-M 2.0 release.

One of the main target audiences for this format is researchers outside of proteomics/metabolomics, such as systems biologists. It should be easy to parse and only contain the minimal information required to evaluate the results of an experiment. One of the goals of this file format is that it, for example, should be possible for a biologist to open such a file in Excel and still be able to "see" the data. 

The aim of the format is to present the results of an experiment in a computationally accessible overview. The aim is not to provide the detailed evidence for these results, or allow recreating the process which led to the results. Both of these functions are established through links to more detailed representations in other formats, in particular mzIdentML and mzQuantML for proteomics ID and quantitation.

When you use the mzTab-M format version 2.0, please cite the following publication:

  * **[N. Hoffmann et al., Analytical Chemistry 2019.](https://pubs.acs.org/doi/10.1021/acs.analchem.8b04310) [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/30688441).**

When you use the mzTab format version 1.0, please cite the following publication:

  * **[J. Griss et al., Mol Cell Proteomics 2014.](http://www.mcponline.org/content/early/2014/06/30/mcp.O113.036681.abstract) [PDF File](http://www.mcponline.org/content/early/2014/06/30/mcp.O113.036681.full.pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/24980485).**

## Current Activities and Software Support

**Version 2.0.0 for Metabolomics**

 * Work with the [Lipidomics Standards Initiative](https://lipidomics-standards-initiative.org/) to map the reporting checklist to mzTab-M metadata.
 * [Custom mapping file for semantic validation of mzTab-M for Lipidomics](https://github.com/lipidomics-standards-initiative/).

**Help wanted**

* Add export of mzTab-M from Skyline.
* Finalize the [Python mzTab-M library](https://github.com/lifs-tools/pymzTab-m).

**Software with support for mzTab-M 2.0**
 
 * [Lipid Data Analyzer 2 (LDA2)](http://genome.tugraz.at/lda2/lda_description.shtml) has support for mzTab-M as output ([Examples](../../wiki/Examples)).
 * [GNPS](https://gnps.ucsd.edu/ProteoSAFe/static/gnps-splash.jsp) can import mzTab-M since late 2019.
 * [MS-Dial](http://prime.psc.riken.jp/Metabolomics_Software/MS-DIAL/) has support for mzTab-M as output ([Examples](../../wiki/Examples)).
 * [MetaboAnalyst](https://www.metaboanalyst.ca/MetaboAnalyst/docs/Format.xhtml) can import mzTab-M since April 2020.
 * [jmzTab-M](https://github.com/lifs-tools/jmzTab-m) provides the reference implementation to read, write and validate mzTab-M 2.0.
 * [MzMine 3](https://mzmine.github.io) provides feature input and output support via mzTab-M, implemented during [GSoC 2020](https://summerofcode.withgoogle.com/organizations).
 * [LipidXplorer 2](https://github.com/lifs-tools/lipidxplorer) provides preliminary mzTab-M output of identified and quantified lipid features.
 * [XCMS](https://github.com/sneumann/xcms) has a prototype mzTab-M export.
 * [rmztab-m](https://github.com/lifs-tools/rmztabm) provides support in R for reading, writing and validation of mzTab-M files.

If you are interested in helping with any of the planned or ongoing projects, please get in contact!

## Specifications

**Version 2.0.0 for Metabolomics (March 2019):**

  > Specification document ([adoc](https://github.com/HUPO-PSI/mzTab/blob/master/specification_document-releases/2_0-Metabolomics-Release/mzTab_format_specification_2_0-M_release.adoc), [html](http://hupo-psi.github.io/mzTab/2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.html), [docx](http://hupo-psi.github.io/mzTab/2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.docx), [PDF](http://hupo-psi.github.io/mzTab/2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.pdf))
  
  > Example Files ([Wiki](../../wiki/Examples), [Git](https://github.com/HUPO-PSI/mzTab/tree/master/examples/2_0-Metabolomics_Release))

  > Reference implementation ([jmzTab-m](https://github.com/lifs-tools/jmzTab-m) to read, write and validate **mzTab-M 2.0.+**)
  
  > Validator web application ([jmzTab-m web validator](https://github.com/lifs-tools/jmzTab-m-webapp) for **mzTab-M 2.0** and mzTab 1.0 (see below))
  
Please see the [jmzTab-m project README](https://github.com/lifs-tools/jmzTab-M) and the [Maven site](https://lifs-tools.github.io/jmzTab-m/) for an introduction to the object model, creation of custom mzTab-M files and mzTab-M validation. 
  
When you use the jmzTab-m library, please cite the following publication:

* **[Nils Hoffmann et al., Analytical Chemistry 2019; Sep;](https://pubs.acs.org/doi/10.1021/acs.analchem.9b01987). [PDF File](). [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/31525911).**

---

**Version 1.0.0 (June 2014):**

  > Specification document ([docx](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.docx), [PDF](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.pdf))

  > The 20 minute guide to mzTab ([docx](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/20minute_guide_mzTab.docx), [PDF](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/20minute_guide_mzTab.pdf))

  > Example Files ([zip](http://www.ebi.ac.uk/pride/resources/tools/jmztab/latest/examples.zip), [Wiki](../../wiki/Examples))
  
  > Reference implementation ([**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) for **mzTab-M 1.0**)
  
The main principle behind the design of the [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) core model is to provide an independent light-weight architecture for simplifying the integration of the library in different proteomics/metabolomics software applications. Users can integrate the model into their applications, without the need any other third-party packages. Especially, when users want to recode the model by using other programming languages, and migrates [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) API into other heterogeneous system.

We provide a [tutorial](https://github.com/PRIDE-Utilities/jmztab/wiki/Home) document and a couple of demos to help you to create [metadata](https://github.com/PRIDE-Utilities/jmztab/wiki/jmzTab-Metadata) and [fill data](https://github.com/PRIDE-Utilities/jmztab/wiki/jmzTab-Columns) by calling [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) API.

When you use jmzTab library, please cite the following publication:

  * **[Qing-Wei Xu et al., Proteomics 2014; Jun;14(11):1328-32](http://onlinelibrary.wiley.com/doi/10.1002/pmic.201300560/abstract). [PDF File](http://onlinelibrary.wiley.com/doi/10.1002/pmic.201300560/pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/24659499).**


## Wiki
For more information you can visit the [mzTab (for mzTab 1.0 and mzTab-M 2.0)](https://github.com/HUPO-PSI/mzTab/wiki) and [jmzTab 1.0](https://github.com/PRIDE-Utilities/jmztab/wiki) wikis.
