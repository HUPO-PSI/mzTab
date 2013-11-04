package uk.ac.ebi.pride.jmztab.utils.convert;

import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.model.UserParam;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static uk.ac.ebi.pride.jmztab.model.MZTabUtils.isEmpty;

/**
* User: Qingwei
* Date: 12/03/13
*/
public class ConvertMzIndentMLFile extends ConvertFile {
    private MzIdentMLUnmarshaller reader;

    public ConvertMzIndentMLFile(File inFile) {
        super(inFile, mzIdentML);
        reader = new MzIdentMLUnmarshaller(inFile);
        createArchitecture();
        fillData();
    }

    private String getCVParamValue(List<CvParam> params, String accession) {
        for (CvParam param : params) {
            if (param.getAccession().equals(accession)) {
                return param.getValue();
            }
        }
        return null;
    }

    private void loadSoftware(Unit unit) {
        Iterator<AnalysisSoftware> it = reader.unmarshalCollectionFromXpath(MzIdentMLElement.AnalysisSoftware);
        int id = 0;
        String accession;
        String name;
        String version;
        AnalysisSoftware software;
        while (it.hasNext()) {
            id++;
            software = it.next();
            version = software.getVersion();
            name = software.getSoftwareName().getCvParam().getName();
            accession = software.getSoftwareName().getCvParam().getAccession();
            unit.addSoftwareParam(id, new CVParam("MS", accession, name, version));
        }
    }

    private void loadContact(Unit unit) {
        AuditCollection audits = reader.unmarshal(MzIdentMLElement.AuditCollection);
        List<Person> persons = audits.getPerson();

        String name;
        String affiliation = null;
        String email;
        int id = 0;
        for (Person person : persons) {
            name = (person.getName() == null ? "" : person.getName()) + " "
                    + (person.getMidInitials() == null ? "" : person.getMidInitials()) + " "
                    + (person.getLastName() == null ? "" : person.getLastName());
            if (isEmpty(name)) {
                continue;
            }
            id++;

            email = getCVParamValue(person.getCvParam(), "MS:1000589");

            List<Affiliation> organizations = person.getAffiliation();
            Organization organization;
            for (Affiliation a : organizations) {
                organization = a.getOrganization();
                if (organization == null) {
                    continue;
                }

                if (isEmpty(email)) {
                    email = getCVParamValue(organization.getCvParam(), "MS:1000589");
                }
                affiliation = getCVParamValue(organization.getCvParam(), "MS:1000587");
            }

            unit.addContactName(id, name);
            unit.addContactEmail(id, email);
            unit.addContactAffiliation(id, affiliation);
        }
    }

    @Override
    protected Metadata convertMetadata() {
        Metadata metadata = new Metadata();

        Unit unit = new Unit(reader.getMzIdentMLId());
        unit.addCustom(new UserParam("mzIdentML version", reader.getMzIdentMLVersion()));

        loadSoftware(unit);
        loadContact(unit);

        metadata.addUnit(unit);
        return metadata;
    }

    @Override
    protected MZTabColumnFactory convertProteinColumnFactory() {
        MZTabColumnFactory proteinColumnFactory = MZTabColumnFactory.getInstance(Section.Protein);

        return proteinColumnFactory;
    }

    @Override
    protected MZTabColumnFactory convertPeptideColumnFactory() {
        MZTabColumnFactory peptideColumnFactory = MZTabColumnFactory.getInstance(Section.Peptide);

        return peptideColumnFactory;
    }

    @Override
    protected MZTabColumnFactory convertSmallMoleculeColumnFactory() {
        return null;
    }

    @Override
    protected void fillData() {

    }
}
