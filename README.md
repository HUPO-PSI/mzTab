## mzTab - _Reporting MS-based Proteomics and Metabolomics Results_

[![Build Status](https://travis-ci.org/HUPO-PSI/mzTab.svg?branch=master)](https://travis-ci.org/HUPO-PSI/mzTab)

## General
mzTab has been designed to act as a lightweight, tab-delimited file format for mass spec-derived omics data. It was originally designed for proteomics with limited support for metabolomics (version 1.0). The metabolomics aspects are undergoing further development towards full support in a planned version 2.0 release.

One of the main target audiences for this format is researchers outside of proteomics/metabolomics, such as systems biologists. It should be easy to parse and only contain the minimal information required to evaluate the results of an experiment. One of the goals of this file format is that it, for example, should be possible for a biologist to open such a file in Excel and still be able to "see" the data. This format should also become a way to disseminate proteomics and metabolomics results through protocols such as DAS (http://www.biodas.org).

The aim of the format is to present the results of an experiment in a computationally accessible overview. The aim is not to provide the detailed evidence for these results, or allow recreating the process which led to the results. Both of these functions are established through links to more detailed representations in other formats, in particular mzIdentML and mzQuantML for proteomics ID and quantitation.

When you use the mzTab-M format version 2.0, please cite the following publication:

  * **[N. Hoffmann et al., Analytical Chemistry 2019.](https://pubs.acs.org/doi/10.1021/acs.analchem.8b04310) [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/30688441).**

When you use the mzTab format version 1.0, please cite the following publication:

  * **[J. Griss et al., Mol Cell Proteomics 2014.](http://www.mcponline.org/content/early/2014/06/30/mcp.O113.036681.abstract) [PDF File](http://www.mcponline.org/content/early/2014/06/30/mcp.O113.036681.full.pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/24980485).**

## Current Activities and Software Support

**Version 2.0.0 for Metabolomics**

 * [LIFS](https://lifs.isas.de) will update and finalize the [Python mzTab-M library](https://github.com/lifs-tools/pymzTab-m).
 * [LIFS](https://lifs.isas.de) will be working on implementing support for mzTab-M in [LipidXplorer](https://lifs.isas.de/lipidxplorer) and [LUX Score](https://lifs.isas.de/luxscore) (via the Python mzTab-M library).
 * [LIFS](https://lifs.isas.de) plans to add export of mzTab-M from Skyline in 2020.
 * There is also interest from [MSDK / MzMine](https://msdk.github.io/).
 * [XCMS](https://github.com/sneumann/xcms) has a prototype mzTab-M export.
 * LIFS coordinates with the [Lipidomics Standards Initiative](https://lipidomics-standards-initiative.org/) [Use-cases and best practices for mzTab-M in Lipidomics](https://github.com/lipidomics-standards-initiative/).

**Software with support for mzTab-M 2.0**
 
 * [Lipid Data Analyzer 2 (LDA2)](http://genome.tugraz.at/lda2/lda_description.shtml) has support for mzTab-M as output ([Examples](../../wiki/Examples)).
 * [GNPS](https://gnps.ucsd.edu/ProteoSAFe/static/gnps-splash.jsp) can import of mzTab-M since late last year.
 * [MS-Dial](http://prime.psc.riken.jp/Metabolomics_Software/MS-DIAL/) has support for mzTab-M as output ([Examples](../../wiki/Examples)).
 * [jmzTab-M](https://github.com/lifs-tools/jmzTab-m) provides the reference implementation to read, write and validate mzTab-M 2.0.

If you are interested in helping with any of the planned or ongoing projects, please get in contact!

## Specification documents

**Version 2.0.0 for Metabolomics (March 2019):**

  > Specification document ([adoc](https://github.com/HUPO-PSI/mzTab/blob/master/specification_document-releases/2_0-Metabolomics-Release/mzTab_format_specification_2_0-M_release.adoc), [html](http://hupo-psi.github.io/mzTab/2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.html), [docx](http://hupo-psi.github.io/mzTab/2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.docx), [PDF](http://hupo-psi.github.io/mzTab/2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.pdf))
  
  > Example Files ([Wiki](../../wiki/Examples), [Git](https://github.com/HUPO-PSI/mzTab/tree/master/examples/2_0-Metabolomics_Release))

**Version 1.0.0 (June 2014):**

  > Specification document ([docx](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.docx), [PDF](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.pdf))

  > The 20 minute guide to mzTab ([docx](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/20minute_guide_mzTab.docx), [PDF](https://github.com/HUPO-PSI/mzTab/tree/master/specification_document-releases/1_0-Proteomics-Release/20minute_guide_mzTab.pdf))

  > Example Files ([zip](http://www.ebi.ac.uk/pride/resources/tools/jmztab/latest/examples.zip), [Wiki](../../wiki/Examples))

## jmzTab-m API (for mzTab-M 2.0.+)

  > [jmzTab-m](https://github.com/lifs-tools/jmzTab-m)

  > [jmzTab-m web validator](https://github.com/lifs-tools/jmzTab-m-webapp)
  
When you use the jmzTab-m library, please cite the following publication:

* **[Nils Hoffmann et al., Analytical Chemistry 2019; Sep;](https://pubs.acs.org/doi/10.1021/acs.analchem.9b01987). [PDF File](). [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/31525911).**

## jmzTab API (for mzTab 1.0)

The main principle behind the design of the [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) core model is to provide an independent light-weight architecture for simplifying the integration of the library in different proteomics/metabolomics software applications. Users can integrate the model into their applications, without the need any other third-party packages. Especially, when users want to recode the model by using other programming languages, and migrates [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) API into other heterogeneous system.

We provide a [tutorial](https://github.com/PRIDE-Utilities/jmztab/wiki/Home) document and a couple of demos to help you to create [metadata](https://github.com/PRIDE-Utilities/jmztab/wiki/jmzTab-Metadata) and [fill data](https://github.com/PRIDE-Utilities/jmztab/wiki/jmzTab-Columns) by calling [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) API.

When you use jmzTab library, please cite the following publication:

  * **[Qing-Wei Xu et al., Proteomics 2014; Jun;14(11):1328-32](http://onlinelibrary.wiley.com/doi/10.1002/pmic.201300560/abstract). [PDF File](http://onlinelibrary.wiley.com/doi/10.1002/pmic.201300560/pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/24659499).**

## Wiki
For more information you can visit the [mzTab](https://github.com/HUPO-PSI/mzTab/wiki) and [jmzTab](https://github.com/PRIDE-Utilities/jmztab/wiki) wikis.
