/*
 * (C) Copyright IBM Corp. 2020, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package nz.govt.tewhatuora.central.l4h;

import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.model.Segment;
import io.github.linuxforhealth.hl7.data.Hl7DataHandlerUtil;

public class CentralRegionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralRegionUtils.class);

    // Do nothing default constructor
    private CentralRegionUtils() {
    }

    // Turn Y,N,U unto "yes", "no" or "unknown"  - for Code fields
    private static Map<String,String> ynuCodeMap = 
        Map.of("Y", "yes",
               "N", "no",
               "U", "unknown");
    public static String ynuCode(Object input) {

        String ynu = Hl7DataHandlerUtil.getStringValue(input);
        return ynuCodeMap.get(ynu);
    }

    // Turn Y,N,U unto "Yes", "No" or "Unknown"  - for Display fields
    private static Map<String,String> ynuDisplayMap = 
        Map.of("Y", "Yes",
               "N", "No",
               "U", "Unknown");
    public static String ynuDisplay(Object input) {

        String ynu = Hl7DataHandlerUtil.getStringValue(input);
        return ynuDisplayMap.get(ynu);
    }

    // TODO - Make a proper call against a FHIR Terminology Server
    private static Map<String, Map<String, String>> conceptMaps = Map.of(
        "nzcr-nz-residency-map", Map.ofEntries( 
            Map.entry("ACC", "no"),
            Map.entry("AUS", "no"),
            Map.entry("CEL", "yes"),
            Map.entry("CNE", "no"),
            Map.entry("CWH", "yes"),
            Map.entry("DET", "no"),
            Map.entry("DIP", "no"),
            Map.entry("ELI", "yes"),
            Map.entry("MNE", "no"),
            Map.entry("MPE", "yes"),
            Map.entry("NEL", "no"),
            Map.entry("NLR", "no"),
            Map.entry("NZ",  "no"),
            Map.entry("NZR", "yes"),
            Map.entry("ODF", "no"),
            Map.entry("REC", "no"),
            Map.entry("REF", "no"),
            Map.entry("RES", "yes"),
            Map.entry("RRV", "yes"),
            Map.entry("STU", "no"),
            Map.entry("VIS", "no"),
            Map.entry("VO2", "no"),
            Map.entry("VU2", "no")),
        
        "nzcr-ethnicity-2to4-map", Map.ofEntries(
            Map.entry("10", "10000"),
            Map.entry("11", "11111"),
            Map.entry("12", "10000"),
            Map.entry("21", "21111"),
            Map.entry("30", "30000"),
            Map.entry("31", "31111"),
            Map.entry("32", "32100"),
            Map.entry("33", "33111"),
            Map.entry("34", "34111"),
            Map.entry("35", "35111"),
            Map.entry("36", "36111"),
            Map.entry("37", "30000"),
            Map.entry("40", "40000"),
            Map.entry("41", "41000"),
            Map.entry("42", "42100"),
            Map.entry("43", "43100"),
            Map.entry("44", "40000"),
            Map.entry("51", "51100"),
            Map.entry("52", "52100"),
            Map.entry("53", "53100"),
            Map.entry("61", "61199"),
            Map.entry("94", "94444"),
            Map.entry("95", "95555"),
            Map.entry("97", "97777"),
            Map.entry("99", "99999")),

        "nzcr-language-map", Map.ofEntries(
            Map.entry("ACH", "ach"),
            Map.entry("DIN", "din"),
            Map.entry("FIL", "fil"),
            Map.entry("NIU", "niu"),
            Map.entry("TVL", "tvl"),
            Map.entry("ACE", "ace"),
            Map.entry("AFR", "af"),
            Map.entry("AII", "ayp"),
            Map.entry("AMH", "am"),
            Map.entry("ARA", "ar"),
            Map.entry("ASE", "ase"),
            Map.entry("BEN", "bn"),
            Map.entry("BUL", "bg"),
            Map.entry("CDO", "cdo"),
            Map.entry("CES", "cs"),
            Map.entry("CHM", "cmn"),
            Map.entry("DAN", "da"),
            Map.entry("DUT", "nl"),
            Map.entry("DZO", "dz"),
            Map.entry("ENG", "en"),
            Map.entry("FAS", "fa"),
            Map.entry("FIJ", "fj"),
            Map.entry("FIN", "fi"),
            Map.entry("GER", "de"),
            Map.entry("GRE", "el"),
            Map.entry("GUJ", "gu"),
            Map.entry("HAK", "hak"),
            Map.entry("HEB", "he"),
            Map.entry("HIF", "hif"),
            Map.entry("HIN", "hi"),
            Map.entry("HLT", "hlt"),
            Map.entry("HRV", "hr"),
            Map.entry("HUN", "hu"),
            Map.entry("ILS", "ils"),
            Map.entry("IND", "id"),
            Map.entry("ITA", "it"),
            Map.entry("JAP", "ja"),
            Map.entry("KIN", "rw"),
            Map.entry("KHM", "km"),
            Map.entry("KMR", "kmr"),
            Map.entry("KOR", "ko"),
            Map.entry("KUR", "ku"),
            Map.entry("LAO", "lo"),
            Map.entry("MAO", "mi"),
            Map.entry("MSA", "ms"),
            Map.entry("MYA", "my"),
            Map.entry("NAN", "nan"),
            Map.entry("NEP", "ne"),
            Map.entry("NOR", "no"),
            Map.entry("NZS", "nzs"),
            Map.entry("ORM", "om"),
            Map.entry("PAN", "pa"),
            Map.entry("POL", "pl"),
            Map.entry("POR", "pt"),
            Map.entry("PRS", "prs"),
            Map.entry("RAR", "rar"),
            Map.entry("RUN", "rn"),
            Map.entry("RUS", "ru"),
            Map.entry("SAM", "sm"),
            Map.entry("SIN", "si"),
            Map.entry("SLK", "sk"),
            Map.entry("SOM", "so"),
            Map.entry("SPA", "es"),
            Map.entry("SRP", "sr"),
            Map.entry("SWA", "sw"),
            Map.entry("SWE", "sv"),
            Map.entry("TAM", "ta"),
            Map.entry("THA", "th"),
            Map.entry("TIR", "ti"),
            Map.entry("TKL", "tkl"),
            Map.entry("TON", "to"),
            Map.entry("TUR", "tr"),
            Map.entry("UKR", "uk"),
            Map.entry("URD", "ur"),
            Map.entry("VIE", "vi"),
            Map.entry("YUE", "yue"),
            Map.entry("zFOO", "nan"),
            Map.entry("zLEB", "apc"),
            Map.entry("zROH", "rhg")),

        "nzcr-religion-map", Map.ofEntries(
            Map.entry("R01", "1005"),
            Map.entry("R02", "1004"),
            Map.entry("R03", "1061"),
            Map.entry("R04", "1038"),
            Map.entry("R05", "1007"),
            Map.entry("R06", "1008"),
            Map.entry("R07", "1009"),
            Map.entry("R08", "1062"),
            Map.entry("R09", "1058"),
            Map.entry("R10", "1013"),
            Map.entry("R11", "1064"),
            Map.entry("R12", "1013"),
            Map.entry("R13", "1066"),
            Map.entry("R14", "1066"),
            Map.entry("R15", "1037"),
            Map.entry("R16", "1069"),
            Map.entry("R17", "1068"),
            Map.entry("R18", "1026"),
            Map.entry("R19", "1020"),
            Map.entry("R20", "1025"),
            Map.entry("R21", "1038"),
            Map.entry("R22", "1027"),
            Map.entry("R23", "1028"),
            Map.entry("R24", "1073"),
            Map.entry("R25", "1023"),
            Map.entry("R28", "1062"),
            Map.entry("R30", "1038"),
            Map.entry("R31", "1066"),
            Map.entry("R32", "1076"),
            Map.entry("R33", "1076"),
            Map.entry("R34", "1077"),
            Map.entry("R35", "1071"),
            Map.entry("R36", "1041"),
            Map.entry("R39", "1080")
    )
);

    // Use the indicated ConceptMap to translate the inputCode to relevant FHIR code value
    public static String conceptMapTranslate(String mapId, Object inputCode) {

        // Make sure we've got an actual code to lookup.
        String code =  Hl7DataHandlerUtil.getStringValue(inputCode);
        if(code == null) {
            LOGGER.debug(String.format("No ConceptCode presented for translation by ConceptMap %s", mapId));
            return null;
        }

        // Make sure we've got a valid ConceptMap Id
        Map<String, String> cmap = conceptMaps.get(mapId);
        if(cmap == null) {
            LOGGER.debug(String.format("No concept map with id '%s' found", mapId));
            return null;
        }

        // Make sure we've got a retVal
        String retVal = cmap.get(code);
        if(retVal == null) {
            LOGGER.debug(String.format("ConceptCode %s not found in ConceptMap %s", code, mapId));
            return null;
        }

        // All good
        return retVal;
    }

    private static Map<String, Map<String, String>> codeSystemMaps = Map.of(
        "https://standards.digital.health.nz/ns/nz-residency-code", Map.ofEntries( 
            Map.entry("yes", "Permanent Resident"),
            Map.entry("no",  "Not a Permanent Resident"),
            Map.entry("unknown", "Unknown")),
    
        "https://standards.digital.health.nz/ns/ethnic-group-level-4-code", Map.ofEntries(
            Map.entry("10000", "European nfd"),
            Map.entry("11111", "New Zealand European"),
            Map.entry("21111", "Māori"),
            Map.entry("30000", "Pacific Peoples nfd"),
            Map.entry("31111", "Samoan"),
            Map.entry("32100", "Cook Islands Maori"),
            Map.entry("33111", "Tongan"),
            Map.entry("34111", "Niuean"),
            Map.entry("35111", "Tokelauan"),
            Map.entry("36111", "Fijian"),
            Map.entry("40000", "Asian nfd"),
            Map.entry("41000", "Southeast Asian nfd"),
            Map.entry("42100", "Chinese nfd"),
            Map.entry("43100", "Indian nfd"),
            Map.entry("51100", "Middle Eastern nfd"),
            Map.entry("52100", "Latin American nfd"),
            Map.entry("53100", "African nfd"),
            Map.entry("61199", "Other Ethnicity nec"),
            Map.entry("94444", "Don't Know"),
            Map.entry("95555", "Refused to Answer"),
            Map.entry("97777", "Response unidentifiable"),
            Map.entry("99999", "Not Stated")),
            
        "urn:ietf:bcp:47", Map.ofEntries(
                Map.entry("ach", "Acoli"),
                Map.entry("din", "Dinka"),
                Map.entry("fil", "Filipino"),
                Map.entry("niu", "Niuean"),
                Map.entry("tvl", "Tuvalu"),
                Map.entry("ace", "Achinese"),
                Map.entry("af", "Afrikaans"),
                Map.entry("ayp", "North Mesopotamian Arabic"),
                Map.entry("am", "Amharic"),
                Map.entry("ar", "Arabic"),
                Map.entry("ase", "American Sign Language"),
                Map.entry("bn", "Bengali"),
                Map.entry("bg", "Bulgarian"),
                Map.entry("cdo", "Min Dong Chinese"),
                Map.entry("cs", "Czech"),
                Map.entry("cmn", "Mandarin"),
                Map.entry("da", "Danish"),
                Map.entry("nl", "Dutch"),
                Map.entry("dz", "Dzongkha"),
                Map.entry("en", "English"),
                Map.entry("fa", "Persian"),
                Map.entry("fj", "Fijian"),
                Map.entry("fi", "Finish"),
                Map.entry("de", "German"),
                Map.entry("el", "Modern Greek (1453-)"),
                Map.entry("gu", "Gujarati"),
                Map.entry("hak", "Hakka Chinese"),
                Map.entry("he", "Hebrew"),
                Map.entry("hif", "Fiji Hindi"),
                Map.entry("hi", "Hindi"),
                Map.entry("hlt", "Matu Chin"),
                Map.entry("hr", "Croatian"),
                Map.entry("hu", "Hungarian"),
                Map.entry("ils", "International Sign"),
                Map.entry("id", "Indonesian"),
                Map.entry("it", "Italian"),
                Map.entry("ja", "Japanese"),
                Map.entry("rw", "Kinyarwanda"),
                Map.entry("km", "Central Khmer"),
                Map.entry("kmr", "Northern Kurdish"),
                Map.entry("ko", "Korean"),
                Map.entry("ku", "Kurdish"),
                Map.entry("lao", "Lao"),
                Map.entry("mi", "Māori"),
                Map.entry("ms", "Malay (macrolanguage)"),
                Map.entry("my", "Burmese"),
                Map.entry("nan", "Min Nan Chinese"),
                Map.entry("ne", "Nepali (macrolanguage)"),
                Map.entry("no", "Norwegian"),
                Map.entry("nzs", "New Zealand Sign Language"),
                Map.entry("om", "Oromo"),
                Map.entry("pa", "Panjabi"),
                Map.entry("pl", "Polish"),
                Map.entry("pt", "Portuguese"),
                Map.entry("prs", "Dari"),
                Map.entry("rar", "Cook Island Māori"),
                Map.entry("rn", "Rundi"),
                Map.entry("ru", "Russian"),
                Map.entry("sm", "Samoan"),
                Map.entry("si", "Sinhala"),
                Map.entry("sk", "Slovak"),
                Map.entry("so", "Somali"),
                Map.entry("es", "Spanish"),
                Map.entry("sr", "Serbian"),
                Map.entry("sw", "Swahili (macrolanguage)"),
                Map.entry("sv", "Swedish"),
                Map.entry("ta", "Tamil"),
                Map.entry("th", "Thai"),
                Map.entry("ti", "Tigrinya"),
                Map.entry("tkl", "Tokelaua"),
                Map.entry("to", "Tonga (Tonga Islands)"),
                Map.entry("tr", "Turkish"),
                Map.entry("uk", "Ukrainian"),
                Map.entry("ur", "Urdu"),
                Map.entry("vi", "Vietnamese"),
                Map.entry("yue", "Yue Chinese"),
                Map.entry("apc", "North Levantine Arabic"),
                Map.entry("rhg", "Rohingya")
        ),

        "http://terminology.hl7.org/CodeSystem/v3-ReligiousAffiliation", Map.ofEntries(
            Map.entry("1001", "Adventist"),
            Map.entry("1002", "African Religions"),
            Map.entry("1003", "Afro-Caribbean Religions"),
            Map.entry("1004", "Agnosticism"),
            Map.entry("1005", "Anglican"),
            Map.entry("1006", "Animism"),
            Map.entry("1007", "Atheism"),
            Map.entry("1008", "Babi & Baha'I faiths"),
            Map.entry("1009", "Baptist"),
            Map.entry("1010", "Bon"),
            Map.entry("1011", "Cao Dai"),
            Map.entry("1012", "Celticism"),
            Map.entry("1013", "Christian (non-Catholic, non-specific)"),
            Map.entry("1014", "Confucianism"),
            Map.entry("1015", "Cyberculture Religions"),
            Map.entry("1016", "Divination"),
            Map.entry("1017", "Fourth Way"),
            Map.entry("1018", "Free Daism"),
            Map.entry("1019", "Gnosis"),
            Map.entry("1020", "Hinduism"),
            Map.entry("1021", "Humanism"),
            Map.entry("1022", "Independent"),
            Map.entry("1023", "Islam"),
            Map.entry("1024", "Jainism"),
            Map.entry("1025", "Jehovah's Witnesses"),
            Map.entry("1026", "Judaism"),
            Map.entry("1027", "Latter Day Saints"),
            Map.entry("1028", "Lutheran"),
            Map.entry("1029", "Mahayana"),
            Map.entry("1030", "Meditation"),
            Map.entry("1031", "Messianic Judaism"),
            Map.entry("1032", "Mitraism"),
            Map.entry("1033", "New Age"),
            Map.entry("1034", "non-Roman Catholic"),
            Map.entry("1035", "Occult"),
            Map.entry("1036", "Orthodox"),
            Map.entry("1037", "Paganism"),
            Map.entry("1038", "Pentecostal"),
            Map.entry("1039", "Process, The"),
            Map.entry("1040", "Reformed/Presbyterian"),
            Map.entry("1041", "Roman Catholic Church"),
            Map.entry("1042", "Satanism"),
            Map.entry("1043", "Scientology"),
            Map.entry("1044", "Shamanism"),
            Map.entry("1045", "Shiite (Islam)"),
            Map.entry("1046", "Shinto"),
            Map.entry("1047", "Sikism"),
            Map.entry("1048", "Spiritualism"),
            Map.entry("1049", "Sunni (Islam)"),
            Map.entry("1050", "Taoism"),
            Map.entry("1051", "Theravada"),
            Map.entry("1052", "Unitarian-Universalism"),
            Map.entry("1053", "Universal Life Church"),
            Map.entry("1054", "Vajrayana (Tibetan)"),
            Map.entry("1055", "Veda"),
            Map.entry("1056", "Voodoo"),
            Map.entry("1057", "Wicca"),
            Map.entry("1058", "Yaohushua"),
            Map.entry("1059", "Zen Buddhism"),
            Map.entry("1060", "Zoroastrianism"),
            Map.entry("1061", "Assembly of God"),
            Map.entry("1062", "Brethren"),
            Map.entry("1063", "Christian Scientist"),
            Map.entry("1064", "Church of Christ"),
            Map.entry("1065", "Church of God"),
            Map.entry("1066", "Congregational"),
            Map.entry("1067", "Disciples of Christ"),
            Map.entry("1068", "Eastern Orthodox"),
            Map.entry("1069", "Episcopalian"),
            Map.entry("1070", "Evangelical Covenant"),
            Map.entry("1071", "Friends"),
            Map.entry("1072", "Full Gospel"),
            Map.entry("1073", "Methodist"),
            Map.entry("1074", "Native American"),
            Map.entry("1075", "Nazarene"),
            Map.entry("1076", "Presbyterian"),
            Map.entry("1077", "Protestant"),
            Map.entry("1078", "Protestant, No Denomination"),
            Map.entry("1079", "Reformed"),
            Map.entry("1080", "Salvation Army"),
            Map.entry("1081", "Unitarian Universalist"),
            Map.entry("1082", "United Church of Christ"))
    );

    // Use the indicated ConceptMap to translate the inputCode to relevant FHIR code Display value
    public static String codeSystemLookup(String mapId, String csId, Object inputCode) {
      
        //  Translate the inputCode
        String code = conceptMapTranslate(mapId, inputCode);
        if(code == null) {
            return null;
        }

        // Make sure we've got a valid CodeSystem Id
        Map<String, String> csMap = codeSystemMaps.get(csId);
        if(csMap == null) {
            LOGGER.debug(String.format("No CodeSystem with id '%s' found", csId));
            return null;
        }

        // Make sure we've got a retVal
        String retVal = csMap.get(code);
        if(retVal == null) {
            LOGGER.debug(String.format("Code %s not found in CodeSystem %s", code, csId));
            return null;
        }

        // All good
        return retVal;
    }

    // Return a Base64 encoded string containing the whole HL7v2 message containing the given message segment
    public static String hl7MessageFromSegment(Segment segment) {
        try {
            return Base64.getEncoder().encodeToString(segment.getMessage().encode().getBytes());
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

}
