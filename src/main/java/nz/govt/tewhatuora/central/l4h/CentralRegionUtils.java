/*
 * Te Whatu Ora, Central Region
 * 
 * This is the PROD edition of CentralRegionUtils and it must make native SmileCDR API calls to do the translations.
 * 
 * The other edition uses the public FHIR ConceptMap $translate API to do the conceptMap
 * code translations.
 * 
 * NOTE:   we are using Java ClassPath ordering magic to ensure the correct edition of this file is run in TEST and PROD environments
 */
package nz.govt.tewhatuora.central.l4h;

import java.util.Base64;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.model.Segment;
import io.github.linuxforhealth.hl7.data.Hl7DataHandlerUtil;


public class CentralRegionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralRegionUtils.class);


    // Do nothing default constructor
    private CentralRegionUtils() {
    }

    // Turn Y,N,U unto "yes", "no" or "unknown" - for Code fields
    private static Map<String, String> ynuCodeMap = Map.of("Y", "yes",
            "N", "no",
            "U", "unknown");

    public static String ynuCode(Object input) {

        String ynu = Hl7DataHandlerUtil.getStringValue(input);
        return ynuCodeMap.get(ynu);
    }

    // Turn Y,N,U unto "Yes", "No" or "Unknown" - for Display fields
    private static Map<String, String> ynuDisplayMap = Map.of("Y", "Yes",
            "N", "No",
            "U", "Unknown");

    public static String ynuDisplay(Object input) {

        String ynu = Hl7DataHandlerUtil.getStringValue(input);
        return ynuDisplayMap.get(ynu);
    }

    // A map of maps to Code/Display tuples
    private static Map<String, Map<String, AbstractMap.SimpleImmutableEntry<String, String>>> conceptMaps = new HashMap<String, Map<String, AbstractMap.SimpleImmutableEntry<String, String>>>();
 
    
    // Return a Base64 encoded string containing the whole HL7v2 message containing
    // the given message segment
    public static String hl7MessageFromSegment(Segment segment) {
        try {
            LOGGER.debug("Extracting source HL7 message from Segment %s", segment.getClass());
            return Base64.getEncoder().encodeToString(segment.getMessage().encode().getBytes());
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    //
    // TODO: In production this method needs to make SmileCDR native API calls to do the translate
    //
    // Code Translate worker function that :-
    // 1. Checks the conceptMaps cache for relevant value
    // 2. If not found go do a $Translate call against Terminology Server
    // 3. Pop the resulting code & display value into the cache
    // 4. Return the requested return value
/*
    private static String codeTranslate(String mapId, String system, String code, boolean returnDisplay) {

        // Have we been given good values to search?
        if (code == null) {
            LOGGER.error(String.format("No ConceptCode presented for translation by ConceptMap %s", mapId));
            return null;
        }

        if (mapId == null) {
            LOGGER.error(String.format("No ConceptMapId presented for translation of ConceptCode %s", code));
            return null;
        }

        // Let's check the cache first
        Map<String, AbstractMap.SimpleImmutableEntry<String, String>> cMap = conceptMaps.get(mapId); // Do we have that contextMap ?
        AbstractMap.SimpleImmutableEntry<String, String> tuple = cMap == null ? null : cMap.get(code); // Do we have that code in the contextMap ?

        if (tuple != null) {
            return returnDisplay ? tuple.getValue() : tuple.getKey();
        }

         // Ok it's not in the cache - we gotta go ask the Terminology Server - create a client
        IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/fhir");

        Parameters inParams = new Parameters();
        inParams.addParameter().setName("system").setValue(new StringType(system));
        inParams.addParameter().setName("code").setValue(new StringType(code));

        Parameters outParams = client.operation()
                .onInstance(new IdType("ConceptMap", mapId))
                .named("$translate")
                .withParameters(inParams)
                .useHttpGet() // Use HTTP GET instead of POST
                .execute();

        boolean result = outParams.getParameterBool("result");
        if (!result) {
            LOGGER.error("Expected RESULT boolean value to be TRUE");
            return null;
        }
        ParametersParameterComponent match = outParams.getParameter("match");

        // One of the parts should be called Concept....
        Type typ = getPartNamed(match, "concept");
        if (!(typ instanceof Coding)) {
            LOGGER.error(String.format("Expected Coding in match.concept but got %s", typ.getClass()));
            return null;
        }
        Coding coding = (Coding) typ;

        // Add it to the cache
        tuple = new AbstractMap.SimpleImmutableEntry<String, String>(coding.getCode(), coding.getDisplay()); // We've got the tuple
        if (cMap == null) {
            // Make the missing contextMap and add it to the set of Maps....
            cMap = new HashMap<String, AbstractMap.SimpleImmutableEntry<String, String>>();
            conceptMaps.put(mapId, cMap);
        }
        cMap.put(code, tuple);

        // ... and we're done -- did they want the Code or the Display value ??
        return returnDisplay ? coding.getDisplay() : coding.getCode();
    }
*/

    // Use the given ConceptMapId to translate given Code from given System into the
    // matching tuple's Code value
    public static String translateCodeToCode(String conceptMapId, String system, Object code) {
        
        // TODO: In production this method needs to make SmileCDR native API calls to do the translate
        return null;

        // return codeTranslate(conceptMapId, system, code.toString(), false);
    }

    // Use the given ConceptMapId to translate given Code from given System into the
    // matching tuple's Display value
    public static String translateCodeToDisplay(String conceptMapId, String system, Object code) {

        // TODO: In production this method needs to make SmileCDR native API calls to do the translate
        return null;

        // return codeTranslate(conceptMapId, system, code.toString(), true);
    }
}
