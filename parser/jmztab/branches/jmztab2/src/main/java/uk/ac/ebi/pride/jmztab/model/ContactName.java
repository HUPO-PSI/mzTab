package uk.ac.ebi.pride.jmztab.model;

/**
 * A contact has to be supplied in the format [first name] [initials] [last name]
 *
 * User: Qingwei
 * Date: 01/02/13
 */
public class ContactName {
    private String firstName;
    private String initials;
    private String lastName;

    public ContactName() {
    }

    public ContactName(String firstName, String initials, String lastName) {
        this.firstName = firstName;
        this.initials = initials;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (firstName != null) {
            sb.append(firstName);
        }

        if (initials != null) {
            sb.append(" ").append(initials);
        }

        if (lastName != null) {
            sb.append(" ").append(lastName);
        }

        return sb.toString().trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
