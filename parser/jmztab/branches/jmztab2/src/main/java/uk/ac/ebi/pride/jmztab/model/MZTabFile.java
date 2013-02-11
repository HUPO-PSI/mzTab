package uk.ac.ebi.pride.jmztab.model;

import uk.ac.ebi.pride.jmztab.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Qingwei
 * Date: 05/02/13
 */
public class MZTabFile {
    private List<Comment> commentList = new ArrayList<Comment>();
    private Metadata metadata;
    private PeptideRecord peptideRecord = new PeptideRecord();
    private ProteinRecord proteinRecord = new ProteinRecord();
    private SmallMoleculeRecord smallMoleculeRecord = new SmallMoleculeRecord();

    public MZTabFile(Metadata metadata) {
        this.metadata = metadata;
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }



}
