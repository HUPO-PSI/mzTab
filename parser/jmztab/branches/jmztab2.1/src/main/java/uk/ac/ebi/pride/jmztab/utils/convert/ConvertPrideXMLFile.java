package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.pride.jaxb.model.CvParam;
import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.jaxb.model.Reference;
import uk.ac.ebi.pride.jaxb.model.SampleDescription;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.handler.QuantitationCvParams;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;

/**
 * User: Qingwei
 * Date: 07/06/13
 */
public class ConvertPrideXMLFile extends ConvertFile {
    private PrideXmlReader reader;

    public ConvertPrideXMLFile(File inFile) {
        super(inFile, PRIDE);
        this.reader = new PrideXmlReader(inFile);
        createArchitecture();
        fillData();
    }

    @Override
    protected Metadata convertMetadata() {
        this.metadata = new Metadata();

        // process the references
        loadReferences(reader.getReferences());
        // process the contacts
        loadContacts(reader.getAdmin().getContact());
        // process the experiment params
        loadExperimentParams(reader.getAdditionalParams());
        // process the instrument information
        loadInstrument(reader.getInstrument());
        // set the accession as URI if there's one
        loadURI(reader.getExpAccession());
        // set Ms File
        loadMsRun(reader.getExpAccession());

        return metadata;
    }

    /**
     * Converts the experiment's references into the reference string (DOIs and PubMed ids)
     */
    private void loadReferences(List<Reference> references) {
        if (references == null || references.size() == 0) {
            return;
        }

        for (Reference ref : references) {
            uk.ac.ebi.pride.jaxb.model.Param param = ref.getAdditional();
            if (param == null) {
                continue;
            }

            List<PublicationItem> items = new ArrayList<PublicationItem>();

            // check if there's a DOI
            String doi = getPublicationAccession(param, DAOCvParams.REFERENCE_DOI.getName());
            if (! isEmpty(doi)) {
                items.add(new PublicationItem(PublicationItem.Type.DOI, doi));
            }

            // check if there's a pubmed id
            String pubmed = getPublicationAccession(param, DAOCvParams.REFERENCE_PUBMED.getName());
            if (! isEmpty(pubmed)) {
                items.add(new PublicationItem(PublicationItem.Type.PUBMED, pubmed));
            }

            metadata.addPublicationItems(1, items);
        }
    }

    private String getPublicationAccession(uk.ac.ebi.pride.jaxb.model.Param param, String name) {
        if (param == null || isEmpty(name)) {
            return null;
        }

        // this only makes sense if we have a list of params and an accession!
        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (name.equals(p.getCvLabel())) {
                return p.getAccession();
            }
        }

