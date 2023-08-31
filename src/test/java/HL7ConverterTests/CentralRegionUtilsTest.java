/*
 * (C) Copyright IBM Corp. 2020, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package HL7ConverterTests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nz.govt.tewhatuora.central.l4h.CentralRegionUtils;

class CentralRegionUtilsTest {

    // private static final Logger LOGGER = LoggerFactory.getLogger(CentralRegionUtilsTest.class);

    @ParameterizedTest
    @CsvSource({ "Y,yes", 
                 "N,no", 
                 "U,unknown", 
                 "F,"}) // lowercase
    void get_ynuCode(String code, String result) {
        assertThat(CentralRegionUtils.ynuCode(code)).isEqualTo(result);
    }

    @ParameterizedTest
    @CsvSource({ "Y,Yes", 
                 "N,No", 
                 "U,Unknown",
                 "F,"})   // UpperCamelCase
    void get_ynuDisplay(String code, String result) {
        assertThat(CentralRegionUtils.ynuDisplay(code)).isEqualTo(result);
    }


    // Check one value from each ConceptMap (makes sure we haven't managed to get duplicate keys in our maps)...
    @ParameterizedTest
    @CsvSource({ "nzcr-nz-residency-map,CWH,yes", 
                 "nzcr-ethnicity-2to4-map,21,21111"})
    void get_conceptMapTranslate(String mapId, String inputCode, String result) {
        assertThat(CentralRegionUtils.conceptMapTranslate(mapId, inputCode)).isEqualTo(result);
    }    

    // Check one value from each CodeSystem (makes sure we haven't managed to get duplicate keys in our maps)...
    @ParameterizedTest
    @CsvSource({ "nzcr-nz-residency-map, https://standards.digital.health.nz/ns/nz-residency-code,CWH,Permanent Resident", 
                 "nzcr-ethnicity-2to4-map, https://standards.digital.health.nz/ns/ethnic-group-level-4-code,53,African nfd"})
    void get_codeSystemLookup(String mapId, String csId, Object inputCode, String result) {
        assertThat(CentralRegionUtils.codeSystemLookup(mapId, csId, inputCode)).isEqualTo(result);
    }    
}
