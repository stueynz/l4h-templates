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

    // Check a value from each ConceptMap 
    @ParameterizedTest
    @CsvSource({ "nzcr-nz-residency-map,https://standards.digital.health.nz/ns/central-region/nz-residency-code,CWH,yes,Permanent Resident", 
                 "nzcr-ethnicity-2to4-map,https://standards.digital.health.nz/ns/ethnic-group-level-2-code,21,21111,Māori",
                 "nzcr-language-map,https://standards.digital.health.nz/ns/central-region/pas-language,MAO,mi,Māori",
                 "nzcr-religion-map,https://standards.digital.health.nz/ns/central-region/patient-religion,R01,1005,Anglican"})

    void get_codeTranslateToCodeAndDisplay(String mapId, String inputSystem, String inputCode, String resultCode, String resultDisplay) {
        assertThat(CentralRegionUtils.translateCodeToCode(mapId, inputSystem, inputCode)).isEqualTo(resultCode);
        assertThat(CentralRegionUtils.translateCodeToDisplay(mapId, inputSystem, inputCode)).isEqualTo(resultDisplay);
    }    

}
