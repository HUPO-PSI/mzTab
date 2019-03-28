## mzTab - _Reporting MS-based Proteomics and Metabolomics Results_

## Quick Links

|Format|Version|Date|Documents|Reference Implementations|
|------|-------|----|---------|------------------------|
|mzTab-M| 2.0.0 | March 2019 | [HTML](2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.html), [DOCX](2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.docx), [PDF](2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.pdf) | [jmzTab-m](https://github.com/lifs-tools/jmztab-m), [Web Validator](https://github.com/lifs-tools/jmztab-m-webapp) |
|mzTab| 1.0.0 | June 2014 | [DOCX](https://github.com/HUPO-PSI/mzTab/raw/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.docx), [PDF](https://github.com/HUPO-PSI/mzTab/raw/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.pdf) | [jmzTab](https://github.com/PRIDE-Utilities/jmztab), [Web Validator](https://github.com/lifs-tools/jmztab-m-webapp) |

## General
### mzTab-M 2.0
mzTab 2.0 for metabolomics (mzTab-M 2.0) is a non-backwards compatible new specification of mzTab geared towards MS-based small molecule metabolomics and lipidomics experiments. It is based on the same principles as its predecessor and extends it to these new application domains, where necessary.

When you use the mzTab-M 2.0 format, please cite the following publication:

* **[N. Hoffmann et al., Analytical Chemistry 2019.](https://pubs.acs.org/doi/10.1021/acs.analchem.8b04310)  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/30688441).**

### mzTab 1.0
mzTab 1.0 is meant to be a light-weight, tab-delimited file format for proteomics data. The target audience for this format are primarily researchers outside of proteomics. It should be easy to parse and only contain the minimal information required to evaluate the results of a proteomics experiment. One of the goals of this file format is that it, for example, should be possible for a biologist to open such a file in Excel and still be able to "see" the data. This format should also become a way to disseminate proteomics results through protocols such as DAS (http://www.biodas.org).

The aim of the format is to present the results of a proteomics experiment in a computationally accessible overview. The aim is not to provide the detailed evidence for these results, or allow recreating the process which led to the results. Both of these functions are established through links to more detailed representations in other formats, in particular mzIdentML and mzQuantML.

When you use mzTab format version 1.0.0, please cite the following publication:

  * **[J. Griss et al., Mol Cell Proteomics 2014.](http://www.mcponline.org/content/early/2014/06/30/mcp.O113.036681.abstract) [PDF File](http://www.mcponline.org/content/early/2014/06/30/mcp.O113.036681.full.pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/24980485).**


## Specification documents
### Version 2.0.0 for Metabolomics
  
  > Specification document ([HTML](2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.html),[DOCX](2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.docx),[PDF](2_0-metabolomics-release/mzTab_format_specification_2_0-M_release.pdf))
  
**Example Files**
Several example of the format can be download from the next link [here](https://github.com/HUPO-PSI/mzTab/tree/master/examples/2_0-Metabolomics_Release)

Detailed explanation of all examples can be found [here](https://github.com/HUPO-PSI/mzTab/wiki/Examples)

**jmzTab-m API**

The [jmzTab-m API](https://github.com/lifs-tools/jmztab-m) has been implemented using an API first approach, based on the [SWAGGER / OpenAPI Specification version 2](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md) as a basis for domain model generation. The parsing and validation code reuses parts of the previous jmztab implementation, maintaining and extending the error code system, basic parsing and validation capabilities. The library has been structured such that only a limited number of dependencies is necessary for projects wishing to include it to read / write mzTab-m. The [jmzTab-m web application](https://github.com/lifs-tools/jmztab-m-webapp) shows how jmzTab-m can be integrated into a RESTful webservice with a separate HTML/form-based user interface. Based on the API specification, swagger-codegen can be used to generate compatible domain objects in several languages, effectively requiring code to be written only for parsing or writing of mzTab.

When you use jmzTab-m library, please cite the following publication:

 * **N.N.**

### Version 1.0.0 (June 2014)

  > Specification document ([DOCX](https://github.com/HUPO-PSI/mzTab/raw/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.docx), [PDF](https://github.com/HUPO-PSI/mzTab/raw/master/specification_document-releases/1_0-Proteomics-Release/mzTab_format_specification.pdf))

  > The 20 minute guide to mzTab ([DOCX](https://github.com/HUPO-PSI/mzTab/raw/master/specification_document-releases/1_0-Proteomics-Release/20minute_guide_mzTab.docx), [PDF](https://github.com/HUPO-PSI/mzTab/raw/master/specification_document-releases/1_0-Proteomics-Release/20minute_guide_mzTab.pdf))

**Example Files**
Several example of the format can be download from the next link [examples.zip](http://www.ebi.ac.uk/pride/resources/tools/jmztab/latest/examples.zip)

Detailed explanation of all examples can be found [here](../../wiki/Examples)

**jmzTab API**

The main principle behind the design of the [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) core model is to provide an independent light-weight architecture for simplifying the integration of the library in different proteomics/metabolomics software applications. Users can integrate the model into their applications, without the need any other third-party packages. Especially, when users want to recode the model by using other programming languages, and migrates [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) API into other heterogeneous system.

We provide a [tutorial](https://github.com/PRIDE-Utilities/jmztab/wiki/Home) document and a couple of demos to help you to create [metadata](https://github.com/PRIDE-Utilities/jmztab/wiki/jmzTab-Metadata) and [fill data](https://github.com/PRIDE-Utilities/jmztab/wiki/jmzTab-Columns) by calling [**jmzTab**](https://github.com/PRIDE-Utilities/jmztab) API.

When you use jmzTab library, please cite the following publication:

  * **[Qing-Wei Xu et al., Proteomics 2014; Jun;14(11):1328-32](http://onlinelibrary.wiley.com/doi/10.1002/pmic.201300560/abstract). [PDF File](http://onlinelibrary.wiley.com/doi/10.1002/pmic.201300560/pdf).  [PubMed record](http://www.ncbi.nlm.nih.gov/pubmed/24659499).**

## Wiki
For more information you can visit the [mzTab](https://github.com/HUPO-PSI/mzTab/wiki) and [jmzTab](https://github.com/PRIDE-Utilities/jmztab/wiki) wikis.

