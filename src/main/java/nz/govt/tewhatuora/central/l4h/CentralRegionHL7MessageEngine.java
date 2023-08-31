package nz.govt.tewhatuora.central.l4h;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;

import io.github.linuxforhealth.api.EvaluationResult;
import io.github.linuxforhealth.api.InputDataExtractor;
import io.github.linuxforhealth.api.FHIRResourceTemplate;
import io.github.linuxforhealth.fhir.FHIRContext;
import io.github.linuxforhealth.hl7.message.HL7MessageData;
import io.github.linuxforhealth.hl7.message.HL7MessageEngine;


public class CentralRegionHL7MessageEngine extends HL7MessageEngine {

    private Map<String, Object> customFunctions = new HashMap<String, Object>();
    
    // We need some extra customFunctions in NZ CentralRegion
    public CentralRegionHL7MessageEngine(FHIRContext context, Bundle.BundleType bundleType) {
        super(context, bundleType);

        // add our set of utils as a custom set
        customFunctions.put("CentralRegionUtils", CentralRegionUtils.class);   // Just the one set of extra utilities (for now)
    }

    @Override
    public Bundle transform(InputDataExtractor dataInput, Iterable<FHIRResourceTemplate> resources, Map<String, EvaluationResult> contextValues) {
        HL7MessageData data = (HL7MessageData) dataInput;
        HL7MessageData newMessageData = new HL7MessageData(data.getHL7DataParser(), customFunctions);
        return super.transform(newMessageData, resources, contextValues);
    }
}
