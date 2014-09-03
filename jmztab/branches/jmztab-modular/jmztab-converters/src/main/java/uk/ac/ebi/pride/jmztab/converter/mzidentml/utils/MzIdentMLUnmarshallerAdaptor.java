package uk.ac.ebi.pride.jmztab.converter.mzidentml.utils;

import psidev.psi.tools.xxindex.index.IndexElement;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;

/**
 * MzMLIdentMLUnmarshallerAdaptor provides a list of convenient function to keep in memory information about Spectrum Identification and
 * Protein Hypothesis and PeptideDetectionHypothesis methods to access mzidentML files.
 *
 * @author yperez
 * @since 23/09/11 15:28
 */
public class MzIdentMLUnmarshallerAdaptor extends MzIdentMLUnmarshaller {

    private Map<String, Map<String, List<IndexElement>>> scannedIdMappings;

    private Map<Comparable, SpectraData> spectraDataMapResult = Collections.emptyMap();

    private Map<Comparable, String[]> identSpectrumMap = Collections.emptyMap();

    private List<Comparable> proteinGroupIds = Collections.emptyList();

    private Map<Comparable, List<Comparable>> identProteinsMap = Collections.emptyMap();

    private List<Comparable> proteinIds = Collections.emptyList();

    private Inputs inputs = null;

    private AuditCollection auditCollection = null;

    private Map<Comparable, List<Comparable>> spectraDataMap = Collections.emptyMap();


    public MzIdentMLUnmarshallerAdaptor(File mzIdentMLFile, boolean inMemory) throws ConfigurationException {
        super(mzIdentMLFile, inMemory);
        scanIdMappings();
        cache();
    }

    private void scanIdMappings() throws ConfigurationException {

        scannedIdMappings = new HashMap<String, Map<String, List<IndexElement>>>();

        // get id to index element mappings of SpectrumIdentificationResult
        Map<String, IndexElement> spectrumIdentResultIdToIndexElements = this.index.getIndexElements(SpectrumIdentificationResult.class);

        // get id to index element mappings of SpectrumIdentificationItem
        Map<String, IndexElement> spectrumIdentItemIdToIndexElements = this.index.getIndexElements(SpectrumIdentificationItem.class);

        // get index elements of PeptideEvidenceRef
        List<IndexElement> peptideEvidenceRefIndexElements = this.index.getIndexElements(MzIdentMLElement.PeptideEvidenceRef.getXpath());

        boolean proteinGroupPresent = hasProteinGroup();

        scanForIdMappings(spectrumIdentResultIdToIndexElements, spectrumIdentItemIdToIndexElements, peptideEvidenceRefIndexElements, proteinGroupPresent);

    }

    private void scanForIdMappings(Map<String, IndexElement> spectrumIdentResultIdToIndexElements,
                                   Map<String, IndexElement> spectrumIdentItemIdToIndexElements,
                                   List<IndexElement> peptideEvidenceRefIndexElements,
                                   boolean proteinGroupPresent) {

        for (String spectrumIdentResultId : spectrumIdentResultIdToIndexElements.keySet()) {
            IndexElement spectrumIdentResultIndexElement = spectrumIdentResultIdToIndexElements.get(spectrumIdentResultId);

            Iterator<Map.Entry<String, IndexElement>> spectrumIdentItemElementEntryIterator = spectrumIdentItemIdToIndexElements.entrySet().iterator();
            while (spectrumIdentItemElementEntryIterator.hasNext()) {
                Map.Entry<String, IndexElement> spectrumIdentItemElementEntry = spectrumIdentItemElementEntryIterator.next();
                String spectrumIdentItemId = spectrumIdentItemElementEntry.getKey();
                IndexElement spectrumIdentItemIndexElement = spectrumIdentItemElementEntry.getValue();
                if (isParentIndexElement(spectrumIdentResultIndexElement, spectrumIdentItemIndexElement)) {
                    Map<String, List<IndexElement>> spectrumIdentItemWithin = scannedIdMappings.get(spectrumIdentResultId);
                    if (spectrumIdentItemWithin == null) {
                        spectrumIdentItemWithin = new HashMap<String, List<IndexElement>>();
                        scannedIdMappings.put(spectrumIdentResultId, spectrumIdentItemWithin);
                    }

                    if (proteinGroupPresent) {
                        spectrumIdentItemWithin.put(spectrumIdentItemId, null);
                    } else {
                        spectrumIdentItemWithin.put(spectrumIdentItemId, findPeptideEvidenceRefIndexElements(spectrumIdentItemIndexElement, peptideEvidenceRefIndexElements));
                    }

                    spectrumIdentItemElementEntryIterator.remove();
                }
            }
        }
    }

