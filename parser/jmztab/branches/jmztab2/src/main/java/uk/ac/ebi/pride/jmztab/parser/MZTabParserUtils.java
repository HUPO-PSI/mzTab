package uk.ac.ebi.pride.jmztab.parser;

import uk.ac.ebi.pride.jmztab.model.*;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.jmztab.utils.MZTabConstants.*;

/**
 * User: Qingwei
 * Date: 30/01/13
 */
public class MZTabParserUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static Param parseParam(String target) {
        target = target.trim();

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

    public static SplitList<String> parseStringList(char splitChar, String target) {
        SplitList<String> list = new SplitList<String>(splitChar);

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

    public static SplitList<Param> parseParamList(String target) {
        SplitList<String> list = parseStringList(BAR, target);

        Param param;
        SplitList<Param> paramList = new SplitList<Param>(BAR);
        for (String item : list) {
            param = parseParam(item.trim());
            if (param == null) {
                paramList.clear();
                return paramList;
            } else {
                paramList.add(param);
            }
        }

        return paramList;
    }

    public static SplitList<String> parseGOTermList(String target) {
        SplitList<String> list = parseStringList(COMMA, target);

        SplitList<String> goList = new SplitList<String>(COMMA);
        for (String item : list) {
            item = item.trim();
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
        Integer integer;

        try {
            integer = new Integer(target);
        } catch (NumberFormatException e) {
            integer = null;
        }

        return integer;
    }

    public static BigDecimal parseBigDecimal(String target) {
        BigDecimal decimal;
        try {
            decimal = new BigDecimal(target);
        } catch (NumberFormatException e) {
            decimal = null;
        }

        return decimal;
    }

    public static SplitList<BigDecimal> parseBigDecimalList(String target) {
        SplitList<String> list = parseStringList(BAR, target);

        BigDecimal decimal;
        SplitList<BigDecimal> decimalList = new SplitList<BigDecimal>(BAR);
        for (String item : list) {
            decimal = parseBigDecimal(item.trim());
            if (decimal == null) {
                decimalList.clear();
                break;
            } else {
                decimalList.add(decimal);
            }
        }

        return decimalList;
    }

    /**
     * UNIT_IDs MUST only contain the following characters: 'A'-'Z', 'a'-'z', '0'-'9', and '_'.
     * UNIT_IDs SHOULD consist of the resource identifier plus the resources internal unit id.
     * A resource is anything that is generating mzTab files.
     */
    public static String parseUnitId(String target) {
        if (isEmpty(target)) {
            return null;
        }

        target = target.trim();

        Pattern pattern = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
        Matcher matcher = pattern.matcher(target);

        if (matcher.find() && matcher.start() == 0 && matcher.end() == target.length()) {
            return target;
        } else {
            return null;
        }
    }

    public static URL parseURL(String target) {
        URL url;

        try {
            url = new URL(target);
        } catch (MalformedURLException e) {
            url = null;
        }

        return url;
    }

    public static URI parseURI(String target) {
        URI uri;

        try {
            uri = new URI(target);
        } catch (URISyntaxException e) {
            uri = null;
        }

        return uri;
    }

    public static Publication parsePublication(String target) {
        SplitList<String> list = parseStringList(BAR, target);

        Publication publication = new Publication();
        Publication.Type type;
        String accession;
        for (String pub : list) {
            pub = pub.trim();
            String[] items = pub.split("" + COLON);
            if (items.length != 2 || (type = Publication.findType(items[0].trim())) == null) {
                publication.clear();
                break;
            } else {
                accession = items[1].trim();
                publication.addPublication(type, accession);
            }
        }

        return publication;
    }

    private static boolean check(String regexp, String target) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static String parseEmail(String emailLabel) {
        String regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        if (check(regexp, emailLabel)) {
            return emailLabel;
        } else {
            return null;
        }
    }



    private static SpecRef createSpecRef(Unit unit, Integer ms_file_id, String reference) {
        MsFile msFile = unit.getMsFileMap().get(ms_file_id);

        if (msFile == null) {
            // error
            return null;
        }

        return new SpecRef(msFile, reference);
    }

    public static List<SpecRef> parseSepcRefList(Unit unit, String target) {
        SplitList<String> list = parseStringList(BAR, target);
        List<SpecRef> refList = new ArrayList<SpecRef>(list.size());

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

    public static List<Modification> parseModificationList(Section section, String target) {
        SplitList<String> list = parseStringList(COMMA, target);
        List<Modification> modList = new ArrayList<Modification>(list.size());

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

    public static String parseChemmodAccession(String accession) {
        if (isEmpty(accession)) {
            return null;
        }

        accession = accession.trim();

        Pattern pattern = Pattern.compile("[+-](\\d+(.\\d+)?)?|(([A-Z][a-z]*)(\\d*))?");
        Matcher matcher = pattern.matcher(accession);

        if (matcher.find()) {
            return accession;
        } else {
            return null;
        }
    }

    public static String parseSubstitutionIdentifier(String identifier) {
        if (isEmpty(identifier)) {
            return null;
        }

        identifier = identifier.trim();

        Pattern pattern = Pattern.compile("\"[A-Z]+\"");
        Matcher matcher = pattern.matcher(identifier);

        if (matcher.find() && matcher.start() == 0 && matcher.end() == identifier.length()) {
            return identifier;
        } else {
            return null;
        }
    }
}
