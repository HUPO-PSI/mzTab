package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 13/02/13
 */
public class ModificationRun {
    public static void main(String[] args) {
        Modification modification1 = new Modification(Section.Protein, Modification.Type.MOD, "00412");
        modification1.addPosition(3, null);
        System.out.println(modification1);
        modification1.addPosition(4, null);
        System.out.println(modification1);

        Modification modification2 = new Modification(Section.Small_Molecule, Modification.Type.CHEMMOD, "+159.93");
        System.out.println(modification2);

        Modification modification3 = new Modification(Section.Protein, Modification.Type.MOD, "00412");
        modification3.addPosition(3, new CVParam("MS", "MS:100xxxx", "Probability Score Y", "0.8"));
        modification3.addPosition(4, new CVParam("MS", "MS:100xxxx", "Probability Score Y", "0.2"));
        modification3.setNeutralLoss(new CVParam("MS", "MS:1001524", "fragment neutral loss", "63.998285"));
        System.out.println(modification3);

        Modification modification4 = new Modification(Section.Protein, Modification.Type.SUBST, "R");
        modification4.addPosition(3, null);
        System.out.println(modification4);

        ModificationList modList = new ModificationList();
        modList.add(modification2);
        modList.add(modification1);
        modList.add(modification4);
        System.out.println(modList);
    }
}
