package uk.ac.ebi.pride.jmztab.converter.mzidentml;

import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.PeptideEvidenceRef;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.pride.jmztab.converter.mzidentml.utils.MZIdentMLUtils;
import uk.ac.ebi.pride.jmztab.converter.utils.cv.SearchEngineParam;
import uk.ac.ebi.pride.jmztab.model.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;

/**
 * Created by yperez on 10/08/2014.
 */
public class ConvertAmbiguityModMZIdentMLFile  extends ConvertMZidentMLFile{

    public ConvertAmbiguityModMZIdentMLFile(File source) {
        super(source);
    }

    /**
     * Converts the passed Identification object into an MzTab PSM.
     */
    protected List<PSM> loadPSMs(Set<String> oldpsmList) throws JAXBException {
        Map<Comparable, Integer> indexSpectrumID = new HashMap<Comparable, Integer>();
        List<PSM> psmList = new ArrayList<PSM>();
        variableModifications = new HashMap<Param, Set<String>>();
        for (String oldPsmId : oldpsmList) {
            SpectrumIdentificationItem oldPSM = reader.getSpectrumIdentificationItem(oldPsmId);
            for(PeptideEvidenceRef peptideEvidenceRef: oldPSM.getPeptideEvidenceRef()){
                PSM psm = new PSM(psmColumnFactory, metadata);
                psm.setSequence(oldPSM.getPeptide().getPeptideSequence());
                psm.setPSM_ID(oldPSM.getId());
                    psm.setAccession(peptideEvidenceRef.getPeptideEvidence().getDBSequence().getAccession());
                    psm.setDatabase(getDatabaseName(peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getDatabaseName().getCvParam(),peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getDatabaseName().getUserParam()));
                    String version = (peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getVersion() != null && !peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getVersion().isEmpty())?peptideEvidenceRef.getPeptideEvidence().getDBSequence().getSearchDatabase().getVersion():null;
                    psm.setDatabaseVersion(version);

                    psm.setStart(peptideEvidenceRef.getPeptideEvidence().getStart());
                    psm.setEnd(peptideEvidenceRef.getPeptideEvidence().getEnd());
                    psm.setPre(peptideEvidenceRef.getPeptideEvidence().getPre());
                    psm.setPost(peptideEvidenceRef.getPeptideEvidence().getPost());

                    /**
                     * This hack is to allow the conversion from mzIdentML 1.1 with unambiguity modification annotation. Now allow only the Proteome Discoverer
                     * CVParam and the value should contain this structure: S(1): 47.9; T(3): 47.9; T(5): 4.2;
                     * Todo: This hack most be replace in the future for mzIdentML 1.2
                     */
                    List<String[]> modposList = null;
                    if(MZIdentMLUtils.getCVParamByAccession(oldPSM.getCvParam(), MZIdentMLUtils.PROTEOMEDISCOVERER_PHOSPHORS_SITE_PROBABILIY) != null){
                        modposList = new ArrayList<String[]>();
                        String[] valueArray = MZIdentMLUtils.getCVParamByAccession(oldPSM.getCvParam(), MZIdentMLUtils.PROTEOMEDISCOVERER_PHOSPHORS_SITE_PROBABILIY).getValue().split(";");
                        for(String value: valueArray){
                            value = value.replaceAll("\\s+","");
                            String[] arrayValues = value.split("\\(")[1].split("\\)");
                            String[] modDiscover = new String[3];
                            modDiscover[0]     = value.substring(0,1); // First letter is aminoacid
                            modDiscover[1]     = arrayValues[0];       // Between pharenteses must be the postion
                            modDiscover[2]     = arrayValues[1].replace(":","");       // last number is the score
                            modposList.add(modDiscover);
                        }
                    }
                    List<Modification> mods = new ArrayList<Modification>();
                    for(uk.ac.ebi.jmzidml.model.mzidml.Modification oldMod: oldPSM.getPeptide().getModification()){
                        Modification mod = MZTabUtils.parseModification(Section.PSM, oldMod.getCvParam().get(0).getAccession());
                        if(mod != null){
                            //This part can be removed in the future when We supported mzIdentML 1.2. This is a hack to support ambiguity score modification
                            // particulary Phosphorylation.
                            if(modposList != null && (oldMod.getCvParam().get(0).getAccession().equalsIgnoreCase("MOD:00042") || oldMod.getCvParam().get(0).getAccession().equalsIgnoreCase("UNIMOD:21"))){
                                mod.setAmbiguity(true);
                                for(String[] value: modposList){
                                    CVParam cvParam = new CVParam("PSI-MS", "MS:1001971","ProteomeDiscoverer:phosphoRS site probabilities",value[2]);
                                    mod.addPosition(Integer.parseInt(value[1]), cvParam);

                                    Param param = convertParam(oldMod.getCvParam().get(0));
                                    if(!variableModifications.containsKey(param) || !variableModifications.get(param).contains(value[0])){
                                        Set<String> sites = new HashSet<String>();
                                        sites = (variableModifications.containsKey(param.getAccession()))?variableModifications.get(param.getAccession()):sites;
                                        sites.add(value[0]);
                                        variableModifications.put(param, sites);
                                    }
                                    mods.add(mod);
                                }
                            }else{
                                mod.addPosition(oldMod.getLocation(), null);
                                mods.add(mod);
                                String site = null;
                                if(oldMod.getLocation()-1 < 0)
                                    site = "N-Term";
                                else if(peptideEvidenceRef.getPeptideEvidence().getPeptide().getPeptideSequence().length() <= oldMod.getLocation() -1)
                                    site = "C-Term";
                                else
                                    site = String.valueOf(peptideEvidenceRef.getPeptideEvidence().getPeptide().getPeptideSequence().charAt(oldMod.getLocation()-1));
                                Param param = convertParam(oldMod.getCvParam().get(0));
                                if(!variableModifications.containsKey(param) || !variableModifications.get(param).contains(site)){
                                    Set<String> sites = new HashSet<String>();
                                    sites = (variableModifications.containsKey(param.getAccession()))?variableModifications.get(param.getAccession()):sites;
                                    sites.add(site);
                                    variableModifications.put(param, sites);
                                }
                            }
                        }else{
                            logger.warn("Your mzidentml contains an UNKNOWN modification which is not supported by mzTab format");
                        }
                        for(CvParam param: oldMod.getCvParam()) {
                            if(param.getAccession().equalsIgnoreCase(MZIdentMLUtils.CVTERM_NEUTRAL_LOST)){
                                CVParam lost = convertParam(param);
                                Modification modNeutral = new Modification(Section.PSM,Modification.Type.NEUTRAL_LOSS, lost.getAccession());
                                modNeutral.setNeutralLoss(lost);
                                modNeutral.addPosition(oldMod.getLocation(), null);
                                mods.add(modNeutral);
                                //mod.setNeutralLoss(lost);
                            }
                        }
                    }

                    for(Modification mod: mods) psm.addModification(mod);

                    psm.setExpMassToCharge(oldPSM.getExperimentalMassToCharge());
                    psm.setCharge(oldPSM.getChargeState());
                    psm.setCalcMassToCharge(oldPSM.getCalculatedMassToCharge());

                    String[] spectumMap = reader.getIdentSpectrumMap().get(oldPsmId);

                    String spectrumReference = spectumMap[0];
                    if(spectumMap[1] != null && spectrumReference != null)
                        psm.addSpectraRef(new SpectraRef(metadata.getMsRunMap().get(spectraToRun.get(spectumMap[1])), spectrumReference));
                    psm.setStart(peptideEvidenceRef.getPeptideEvidence().getStart());
                    psm.setEnd(peptideEvidenceRef.getPeptideEvidence().getEnd());

                    // See which psm scores are supported
                    for(CvParam cvPAram: oldPSM.getCvParam()){
                        if(psmScoreToScoreIndex.containsKey(cvPAram.getAccession())){
                            CVParam param = convertParam(cvPAram);
                            int idCount = psmScoreToScoreIndex.get(cvPAram.getAccession());
                            psm.setSearchEngineScore(idCount, param.getValue());
                        }
                    }
                    //loadModifications(psm,peptideEvidenceRef.getPeptideEvidence());
                    if(!indexSpectrumID.containsKey(oldPSM.getId())){
                        int index = indexSpectrumID.size()+1;
                        indexSpectrumID.put(oldPSM.getId(), index);
                    }

                    //Set Search Engine
                    Set<SearchEngineParam> searchEngines = new HashSet<SearchEngineParam>();
                    List<SearchEngineParam> searchEngineParams = MZIdentMLUtils.getSearchEngineTypes(oldPSM.getCvParam());
                    searchEngines.addAll(searchEngineParams);

                    for(SearchEngineParam searchEngineParam: searchEngines)
                        psm.addSearchEngineParam(searchEngineParam.getParam());

                    //Set optional parameter

                    psm.setPSM_ID(indexSpectrumID.get(oldPSM.getId()));
                    psm.setOptionColumnValue(MZIdentMLUtils.OPTIONAL_ID_COLUMN, oldPSM.getId());
                    Boolean decoy = peptideEvidenceRef.getPeptideEvidence().isIsDecoy();
                    psm.setOptionColumnValue(MZIdentMLUtils.OPTIONAL_DECOY_COLUMN, (!decoy)?0:1);
                    psm.setOptionColumnValue(MZIdentMLUtils.OPTIONAL_RANK_COLUMN, oldPSM.getRank());
                    psmList.add(psm);
                }
            }

            //Load the modifications in case some of modifications are not reported in the SpectrumIdentificationProtocol
            int varId = 1;
            for(Param param: variableModifications.keySet()){
                String siteString = "";
                for(String site: variableModifications.get(param)){
                    siteString=siteString+" "+ site;
                }
                siteString = siteString.trim();
                metadata.addVariableModParam(varId, param);
                metadata.addVariableModSite(varId, siteString);
                varId++;
            }
            return psmList;
        }
}
