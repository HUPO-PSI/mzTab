package uk.ac.ebi.pride.jmztab.model;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.model.MZTabConstants.*;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class MZTabUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * Pre-process the String object. If object is null, return null; otherwise
     * remove heading and tailing white space.
     */
    public static String parseString(String target) {
        return target == null ? null : target.trim();
    }

    /**
     * If ratios are included and the denominator is zero, the “INF” value MUST be used.
     * If the result leads to calculation errors (for example 0/0), this MUST be reported
     * as “not a number” (“NaN”).
     *
     * @see #parseDouble(String)
     */
    public static String printDouble(Double value) {
        if (value == null) {
            return NULL;
        } else if (value.equals(Double.NaN)) {
            return CALCULATE_ERROR;
        } else if (value.equals(Double.POSITIVE_INFINITY)) {
            return INFINITY;
        } else {
            return value.toString();
        }
    }

    /**
     * Parameters are always reported as [CV label, accession, name, value].
     * Any field that is not available MUST be left empty.
     *
     * Notice: name cell never set null.
     */
    public static Param parseParam(String target) {
        target = parseString(target);
        if (target == null) {
            return null;
        }

        String regexp = "\\[([^,]+)?,([^,]+)?,([^,]+),([^,]*)\\]";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(target);

        if (matcher.find()) {
            String cvLabel = matcher.group(1);
            String accession = matcher.group(2);
            String name = matcher.group(3);
            String value = matcher.group(4);

            if (isEmpty(name)) {
                return null;
            }

            if (isEmpty(cvLabel) && isEmpty(accession)) {
                return new UserParam(name, value);
            } else {
                return new CVParam(cvLabel, accession, name, value);
            }
        } else {
            return null;
        }
    }

    /**
     * Multiple identifiers MUST be separated by splitChar.
     */
    public static SplitList<String> parseStringList(char splitChar, String target) {
        SplitList<String> list = new SplitList<String>(splitChar);

        target = parseString(target);
        if (target == null) {
            return list;
        }

        // regular express reserved keywords escape
        StringBuilder sb = new StringBuilder();
        switch (splitChar) {
            case '.' :
            case '$' :
            case '^' :
            case '{' :
            case '}' :
            case '[' :
            case ']' :
            case '(' :
            case ')' :
            case '|' :
            case '*' :
            case '+' :
            case '?' :
            case '\\' :
                sb.append("\\").append(splitChar);
                break;
            default:
                sb.append(splitChar);
        }

        String[] items = target.split(sb.toString());
        Collections.addAll(list, items);

        return list;
    }

    /**
     * A list of '|' separated parameters
     */
    public static SplitList<Param> parseParamList(String target) {
        SplitList<String> list = parseStringList(BAR, target);

        Param param;
        SplitList<Param> paramList = new SplitList<Param>(BAR);
        for (String item : list) {
            param = parseParam(item);
            if (param == null) {
                paramList.clear();
                return paramList;
            } else {
                paramList.add(param);
            }
        }

        return paramList;
    }

    /**
     * A '|' delimited list of GO accessions
     */
    public static SplitList<String> parseGOTermList(String target) {
        SplitList<String> list = parseStringList(COMMA, target);

        SplitList<String> goList = new SplitList<String>(COMMA);
        for (String item : list) {
            item = parseString(item);
            if (item.startsWith("GO:")) {
                goList.add(item);
            } else {
                goList.clear();
                break;
            }
        }

        return goList;
    }

    public static Integer parseInteger(String target) {
        target = parseString(target);
        if (target == null) {
            return null;
        }

        Integer integer;

        try {
            integer = new Integer(target);
        } catch (NumberFormatException e) {
            integer = null;
        }

        return integer;
    }

    public static Double parseDouble(String target) {
        target = parseString(target);
        if (target == null) {
            return null;
        }

        Double value;
        try {
            value = new Double(target);
        } catch (NumberFormatException e) {
            if (target.equals(CALCULATE_ERROR)) {
                value = Double.NaN;
            } else if (target.equals(INFINITY)) {
                value = Double.POSITIVE_INFINITY;
            } else {
                value = null;
            }
        }

        return value;
    }

    public static SplitList<Double> parseDoubleList(String target) {
        SplitList<String> list = parseStringList(BAR, target);

        Double value;
        SplitList<Double> valueList = new SplitList<Double>(BAR);
        for (String item : list) {
            value = parseDouble(item);
            if (value == null) {
                valueList.clear();
                break;
            } else {
                valueList.add(value);
            }
        }

        return valueList;
    }

    /**
     * UNIT_IDs MUST only contain the following characters: 'A'-'Z', 'a'-'z', '0'-'9', and '_'.
     * UNIT_IDs SHOULD consist of the resource identifier plus the resources internal unit id.
     * A resource is anything that is generating mzTab files.
     */
    public static String parseUnitId(String target) {
        target = parseString(target);
        if (target == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
        Matcher matcher = pattern.matcher(target);

        if (matcher.find() && matcher.start() == 0 && matcher.end() == target.length()) {
            return target;
        } else {
            return null;
        }
    }

    public static URL parseURL(String target) {
        target = parseString(target);
        if (target == null) {
            return null;
        }

        URL url;

        try {
            url = new URL(target);
        } catch (MalformedURLException e) {
            url = null;
        }

        return url;
    }

    public static URI parseURI(String target) {
        target = parseString(target);
        if (target == null) {
            return null;
        }

        URI uri;

        try {
            uri = new URI(target);
        } catch (URISyntaxException e) {
            uri = null;
        }

        return uri;
    }

    /**
     * A publication on this unit. PubMed ids must be prefixed by “pubmed:”,
     * DOIs by “doi:”. Multiple identifiers MUST be separated by “|”.
     */
    public static SplitList<PublicationItem> parsePublicationItems(String target) {
        SplitList<String> list = parseStringList(BAR, target);

        PublicationItem.Type type;
        String accession;
        PublicationItem item;
        SplitList<PublicationItem> itemList = new SplitList<PublicationItem>(BAR);
        for (String pub : list) {
            pub = parseString(pub);
            if (pub == null) {
                itemList.clear();
                break;
            }
            String[] items = pub.split("" + COLON);
            if (items.length != 2 || (type = PublicationItem.findType(items[0].trim())) == null) {
                itemList.clear();
                break;
            } else {
                accession = items[1].trim();
                item = new PublicationItem(type, accession);
                itemList.add(item);
            }
        }

        return itemList;
    }

    private static SpecRef createSpecRef(Unit unit, Integer ms_file_id, String reference) {
        MsFile msFile = unit.getMsFileMap().get(ms_file_id);

        if (msFile == null) {
            // error
            return null;
        }

        return new SpecRef(msFile, reference);
    }

    public static SplitList<SpecRef> parseSpecRefList(Unit unit, String target) {
        if (unit == null) {
            throw new NullPointerException("Unit is null!");
        }

        SplitList<String> list = parseStringList(BAR, target);
        SplitList<SpecRef> refList = new SplitList<SpecRef>(BAR);

        Pattern pattern = Pattern.compile("ms_file\\[(\\d+)\\]:(.*)");
        Matcher matcher;
        Integer ms_file_id;
        String reference;
        SpecRef ref;
        for (String item : list) {
            matcher = pattern.matcher(item.trim());
            if (matcher.find()) {
                ms_file_id = new Integer(matcher.group(1));
                reference = matcher.group(2);
                ref = createSpecRef(unit, ms_file_id, reference);
                if (ref == null) {
                    refList.clear();
                    break;
                } else {
                    refList.add(ref);
                }
            }
        }

        return refList;
    }

    private static void parsePosition(String target, Modification modification) {
        target = translateTabToComma(target);
        SplitList<String> list = parseStringList(BAR, target);

        Pattern pattern = Pattern.compile("(\\d+)(\\[([^,]+)?,([^,]+)?,([^,]+),([^,]*)\\])?");
        Matcher matcher;
        Integer id;
        CVParam param;
        for (String item : list) {
            matcher = pattern.matcher(item.trim());
            if (matcher.find()) {
                id = new Integer(matcher.group(1));
                param = matcher.group(5) == null ? null : new CVParam(matcher.group(3), matcher.group(4), matcher.group(5), matcher.group(6));
                modification.addPosition(id, param);
            }
        }
    }

    public static Modification parseModification(Section section, String target) {
        target = translateTabToComma(target);
        String[] items = target.split("\\-");
        String modLabel;
        String positionLabel;
        if (items.length > 2) {
            // error
            return null;
        } if (items.length == 2) {
            positionLabel = items[0];
            modLabel = items[1];
        } else {
            positionLabel = null;
            modLabel = items[0];
        }

        Modification modification = null;
        Modification.Type type;
        String accession;
        CVParam neutralLoss;

        Pattern pattern = Pattern.compile("(MOD|UNIMOD|CHEMMOD|SUBST):([^\\|]+)(\\|\\[([^,]+)?,([^,]+)?,([^,]+),([^,]*)\\])?");
        Matcher matcher = pattern.matcher(modLabel);
        if (matcher.find()) {
            type = Modification.findType(matcher.group(1));
            accession = matcher.group(2);
            modification = new Modification(section, type, accession);
            if (positionLabel != null) {
                parsePosition(positionLabel,  modification);
            }

            neutralLoss = matcher.group(6) == null ? null : new CVParam(matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7));
            modification.setNeutralLoss(neutralLoss);
        }

        return modification;
    }

    /**
     * locate param label [label, accession, name, value], translate ',' to '\t'
     * which used to identified
     */
    private static String translateCommaToTab(String target) {
        Pattern pattern = Pattern.compile("\\[([^\\[\\]]+)\\]");
        Matcher matcher = pattern.matcher(target);

        StringBuilder sb = new StringBuilder();

        int start = 0;
        int end;
        while (matcher.find()) {
            end = matcher.start(1);
            sb.append(target.substring(start, end));
            sb.append(matcher.group(1).replaceAll(",", "\t"));
            start = matcher.end(1);
        }
        sb.append(target.substring(start, target.length()));

        return sb.toString();
    }

    private static String translateTabToComma(String target) {
        Pattern pattern = Pattern.compile("\\[([^\\[\\]]+)\\]");
        Matcher matcher = pattern.matcher(target);

        StringBuilder sb = new StringBuilder();

        int start = 0;
        int end;
        while (matcher.find()) {
            end = matcher.start(1);
            sb.append(target.substring(start, end));
            sb.append(matcher.group(1).replaceAll("\t", ","));
            start = matcher.end(1);
        }
        sb.append(target.substring(start, target.length()));

        return sb.toString();
    }

    public static SplitList<Modification> parseModificationList(Section section, String target) {
        target = translateCommaToTab(target);

        SplitList<String> list = parseStringList(COMMA, target);
        SplitList<Modification> modList = new SplitList<Modification>(COMMA);

        Modification mod;
        for (String item : list) {
            mod = parseModification(section,  item.trim());
            if (mod == null) {
                modList.clear();
                break;
            } else {
                modList.add(mod);
            }
        }

        return modList;
    }


}
