package uk.ac.ebi.pride.jmztab.model;

/**
 * User: Qingwei
 * Date: 23/05/13
 */
public class MZTabColumn {
    private final String name;
    private final String order;
    private String header;
    private String logicPosition;
    private Class dataType;
    private boolean optional;

    private IndexedElement element;

    public MZTabColumn(String name, Class dataType, boolean optional, String order) {
        this.name = name;
        this.dataType = dataType;
        this.optional = optional;
        this.order = order;

        this.header = name;
        this.logicPosition = order;
    }

    public String getName() {
        return name;
    }

    public String getOrder() {
        return order;
    }

    public String getHeader() {
        return header;
    }

    public String getLogicPosition() {
        return logicPosition;
    }

    public Class getDataType() {
        return dataType;
    }

    public boolean isOptional() {
        return optional;
    }

    public IndexedElement getElement() {
        return element;
    }

    void setElement(IndexedElement element) {
        this.element = element;

        if (element != null) {
            this.logicPosition = logicPosition + element.getId();
            StringBuilder sb = new StringBuilder();
            sb.append(this.header).append("_").append(element.getReference());
            this.header = sb.toString();
        }
    }

    static MZTabColumn createOptionalColumn(MZTabColumn column, IndexedElement element) {
        if (! column.isOptional()) {
            throw new IllegalArgumentException(column + " is not optional column!");
        }

        MZTabColumn optionColumn = new MZTabColumn(column.getName(), column.getDataType(), column.isOptional(), column.getOrder());
        optionColumn.setElement(element);

        return optionColumn;
    }

    @Override
    public String toString() {
        return "MZTabColumn{" +
                "header='" + header + '\'' +
                ", logicPosition='" + logicPosition + '\'' +
                ", dataType=" + dataType +
                ", optional=" + optional +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MZTabColumn column = (MZTabColumn) o;

        if (optional != column.optional) return false;
        if (dataType != null ? !dataType.equals(column.dataType) : column.dataType != null) return false;
        if (header != null ? !header.equals(column.header) : column.header != null) return false;
        if (logicPosition != null ? !logicPosition.equals(column.logicPosition) : column.logicPosition != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = header != null ? header.hashCode() : 0;
        result = 31 * result + (logicPosition != null ? logicPosition.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (optional ? 1 : 0);
        return result;
    }
}