        return null;
    }

    /**
     * Converts a list of PRIDE JAXB Contacts into an ArrayList of mzTab Contacts.
     */
    private void loadContacts(List<uk.ac.ebi.pride.jaxb.model.Contact> contactList)  {
        // make sure there are contacts to be processed
        if (contactList == null || contactList.size() == 0) {
            return;
        }

        // initialize the return variable
        int id = 1;
        for (uk.ac.ebi.pride.jaxb.model.Contact c : contactList) {
            metadata.addContactName(id, c.getName());
            metadata.addContactAffiliation(id, c.getInstitution());
            if (c.getContactInfo() != null && c.getContactInfo().contains("@")) {
                metadata.addContactEmail(id, c.getContactInfo());
            }
            id++;
        }
    }

    /**
     * Processes the experiment additional params
     * (f.e. quant method, description...).
     */
    private void loadExperimentParams(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return;
        }

        for (uk.ac.ebi.pride.jaxb.model.CvParam p : param.getCvParam()) {
            if (DAOCvParams.EXPERIMENT_DESCRIPTION.getAccession().equals(p.getAccession())) {
                metadata.setDescription(p.getValue());
            } else if (QuantitationCvParams.isQuantificationMethod(p.getAccession())) {
                // check if it's a quantification method
                metadata.setQuantificationMethod(convertParam(p));
            } else if (DAOCvParams.GEL_BASED_EXPERIMENT.getAccession().equals(p.getAccession())) {
                metadata.addCustom(convertParam(p));
            }
        }
    }

    private CVParam convertParam(uk.ac.ebi.pride.jaxb.model.CvParam param) {
        return new CVParam(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());
    }

    private uk.ac.ebi.pride.jaxb.model.CvParam getFirstCvParam(uk.ac.ebi.pride.jaxb.model.Param param) {
        if (param == null) {
            return null;
        }

        if (param.getCvParam().iterator().hasNext()) {
            return param.getCvParam().iterator().next();
        }

        return null;
    }

    /**
     * Checks whether the passed identification object is a decoy hit. This function only checks for
     * the presence of specific cv / user Params.
     *
     * @param identification A PRIDE JAXB Identification object.
     * @return Boolean indicating whether the passed identification is a decoy hit.
     */
    private boolean isDecoyHit(Identification identification) {
        for (uk.ac.ebi.pride.jaxb.model.CvParam param : identification.getAdditional().getCvParam()) {
            if (param.getAccession().equals(DAOCvParams.DECOY_HIT.getAccession()))
                return true;
        }

        for (uk.ac.ebi.pride.jaxb.model.UserParam param : identification.getAdditional().getUserParam()) {
            if ("Decoy Hit".equals(param.getName()))
                return true;
        }

        return false;
    }

    private void loadInstrument(uk.ac.ebi.pride.jaxb.model.Instrument instrument) {
        if (instrument == null) {
            return;
        }

        // handle the source information
        uk.ac.ebi.pride.jaxb.model.Param sourceParam = instrument.getSource();
        CvParam param = getFirstCvParam(sourceParam);
        if (param != null) {
            metadata.addInstrumentSource(1, convertParam(param));
        }

        uk.ac.ebi.pride.jaxb.model.Param detectorParam = instrument.getDetector();
        param = getFirstCvParam(detectorParam);
        if (param != null) {
            metadata.addInstrumentDetector(1, convertParam(param));
        }

        // handle the analyzer information
        if (instrument.getAnalyzerList().getCount() > 0) {
            uk.ac.ebi.pride.jaxb.model.Param analyzerParam = instrument.getAnalyzerList().getAnalyzer().iterator().next();
            param = getFirstCvParam(analyzerParam);
            if (param != null) {
                metadata.addInstrumentAnalyzer(1, convertParam(param));
            }
        }

    }

    private void loadURI(String expAccession) {
        if (isEmpty(expAccession)) {
            return;
        }

        try {
            URI uri = new URI("http://www.ebi.ac.uk/pride/experiment.do?experimentAccessionNumber=" + expAccession);
            metadata.addUri(uri);
        } catch (URISyntaxException e) {
            // do nothing
        }
    }

    private void loadMsRun(String expAccession) {
        if (!inFile.isFile()) {
            return;
        }

        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000564", "PSI mzData file", null));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1000777", "spectrum identifier nativeID format", null));
        try {
            metadata.addMsRunLocation(1, new URL("ftp://ftp.ebi.ac.uk/pub/databases/pride/PRIDE_Exp_Complete_Ac_" + expAccession + ".xml"));
        } catch (MalformedURLException e) {
            // do nothing
        }
    }

    /**
     * Adds the sample parameters (species, tissue, cell type, disease) to the unit and the various subsamples.
     */
    private void loadSubSamples(SampleDescription sampleDescription) {
        if (sampleDescription == null) {
            return;
        }

        Sample sample1 = null;
        Sample sample2 = null;
        Sample sample3 = null;
        Sample sample4 = null;
        Sample sample5 = null;
        Sample sample6 = null;
        Sample sample7 = null;
        Sample sample8 = null;

        Sample noIdSample = null;
        for (CvParam p : sampleDescription.getCvParam()) {
            // check for subsample descriptions
            if (QuantitationCvParams.SUBSAMPLE1_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample1 == null) {
                    sample1 = new Sample(1);
                    metadata.addSample(sample1);
                }
                sample1.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE2_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample2 == null) {
                    sample2 = new Sample(2);
                    metadata.addSample(sample1);
                }
                sample2.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE3_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample3 == null) {
                    sample3 = new Sample(3);
                    metadata.addSample(sample3);
                }
                sample3.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE4_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample4 == null) {
                    sample4 = new Sample(4);
                    metadata.addSample(sample4);
                }
                sample4.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE5_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample5 == null) {
                    sample5 = new Sample(5);
                    metadata.addSample(sample5);
                }
                sample5.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE6_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample6 == null) {
                    sample6 = new Sample(6);
                    metadata.addSample(sample6);
                }
                sample6.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE7_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample7 == null) {
                    sample7 = new Sample(7);
                    metadata.addSample(sample7);
                }
                sample7.setDescription(p.getValue());
                continue;
            }
            if (QuantitationCvParams.SUBSAMPLE8_DESCRIPTION.getAccession().equals(p.getAccession())) {
                if (sample8 == null) {
                    sample8 = new Sample(8);
                    metadata.addSample(sample8);
                }
                sample8.setDescription(p.getValue());
                continue;
            }

            // check if it belongs to a sample
            if (p.getValue() != null && p.getValue().startsWith("subsample")) {
                // get the subsample number
                Pattern subsampleNumberPattern = Pattern.compile("subsample(\\d+)");
                Matcher matcher = subsampleNumberPattern.matcher(p.getValue());

                if (matcher.find()) {
                    Integer id = Integer.parseInt(matcher.group(1));

                    // remove the value
                    p.setValue(null);

                    Sample sample = metadata.getSampleMap().get(id);
                    if (sample == null) {
                        sample = new Sample(id);
                        metadata.addSample(sample);
                    }

                    // add the param depending on the type
                    if ("NEWT".equals(p.getCvLabel())) {
                        sample.addSpecies(1, convertParam(p));
                    } else if ("BRENDA".equals(p.getCvLabel())) {
                        sample.addTissue(1, convertParam(p));
                    } else if ("CL".equals(p.getCvLabel())) {
                        sample.addCellType(1, convertParam(p));
                    } else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                        sample.addDisease(1, convertParam(p));
                    } else if (QuantitationCvParams.isQuantificationReagent(p.getAccession())) {
                        // check if it's a quantification reagent
                        Assay assay = metadata.getAssayMap().get(sample.getId());
                        if (assay == null) {
                            assay = new Assay(sample.getId());
                            assay.setSample(sample);
                            metadata.addAssay(assay);
                        }
                        assay.setQuantificationReagent(convertParam(p));
                    }
                }
            } else {
                Sample u = new Sample(1);
                if (u == null) {
                    noIdSample = new Sample(1);
                } else {
                    noIdSample = u;
                }

                // add the param to the "global" group
                if ("NEWT".equals(p.getCvLabel())) {
                    noIdSample.addSpecies(1, convertParam(p));
                }
                else if ("BTO".equals(p.getCvLabel())) {
                    noIdSample.addTissue(1, convertParam(p));
                }
                else if ("CL".equals(p.getCvLabel())) {
                    noIdSample.addCellType(1, convertParam(p));
                }
                else if ("DOID".equals(p.getCvLabel()) || "IDO".equals(p.getCvLabel())) {
                    //DOID: Human Disease Ontology
                    //IDO: Infectious Disease Ontology
                    noIdSample.addDisease(1, convertParam(p));
                }
            }
        }

        // combine subUnits
        if (noIdSample == null) {
            return;
        }

        SortedMap<Integer, Sample> samples = metadata.getSampleMap();
        if (samples.isEmpty()) {
            metadata.addSample(noIdSample);
        }
    }

    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        return null;
    }

    @Override
    protected MZTabColumnFactory convertPeptideColumnFactory() {
        return null;
    }

    @Override
    protected MZTabColumnFactory convertPSMColumnFactory() {
        return null;
    }

    @Override
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        return null;
    }

    @Override
    protected void fillData() {

    }
}
