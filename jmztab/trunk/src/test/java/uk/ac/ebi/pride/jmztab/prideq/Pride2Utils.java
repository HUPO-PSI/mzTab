package uk.ac.ebi.pride.jmztab.prideq;

import uk.ac.ebi.pride.jmztab.model.MZTabConstants;
import uk.ac.ebi.pride.jmztab.model.SplitList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * User: qingwei
 * Date: 17/10/13
 */
public class Pride2Utils {
    private List<String> pmidList = new ArrayList<String>();

    private class Mapping {
        private String accession;
        private String pmid;

        private Mapping(String accession, String pmid) {
            this.accession = accession;
            this.pmid = pmid;
        }
    }

    public void setFilterPMIDList(List<String> pmidList) {
        this.pmidList = pmidList == null ? new ArrayList<String>() : pmidList;
    }

    private List<Mapping> queryProjectPMIDs() throws SQLException {
        StringBuilder sb = new StringBuilder();
        if (! pmidList.isEmpty()) {
            sb.append("(");
            sb.append(pmidList.get(0));
        }
        for (int i = 1; i < pmidList.size(); i++) {
            sb.append(", ").append(pmidList.get(i));
        }
        if (! pmidList.isEmpty()) {
            sb.append(")");
        }

        String sql = "SELECT e.accession as accession, p.accession as pmid\n" +
            "FROM \n" +
            "    pride.pride_reference_exp_link l, \n" +
            "    pride.pride_reference_param p, \n" +
            "    pride.pride_experiment e\n" +
            "WHERE \n" +
            "    p.parent_element_fk = l.reference_id \n" +
            "AND e.experiment_id = l.experiment_id\n" +
            "AND l.public_flag = 1\n" +
            "AND p.cv_label='PubMed'\n" +
            (pmidList.isEmpty() ? "" : "AND p.accession in " + sb.toString() + "\n") +
            "ORDER BY 1";

        List<Mapping> mappingList = new ArrayList<Mapping>();
        Connection connection = ConnectionFactory.getPRIDE2Connection();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            mappingList.add(new Mapping(rs.getString(1), rs.getString(2)));
        }

        rs.close();
        connection.close();

        return mappingList;
    }

    /**
     * "Project_accession : List<PMID>
     */
    public HashMap<String, SplitList<String>> createProjectPMIDs() throws Exception {
        HashMap<String, SplitList<String>> map = new HashMap<String, SplitList<String>>();

        SplitList<String> pmidList;
        for (Mapping mapping : queryProjectPMIDs()) {
            if (map.containsKey(mapping.accession)) {
                map.get(mapping.accession).add(mapping.pmid);
            } else {
                pmidList = new SplitList<String>(MZTabConstants.BAR);
                pmidList.add(mapping.pmid);
                map.put(mapping.accession, pmidList);
            }
        }

        return map;
    }
}