    private void cache() throws ConfigurationException {

        boolean proteinGroupPresent = hasProteinGroup();

        // cache spectra data
        cacheSpectraData();

        /* Get a preScan of the File, the PreCan of the mzidentml File gets the information
         * about all the spectrums, protein identifications, and peptide-spectrum matchs with the
         * same structure that currently follow the mzidentml library.
         * */
        if (proteinGroupPresent) {
            cacheSpectrumIds();
            cacheProteinGroups();
        } else {
            cachePrescanIdMaps();
        }
    }

    private void cacheSpectraData() {
        Map<Comparable, SpectraData> oldSpectraDataMap = getSpectraDataMap();

        if (oldSpectraDataMap != null && !oldSpectraDataMap.isEmpty()) {
            spectraDataMapResult = new HashMap<Comparable, SpectraData>();

            Iterator iterator = oldSpectraDataMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) iterator.next();
                SpectraData spectraData = (SpectraData) mapEntry.getValue();
                spectraDataMapResult.put((Comparable) mapEntry.getKey(), spectraData);
            }
        }
    }

    private void cacheProteinGroups() throws ConfigurationException {
        Set<String> proteinAmbiguityGroupIds = getIDsForElement(MzIdentMLElement.ProteinAmbiguityGroup);

        if (proteinAmbiguityGroupIds != null && !proteinAmbiguityGroupIds.isEmpty()) {

            proteinGroupIds = new ArrayList<Comparable>(proteinAmbiguityGroupIds);

            proteinIds = new ArrayList<Comparable>(getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis));
        }
    }

    private void cacheSpectrumIds() throws ConfigurationException {

        identSpectrumMap = new HashMap<Comparable, String[]>();


        Set<String> spectrumIdentResultIds = getIDsForElement(MzIdentMLElement.SpectrumIdentificationResult);

        Map<Comparable, SpectraData> spectraDataIds = getSpectraDataMap();

        spectraDataMap = new HashMap<Comparable, List<Comparable>>(spectraDataIds.size());

        for (String spectrumIdentResultId : spectrumIdentResultIds) {

            Map<String, String> spectrumIdentificationResultAttributes = getElementAttributes(spectrumIdentResultId, SpectrumIdentificationResult.class);
            String spectrumDataReference = spectrumIdentificationResultAttributes.get("spectraData_ref");
            String spectrumID = spectrumIdentificationResultAttributes.get("spectrumID");

            // fill the SpectraDataMap
            // for the currently referenced spectra file, retrieve the List (if it exists already) that is to store all the spectra IDs
            List<Comparable> spectrumIds = spectraDataMap.get(spectrumDataReference);
            // if there is no spectra ID list for the spectrum file yet, then create one and add it to the map
            if (spectrumIds == null) {
                spectrumIds = new ArrayList<Comparable>();
                spectraDataMap.put(spectrumDataReference, spectrumIds);
            }
            // add the spectrum ID to the list of spectrum IDs for the current spectrum file
            spectrumIds.add(spectrumID);

            // proceed to populate the identSpectrumMap
            Set<String> spectrumIdentItemIds = getSpectrumIdentificationItemIds(spectrumIdentResultId);
            for (String spectrumIdentItemId : spectrumIdentItemIds) {

                // extract the spectrum ID from the provided identifier
                String[] spectrumFeatures = {spectrumID, spectrumDataReference};

                identSpectrumMap.put(spectrumIdentItemId, spectrumFeatures);
            }
        }
    }

    /**
     * This function try to Map in memory ids mapping and relation for an mzidentml file. The structure of the
     * mzidentml files is from spectrum->peptide->protein, but most for the end users is more interesting to
     * have an information structure from protein->peptide->spectrum. The function take the information from
     * spectrumItems and read the Peptide Evidences and the Proteins related with these peptideEvidence. Finally
     * the function construct a map in from proteins to spectrums named identProteinsMap.
     *
     * @throws javax.naming.ConfigurationException
     *
     */
    private void cachePrescanIdMaps() throws ConfigurationException {

        /**
         * Map of IDs to SpectraData, e.g. IDs to spectra files
         */
        Map<Comparable, SpectraData> spectraDataIds = getSpectraDataMap();


        /**
         * First Map is the Relation between an Spectrum file and all the Spectrums ids in the file
         * This information is useful to retrieve the for each spectrum File with spectrums are really
         * SpectrumIdentificationItems. For PRIDE Inspector is important for one of the windows that
         * shows the number of missing spectrum for an mzidentml file.
         * Map of SpectraData IDs to List of spectrum IDs, e.g. which spectra come from which file
         */
        spectraDataMap = new HashMap<Comparable, List<Comparable>>(spectraDataIds.size());

        /**
         * The relation between the peptide evidence and the spectrumIdentificationItem.
         * This map allow the access to the peptide evidence and spectrum information
         * without the Protein information.
         * ???? PeptideEvidence ????
         *
         * Map of SII IDs to a String[2] of spectrum ID and spectrum file ID
         */
        identSpectrumMap = new HashMap<Comparable, String[]>();


        /**
         * List of PSMs, e.g. SpectrumIdentificationResult IDs
         */
        Set<String> spectrumIdentResultIds = getIDsForElement(MzIdentMLElement.SpectrumIdentificationResult);

        /**
         * This Protein Map represents the Protein identification in the DBSequence Section that contains SpectrumIdentification Items
         * Each key is the Protein Id, the Map related with each key is a Peptide Evidence Map. Each Peptide Evidence Map contains a key
         * of the for a PeptideEvidence and a list of SpectrumIdentificationItems. Each Sepctrum Identification Item is that contains
         * the original id of the spectrum in the Spectrum file and the id of the spectrum file.
         *
         */

        identProteinsMap = new HashMap<Comparable, List<Comparable>>();

        for (String spectrumIdentResultId : spectrumIdentResultIds) {

            Map<String, String> spectrumIdentificationResultAttributes = getElementAttributes(spectrumIdentResultId, SpectrumIdentificationResult.class);
            String spectrumDataReference = spectrumIdentificationResultAttributes.get("spectraData_ref");
            String spectrumID = spectrumIdentificationResultAttributes.get("spectrumID");

            // fill the SpectraDataMap
            // for the currently referenced spectra file, retrieve the List (if it exists already) that is to store all the spectra IDs
            List<Comparable> spectrumIds = spectraDataMap.get(spectrumDataReference);
            // if there is no spectra ID list for the spectrum file yet, then create one and add it to the map
            if (spectrumIds == null) {
                spectrumIds = new ArrayList<Comparable>();
                spectraDataMap.put(spectrumDataReference, spectrumIds);
            }
            // add the spectrum ID to the list of spectrum IDs for the current spectrum file
            spectrumIds.add(spectrumID);

            // proceed to populate the identSpectrumMap
            Set<String> spectrumIdentItemIds = getSpectrumIdentificationItemIds(spectrumIdentResultId);
            for (String spectrumIdentItemId : spectrumIdentItemIds) {

                // fill the SpectrumIdentification and the Spectrum information
                // extract the spectrum ID from the provided identifier
                String[] spectrumFeatures = {spectrumID, spectrumDataReference};

                identSpectrumMap.put(spectrumIdentItemId, spectrumFeatures);

                Set<Comparable> idProteins = new HashSet<Comparable>();
                Set<String> peptideEvidenceReferences = getPeptideEvidenceReferences(spectrumIdentResultId, spectrumIdentItemId);
                for (String peptideEvidenceReference : peptideEvidenceReferences) {
                    Map<String, String> attributes = getElementAttributes(peptideEvidenceReference, PeptideEvidence.class);
                    idProteins.add(attributes.get("dBSequence_ref"));
                }

                for (Comparable idProtein : idProteins) {
                    List<Comparable> spectrumIdentifications = identProteinsMap.get(idProtein);
                    if (spectrumIdentifications == null) {
                        spectrumIdentifications = new ArrayList<Comparable>();
                        identProteinsMap.put(idProtein, spectrumIdentifications);
                    }
                    spectrumIdentifications.add(spectrumIdentItemId);
                }
            }
        }

        proteinIds = new ArrayList<Comparable>(identProteinsMap.keySet());
    }


    private List<IndexElement> findPeptideEvidenceRefIndexElements(IndexElement spectrumIdentItemIndexElement, List<IndexElement> peptideEvidenceRefIndexElements) {
        List<IndexElement> peptideEvidenceRefIndexElementsFound = new ArrayList<IndexElement>();

        Iterator<IndexElement> peptideEvidenceRefIndexElementIterator = peptideEvidenceRefIndexElements.iterator();
        while (peptideEvidenceRefIndexElementIterator.hasNext()) {
            IndexElement peptideEvidenceRefIndexElement = peptideEvidenceRefIndexElementIterator.next();
            if (isParentIndexElement(spectrumIdentItemIndexElement, peptideEvidenceRefIndexElement)) {
                peptideEvidenceRefIndexElementsFound.add(peptideEvidenceRefIndexElement);
                peptideEvidenceRefIndexElementIterator.remove();
            }
        }

        return peptideEvidenceRefIndexElementsFound;
    }

    private boolean isParentIndexElement(IndexElement parent, IndexElement child) {
        return parent.getStart() <= child.getStart() && parent.getStop() >= child.getStop();
    }

    public List<Sample> getSampleList() {
        AnalysisSampleCollection asc =
                this.unmarshal(AnalysisSampleCollection.class);
        return (asc != null) ? asc.getSample() : null;
    }

    public List<AnalysisSoftware> getSoftwares() {
        AnalysisSoftwareList asl =
                this.unmarshal(AnalysisSoftwareList.class);

        return (asl != null) ? asl.getAnalysisSoftware() : null;
    }

    public List<Person> getPersonContacts() {
        if(auditCollection == null) auditCollection = this.unmarshal(AuditCollection.class);
        return (auditCollection != null) ? auditCollection.getPerson() : null;
    }

    public Iterator<BibliographicReference> getReferences() {
        return this.unmarshalCollectionFromXpath(MzIdentMLElement.BibliographicReference);
    }

    public ProteinDetectionHypothesis getIdentificationById(Comparable IdentId) throws JAXBException {
        return this.unmarshal(ProteinDetectionHypothesis.class, (String) IdentId);
    }

    public String getMzIdentMLName() {
        Map<String, String> properties = this.getElementAttributes(this.getMzIdentMLId(),
                MzIdentML.class);

        /*
         * This is the only way that we can use now to retrieve the name property
         * In the future we need to think in more elaborated way.
         */
        return (properties.containsKey("name")) ? properties.get("name") : "Unknown experiment (mzIdentML)";
    }

    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocol() {
        AnalysisProtocolCollection apc = this.unmarshal(AnalysisProtocolCollection.class);
        return (apc != null) ? apc.getSpectrumIdentificationProtocol() : null;
    }

    public ProteinDetectionProtocol getProteinDetectionProtocol() {
        return this.unmarshal(ProteinDetectionProtocol.class);
    }

    public List<SpectraData> getSpectraData() {
        if(inputs == null) inputs = this.unmarshal(Inputs.class);
        return inputs.getSpectraData();
    }

    private Map<Comparable, SpectraData> getSpectraDataMap() {
        if(inputs == null) inputs = this.unmarshal(Inputs.class);
        List<SpectraData> spectraDataList = inputs.getSpectraData();
        Map<Comparable, SpectraData> spectraDataMap = null;
        if (spectraDataList != null && spectraDataList.size() > 0) {
            spectraDataMap = new HashMap<Comparable, SpectraData>();
            for (SpectraData spectraData : spectraDataList) {
                spectraDataMap.put(spectraData.getId(), spectraData);
            }
        }
        return spectraDataMap;
    }

    public ProteinAmbiguityGroup getProteinAmbiguityGroup(Comparable id) throws JAXBException {
        return this.unmarshal(ProteinAmbiguityGroup.class, (String) id);
    }

    public DBSequence getDBSequenceById(Comparable id) throws JAXBException {
        return this.unmarshal(DBSequence.class, (String) id);
    }

    public boolean hasProteinGroup() throws ConfigurationException {
        Set<String> proteinAmbiguityGroupIds = this.getIDsForElement(MzIdentMLElement.ProteinAmbiguityGroup);
        return proteinAmbiguityGroupIds != null && !proteinAmbiguityGroupIds.isEmpty();
    }

    public List<SpectrumIdentificationItem> getSpectrumIdentificationsByIds(List<Comparable> spectrumIdentIds) throws JAXBException {
        List<SpectrumIdentificationItem> spectrumIdentifications = null;
        if (spectrumIdentIds != null && spectrumIdentIds.size() > 0) {
            spectrumIdentifications = new ArrayList<SpectrumIdentificationItem>();
            for (Comparable id : spectrumIdentIds) {
                SpectrumIdentificationItem spectrumIdentification = this.unmarshal(SpectrumIdentificationItem.class, (String) id);
                spectrumIdentifications.add(spectrumIdentification);
            }

        }
        return spectrumIdentifications;
    }

    private Set<String> getSpectrumIdentificationItemIds(String spectrumIdentResultId) {
        Map<String, List<IndexElement>> elementsWithSpectrumIdentResult = scannedIdMappings.get(spectrumIdentResultId);

        if (elementsWithSpectrumIdentResult != null) {
            return new LinkedHashSet<String>(elementsWithSpectrumIdentResult.keySet());
        } else {
            return Collections.emptySet();
        }
    }

    private Set<String> getPeptideEvidenceReferences(String spectrumIdentResultId, String spectrumIdentItemId) {
        Map<String, List<IndexElement>> elementsWithSpectrumIdentResult = scannedIdMappings.get(spectrumIdentResultId);

        if (elementsWithSpectrumIdentResult != null) {
            List<IndexElement> peptideEvidenceRefIndexElements = elementsWithSpectrumIdentResult.get(spectrumIdentItemId);
            if (peptideEvidenceRefIndexElements != null) {
                Set<String> peptideEvidenceRefs = new LinkedHashSet<String>();

                for (IndexElement peptideEvidenceRefIndexElement : peptideEvidenceRefIndexElements) {
                    Map<String, String> peptideEvidenceRefAttributes = this.getElementAttributes(this.index.getXmlString(peptideEvidenceRefIndexElement));
                    if (peptideEvidenceRefAttributes.containsKey("peptideEvidence_ref")) {
                        peptideEvidenceRefs.add(peptideEvidenceRefAttributes.get("peptideEvidence_ref"));
                    }
                }

                return peptideEvidenceRefs;
            } else {
                return Collections.emptySet();
            }
        } else {
            return Collections.emptySet();
        }
    }

    public Set<String> getAllSpectrumIdentificationItem() throws ConfigurationException {
        return this.getIDsForElement(MzIdentMLElement.SpectrumIdentificationItem);
    }

    public SpectrumIdentificationItem getSpectrumIdentificationItem(String id) throws JAXBException {
        return this.unmarshal(SpectrumIdentificationItem.class, id);
    }



    public Set<String> getProteinHypothesisIds() throws ConfigurationException {
        Set<String> ids = Collections.emptySet();
        if(hasProteinGroup()){
            ids  = this.getIDsForElement(MzIdentMLElement.ProteinDetectionHypothesis);
        }
        return ids;
    }

    public Map<Comparable, String[]> getIdentSpectrumMap() {
        return identSpectrumMap;
    }

    public List<Comparable> getProteinGroupIds() {
        return proteinGroupIds;
    }

    public Map<Comparable, List<Comparable>> getIdentProteinsMap() {
        return identProteinsMap;
    }

    public List<Comparable> getProteinIds() {
        return proteinIds;
    }

    public List<SearchModification> getModificationIds(){
        List<SearchModification> modifications = new ArrayList<SearchModification>();
        List<SpectrumIdentificationProtocol> spectrumProtocol = this.getSpectrumIdentificationProtocol();
        for(SpectrumIdentificationProtocol spec: spectrumProtocol){
            if(spec.getModificationParams() != null){
                List<SearchModification> specMods = spec.getModificationParams().getSearchModification();
                modifications.addAll(specMods);
            }
        }
        return modifications;
    }
}



