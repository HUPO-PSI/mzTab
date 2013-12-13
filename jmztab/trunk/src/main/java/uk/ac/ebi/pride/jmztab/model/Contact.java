package uk.ac.ebi.pride.jmztab.model;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.NEW_LINE;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.CONTACT_AFFILIATION;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.CONTACT_EMAIL;
import static uk.ac.ebi.pride.jmztab.model.MetadataProperty.CONTACT_NAME;

/**
 * Setting contact name, email, affiliation in {@link Metadata}.
 * Several contacts can be given by indicating the number in the square brackets after "contact".
 *
 * User: Qingwei
 * Date: 23/05/13
 */
public class Contact extends IndexedElement {
    private String name;
    private String email;
    private String affiliation;

    public Contact(int id) {
        super(MetadataElement.CONTACT, id);
    }

    /**
     * The contact's name. Several contacts can be given by indicating the number in the square brackets after "contact".
     * A contact has to be supplied in the format [first name] [initials] [last name] (see example).
     */
    public String getName() {
        return name;
    }

    /**
     * The contact's name. Several contacts can be given by indicating the number in the square brackets after "contact".
     * A contact has to be supplied in the format [first name] [initials] [last name] (see example).
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The contact’s e-mail address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * The contact’s e-mail address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The contact’s affiliation.
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * The contact’s affiliation.
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    /**
     * Print contact into a String:
     * <ul>
     *     <li>MTD	contact[1]-name	James D. Watson</li>
     *     <li>MTD	contact[1]-affiliation	Cambridge University, UK</li>
     *     <li>MTD	contact[1]-email	watson@cam.ac.uk</li>
     * </ul>
     */
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

    /**
     * If the contact's email equal, return true.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (email != null ? !email.equals(contact.email) : contact.email != null) return false;

        return true;
    }

    /**
     * If the contact's email equal, hash code equal.
     */
    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}
