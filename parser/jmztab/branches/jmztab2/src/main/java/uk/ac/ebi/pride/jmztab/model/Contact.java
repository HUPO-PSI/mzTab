package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MetadataElement.*;
import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.*;

/**
 * User: Qingwei
 * Date: 01/02/13
 */
public class Contact extends UnitElement {
	private ContactName name;
	private String email;
	private String affiliation;
	
	public Contact(int id, Unit unit) {
        super(id, unit);
	}

    public ContactName getName() {
        return name;
    }

    public void setName(ContactName name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (name != null) {
            sb.append(printProperty(CONTACT_NAME, name)).append(NEW_LINE);
        }
        if (affiliation != null) {
            sb.append(printProperty(CONTACT_AFFILIATION, affiliation)).append(NEW_LINE);
        }
        if (email != null) {
            sb.append(printProperty(CONTACT_EMAIL, email)).append(NEW_LINE);
        }

        return sb.toString();
    }


}
