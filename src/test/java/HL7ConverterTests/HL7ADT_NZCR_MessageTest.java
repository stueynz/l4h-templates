/*
 * (C) Copyright IBM Corp. 2020, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package HL7ConverterTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;

class HL7ADT_NZCR_MessageTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HL7ADT_NZCR_MessageTest.class);

    private HL7ToFHIRConverter ftv = new HL7ToFHIRConverter();

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS sends either NHI or temporary identifier in PID-3
    @ValueSource(strings = { "ADT^A01", "ADT^A04", "ADT^A08"/* , "ADT^A13" */ })
    void testAdtA01_NZCR_UsualNHI(String message) throws IOException {
               
        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"

        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&2.16.840.1.113883.2.18.2||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patientResource = (Patient) patientResourceList.get(0);
        
        // let's check a few fields...       
        assertThat(patientResource.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got NHI identifier correctly
        assertThat(patientResource.hasIdentifier()).isTrue();
        List<Identifier> identifierList = patientResource.getIdentifier();
        assertThat(identifierList).hasSize(1);  // just the one identifier

        // Make sure all the fields are present
        Identifier nhiId = identifierList.get(0); 
        assertThat(nhiId.getSystem()).isEqualTo("https://standards.digital.health.nz/ns/nhi-id");
        assertThat(nhiId.getValue()).isEqualTo("ZHY4846");
        assertThat(nhiId.getUse()).isEqualTo(Identifier.IdentifierUse.USUAL);

        // Should have just one coding value
        CodeableConcept idType = nhiId.getType();
        assertThat(idType.getText()).isNull();   // no "chosen" text on codeableConcept
        assertThat(idType.getCoding().size()).isEqualTo(1);  // just the one Coding

        // Coding should have appropriate values
        Coding cdType = idType.getCoding().get(0);
        assertThat(cdType.getSystem()).isEqualTo("http://terminology.hl7.org/CodeSystem/v2-0203");
        assertThat(cdType.getCode()).isEqualTo("MR");
        assertThat(cdType.getDisplay()).isEqualTo("Medical record number");
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS sends either NHI or temporary identifier in PID-3
    @ValueSource(strings = { "ADT^A01", "ADT^A04", "ADT^A08"/* , "ADT^A13" */ })
    void testAdtA01_NZCR_TemporaryNHI(String message) throws IOException {
               
        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"

        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patientResource = (Patient) patientResourceList.get(0);

        // let's check a few fields...       
        assertThat(patientResource.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got NHI identifier correctly
        assertThat(patientResource.hasIdentifier()).isTrue();
        List<Identifier> identifierList = patientResource.getIdentifier();
        assertThat(identifierList).hasSize(1);  // just the one identifier

        // Make sure all the fields are present
        Identifier nhiId = identifierList.get(0); 
        assertThat(nhiId.getSystem()).isEqualTo("https://standards.digital.health.nz/ns/central_region/pas-patient-id");
        assertThat(nhiId.getValue()).isEqualTo("ZHY4846");
        assertThat(nhiId.getUse()).isEqualTo(Identifier.IdentifierUse.TEMP);

        // Should have just one coding value
        CodeableConcept idType = nhiId.getType();
        assertThat(idType.getText()).isNull();   // no "chosen" text on codeableConcept
        assertThat(idType.getCoding().size()).isEqualTo(1);  // just the one Coding

        // Coding should have appropriate values
        Coding cdType = idType.getCoding().get(0);
        assertThat(cdType.getSystem()).isEqualTo("http://terminology.hl7.org/CodeSystem/v2-0203");
        assertThat(cdType.getCode()).isEqualTo("MR");
        assertThat(cdType.getDisplay()).isEqualTo("Medical record number");
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    @ValueSource(strings = { "ADT^A01", "ADT^A04", "ADT^A08"/* , "ADT^A13" */ })
    void testAdtA01_NZCR_NameFields(String message) throws IOException {
               
        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"

        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^B|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patientResource = (Patient) patientResourceList.get(0);

        // let's check a few fields...       
        assertThat(patientResource.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got a pair of names; an OLD and a USUAL one; and the values are correct
        List<HumanName> nameList = patientResource.getName();
        assertThat(nameList.size()).isEqualTo(2);

        assertThat(nameList).filteredOn(hname -> hname.getUse().equals(HumanName.NameUse.USUAL))
            .hasSize(1)
            .allSatisfy( hname -> {
                assertThat(hname.getGivenAsSingleString()).isEqualTo("STEVE");
                assertThat(hname.getFamily()).isEqualTo("MCH STEPHENS");
                assertThat(hname.getPrefixAsSingleString()).isEqualTo("FR");
                assertThat(hname.getText()).isEqualTo("FR STEVE MCH STEPHENS");
            } );

        // Ensuring that default mapping of B (Name at Birth) -> OFFICIAL is overwritten to OLD
        assertThat(nameList).filteredOn(hname -> hname.getUse().equals(HumanName.NameUse.OLD))
            .hasSize(1)
            .allSatisfy( hname -> { 
                assertThat(hname.getGivenAsSingleString()).isEqualTo("Stephen Frederick");
                assertThat(hname.getFamily()).isEqualTo("Stephens");
                assertThat(hname.getPrefixAsSingleString()).isEmpty();
                assertThat(hname.getText()).isEqualTo("Stephen Frederick Stephens");
            } );
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS sends weird PID-13 and PID-14 segments where some repetitions have no actual Phone No / Email Address value
    //   PH -> phone
    //   FX -> fax
    //   CP -> sms
    //   BP -> pager
    //   NET -> email
    @CsvSource({ "ADT^A01, PH,02758880032,",           "ADT^A04,PH,02758880032,",           "ADT^A08,PH,02758880032,",
                 "ADT^A01, FX,02758880032,",           "ADT^A04,FX,02758880032,",           "ADT^A08,FX,02758880032,",
                 "ADT^A01, CP,02758880032,",           "ADT^A04,CP,02758880032,",           "ADT^A08,CP,02758880032,",
                 "ADT^A01, BP,02758880032,",           "ADT^A04,BP,02758880032,",           "ADT^A08,BP,02758880032,",
                 "ADT^A01, NET,,hello@stuart.geek.nz", "ADT^A04,NET,,hello@stuart.geek.nz", "ADT^A08,NET,,hello@stuart.geek.nz"
 })
    void testAdtA01_NZCR_TelecomFields(String message, String eqType, String phNo, String email) throws IOException {
               
        Map<String, ContactPoint.ContactPointSystem> typeMap = Map.of(
             "PH",  ContactPoint.ContactPointSystem.PHONE, 
             "FX",  ContactPoint.ContactPointSystem.FAX,
             "CP",  ContactPoint.ContactPointSystem.SMS,
             "BP",  ContactPoint.ContactPointSystem.PAGER,
             "NET", ContactPoint.ContactPointSystem.EMAIL);

        Map<String, ContactPoint.ContactPointUse> useMap = Map.of(
             "PH",  ContactPoint.ContactPointUse.HOME,
             "FX",  ContactPoint.ContactPointUse.HOME,
             "CP",  ContactPoint.ContactPointUse.MOBILE,
             "BP",  ContactPoint.ContactPointUse.HOME,
             "NET", ContactPoint.ContactPointUse.HOME);

        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|" +
        
              "^PRN^PH~" + phNo + "^ORN^"+ eqType + (email == null ? "" : "^" + email) + "|^WPN^PH|" + 
        
        "ENG^English^NHDD-132^U|U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patientResource = (Patient) patientResourceList.get(0);

        // let's check a few fields...       
        assertThat(patientResource.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got one actual phone number; use MOBILE
        List<ContactPoint> phoneList = patientResource.getTelecom();
        assertThat(phoneList.size()).isEqualTo(1);

        assertThat(phoneList).filteredOn(phone -> phone.getUse().equals(useMap.get(eqType)))
            .hasSize(1)
            .allSatisfy( phone -> {
                assertThat(phone.getSystem()).isEqualTo(typeMap.get(eqType));
                assertThat(phone.getValue()).isEqualTo(phNo != null ? phNo : email);
            } );
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS sends weird PID-11 segments where some repetitions have no actual Address value
    //                   - WebPAS uses address types C - Residential and M - Postal    (go figure)
    //
    @CsvSource({"ADT^A01,home", 
                "ADT^A01,postal",
                "ADT^A01,both",

                "ADT^A04,home", 
                "ADT^A04,postal",
                "ADT^A04,both",

                "ADT^A08,home", 
                "ADT^A08,postal",
                "ADT^A08,both"
            })
    void testAdtA01_NZCR_HomeAddressFields(String message, String addrType) throws IOException {

        String[] pid11 = new String[] {"100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C^Kelvin Grove~" + "^^^^^NEW ZEALAND^M|",
                          "^^^^^NEW ZEALAND^C~" + "P O Box 100^^PalmerstonNorth^^4410^NEW ZEALAND^M|",
                          "100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C^Kelvin Grove~" + "P O Box 100^^Palmerston North^^4410^NEW ZEALAND^M|"
                        };

        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|" +
 
            pid11[addrType.equals("home") ? 0 : addrType.equals("postal") ? 1 : 2] +
           
           "1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patientResource = (Patient) patientResourceList.get(0);

        // let's check a few fields...       
        assertThat(patientResource.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got the right number of addresses
        List<Address> addrList = patientResource.getAddress();

        // HOME - 1 address of type PHYSICAL
        if(addrType.equals("home")) {
            assertThat(addrList)
                .hasSize(1)
                .allSatisfy( addr -> {
                    assertThat(addr.getLine()).hasSize(1);
                    assertThat(addr.getLine().get(0).getValue()).isEqualTo("100 Broadway Avenue");
                    assertThat(addr.getUse()).isEqualTo(Address.AddressUse.HOME);
                    assertThat(addr.getType()).isEqualTo(Address.AddressType.PHYSICAL);

                    // Check the suburb is in the right place
                    assertThat(addr.getExtension())
                        .hasSize(1)
                        .allSatisfy( extn -> {
                            assertThat(extn.getUrl()).isEqualTo("http://hl7.org.nz/fhir/StructureDefinition/suburb");
                            assertThat(extn.getValue()).hasToString("Kelvin Grove");   // What a horrible way to get the actual StringValue
                        });
                } ) ;
        }

        // POSTAL - 1 address of type POSTAL
        else if (addrType.equals("postal")) {
            assertThat(addrList)
                .hasSize(1)
                .allSatisfy( addr -> {
                    assertThat(addr.getLine()).hasSize(1);
                    assertThat(addr.getLine().get(0).getValue()).isEqualTo("P O Box 100");
                    assertThat(addr.getUse()).isEqualTo(Address.AddressUse.HOME);
                    assertThat(addr.getType()).isEqualTo(Address.AddressType.POSTAL);

                    assertThat(addr.getExtension()).isEmpty();  // Not expecting any extensions on address...
                } ) ;
        }

        // BOTH - 2 addresses of types PHYSICAL, POSTAL
        else {
            assertThat(addrList.size()).isEqualTo(2);
            assertThat(addrList).filteredOn(ad -> ad.getType().equals(Address.AddressType.PHYSICAL))  // check PHYSICAL
                .hasSize(1)
                .allSatisfy( addr -> {
                    assertThat(addr.getLine()).hasSize(1);
                    assertThat(addr.getLine().get(0).getValue()).isEqualTo("100 Broadway Avenue");
                    assertThat(addr.getUse()).isEqualTo(Address.AddressUse.HOME);

                    // Check the suburb is in the right place
                    assertThat(addr.getExtension())
                        .hasSize(1)
                        .allSatisfy( extn -> {
                            assertThat(extn.getUrl()).isEqualTo("http://hl7.org.nz/fhir/StructureDefinition/suburb");
                            assertThat(extn.getValue()).hasToString("Kelvin Grove");   // What a horrible way to get the actual StringValue
                        });
                }) ;

            assertThat(addrList).filteredOn(ad -> ad.getType().equals(Address.AddressType.POSTAL))   // check POSTAL
                .hasSize(1)
                .allSatisfy( addr -> {
                    assertThat(addr.getLine()).hasSize(1);
                    assertThat(addr.getLine().get(0).getValue()).isEqualTo("P O Box 100");
                    assertThat(addr.getUse()).isEqualTo(Address.AddressUse.HOME);

                    assertThat(addr.getExtension()).isEmpty();  // Not expecting any extensions on address...
                } ) ;
            }
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS sends StatsNZ Domicile code alongside address
    @ValueSource(strings = { "ADT^A01", "ADT^A04", "ADT^A08"/* , "ADT^A13" */ })
    void testAdtA01_NZCR_DomicileExtensions(String message) throws IOException {
               
        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patient = (Patient) patientResourceList.get(0);
        assertThat(patient.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got one Domicile extension
        assertThat(patient.getExtensionsByUrl("http://hl7.org.nz/fhir/StructureDefinition/domicile-code"))
            .hasSize(1)
            .element(0).satisfies(extn -> {
            
                // Grab the CodeableConcept value
                assertThat((CodeableConcept) extn.getValue()).satisfies( cc-> {

                    // Make sure there's only one Coding; that System and Code fields are correct
                    assertThat(cc.getCoding())
                        .hasSize(1)
                        .element(0).satisfies(cdg -> {

                            assertThat(cdg.getSystem()).isEqualTo("https://standards.digital.health.nz/ns/domicile-code");
                            assertThat(cdg.getCode()).isEqualTo("1851");
                        });
                });
            });
    }


    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS sends ethnicity as level-2 need to add level-4 equivalent
    @ValueSource(strings = { "ADT^A01", "ADT^A04", "ADT^A08"/* , "ADT^A13" */ })
    void testAdtA01_NZCR_EthnicityExtensions(String message) throws IOException {
               
        LOGGER.debug("classpath = " + System.getProperty("java.class.path"));

        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002|R01^C of E/Anglican|||||" + 
        
           "21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS|" + 
            
           "|||||||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patientResource = (Patient) patientResourceList.get(0);
        assertThat(patientResource.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got the target extension
        List<Extension> extnList = patientResource.getExtensionsByUrl("http://hl7.org.nz/fhir/StructureDefinition/nz-ethnicity");
        assertThat(extnList)
            .hasSize(3)  // The message has three ethnicity values
            .allSatisfy( extn -> {

                // Make sure that each ConceptualValue has two codings (one for each ethnicity code)
                assertThat((CodeableConcept) extn.getValue()).satisfies( cc -> {
                    assertThat(cc.getText()).isNull();
                    assertThat(cc.getCoding()).satisfies(cdgs -> {
                         // has 2 Codings one at level-2 and another at level-4
                        assertThat(cdgs).hasSize(2);

                        assertThat(cdgs).filteredOn(cdg -> cdg.getSystem().equals("https://standards.digital.health.nz/ns/ethnic-group-level-2-code"))
                            .hasSize(1)
                            .element(0).satisfies(cdg -> {

                                switch(Integer.parseInt(cdg.getCode())) {
                                    case 21: 
                                        assertThat(cdg.getDisplay()).isEqualTo("NZ Maori");
                                        break;
                                    
                                    case 11:
                                        assertThat(cdg.getDisplay()).isEqualTo("NZ European / Pakeha");
                                        break;

                                    case 32:
                                        assertThat(cdg.getDisplay()).isEqualTo("Cook Island Maori");
                                        break;
                                }                               
                            });
                        assertThat(cdgs).filteredOn(cdg -> cdg.getSystem().equals("https://standards.digital.health.nz/ns/ethnic-group-level-4-code"))
                            .hasSize(1)
                            .element(0).satisfies(cdg -> {
                                switch(Integer.parseInt(cdg.getCode())) {
                                    case 21111: 
                                        assertThat(cdg.getDisplay()).isEqualTo("Māori");
                                        break;
                                    
                                    case 11111:
                                        assertThat(cdg.getDisplay()).isEqualTo("New Zealand European");
                                        break;

                                    case 32100:
                                        assertThat(cdg.getDisplay()).isEqualTo("Cook Island Māori");
                                        break;
                                }                               
                            });
                    });
                });
            } );
    }


    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS sends custom codes R01 etc.
    @CsvSource({ "ADT^A01,R01,C of E/Anglican", "ADT^A04,R01,C of E/Anglican", "ADT^A08,R01,C of E/Anglican",
                 "ADT^A01,R38,Ratana",          "ADT^A04,R38,Ratana",          "ADT^A08,R38,Ratana" })
    void testAdtA01_NZCR_ReligionExtensions(String message, String code, String display) throws IOException {
               

        //  R38 - Ratana won't translate to v3 religions
        String v3Code = "1005";
        String v3Display = "Anglican";
        int expectedCodingCount = code.equals("R38") ? 1 : 2;

        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002|" + 
        
          code + "^" + display + 
          
          "|||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patientResource = (Patient) patientResourceList.get(0);
        assertThat(patientResource.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got the target extension
        List<Extension> extnList = patientResource.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/patient-religion");
        assertThat(extnList)
            .hasSize(1)
            .allSatisfy( extn -> {

                assertThat((CodeableConcept) extn.getValue()).satisfies( cc -> {

                    assertThat(cc.getCoding()).satisfies(cdgs -> {

                        assertThat(cdgs).hasSize(expectedCodingCount);
                        assertThat(cdgs)
                            .filteredOn(cdg -> cdg.getSystem().equals("https://standards.digital.health.nz/ns/central-region/patient-religion"))
                            .hasSize(1)
                            .element(0).satisfies(cdg -> {
                                assertThat(cdg.getCode()).isEqualTo(code);
                                assertThat(cdg.getDisplay()).isEqualTo(display);
                            });

                        // Some religions don't have v3 codes
                        if (expectedCodingCount == 2) {
                            assertThat(cdgs)
                                .filteredOn(cdg -> cdg.getSystem().equals("http://terminology.hl7.org/CodeSystem/v3-ReligiousAffiliation"))
                                .hasSize(1)
                                .element(0).satisfies(cdg -> {
                                    assertThat(cdg.getCode()).isEqualTo(v3Code);
                                    assertThat(cdg.getDisplay()).isEqualTo(v3Display);
                            });
                        }
                    });
                });
            });
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    //  In CentralRegion - WebPAS has a coupole of extra marital status values U - CivlUnion,  C - DeFacto
    @CsvSource({ "ADT^A01,C", "ADT^A04,C", "ADT^A08,C",
                 "ADT^A01,V", "ADT^A04,V", "ADT^A08,V",
                 "ADT^A01,U", "ADT^A04,U", "ADT^A08,U", })
    void testAdtA01_NZCR_MaritalStatus(String message, String code) throws IOException {


        Map<String, String> typeMap = Map.of(
             "C", "DE FACTO",
             "V", "CIVIL UNION", 
             "U", "UNKNOWN");

        Map<String, String> sysMap = Map.of(
             "C", "https://standards.digital.health.nz/ns/central-region/extra-marital-status",
             "V", "https://standards.digital.health.nz/ns/central-region/extra-marital-status", 
             "U", "http://terminology.hl7.org/CodeSystem/v3-MaritalStatus");

        String text = typeMap.get(code);
        String system = sysMap.get(code);

        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|" +
            
            code + "^" + text +  "^HL70002|" + 
            
          "ANG^Church of England|||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        assertThat(ResourceUtils.getResourceList(e, ResourceType.Patient))
            // Only one Patient
            .hasSize(1)

            // Check the single Patient
            .element(0).satisfies( p -> {

                assertThat(p.hasIdElement()).isFalse();  // We're not generating Id

                // Check MaritalStatus field...
                assertThat( ((Patient) p).getMaritalStatus())
                    
                    .isNotNull()
                    .satisfies( cc -> { 
                        assertThat(cc.getText()).isEqualTo(text);
                        assertThat(cc.getCoding())

                            .hasSize(1)
                            .element(0).satisfies( cdg -> {

                                assertThat(cdg.getCode()).isEqualTo(code);
                                assertThat(cdg.getDisplay()).isEqualTo(text);
                                assertThat(cdg.getSystem()).isEqualTo(system);
                            });
                    });
            });
    }    


    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    // Translation of nzCitizenship Y/N to yes/no
    @CsvSource({ "ADT^A01,Y", "ADT^A04,Y", "ADT^A08,Y",
                 "ADT^A01,N", "ADT^A04,N", "ADT^A08,N",
                 "ADT^A01,U", "ADT^A04,U", "ADT^A08,U"}) 
    void testAdtA01_NZCR_CitizenshipExtensions(String message, String value) throws IOException {
               
        String code = value.equals("Y") ? "yes" : value.equals("N") ? "no" : "unknown";
        String text = value.equals("Y") ? "Yes" : value.equals("N") ? "No" : "Unknown";
        
        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||" +

            value +"||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"

        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList)
        
            .hasSize(1)   // from PID
            .element(0).satisfies( p -> {

                assertThat(p.hasIdElement()).isFalse();  // We're not generating Id

                assertThat( ((Patient) p).getExtensionsByUrl("http://hl7.org.nz/fhir/StructureDefinition/nz-citizenship"))
                    .hasSize(1)
                    .element(0).satisfies( extn -> {


                        // Make sure the nzCitizenship extension has only one field called STATUS
                        assertThat(extn.getExtension())
                            .hasSize(1)
                            .element(0).satisfies( status -> {

                                assertThat( status.getUrl()).isEqualTo("status");
                                assertThat( (CodeableConcept) status.getValue())
                                    .isNotNull()

                                    // ... which is a CodeableConcept
                                    .satisfies( cc -> {
                                         assertThat(cc.getText()).isNull();  // No user-chosen text field
                                         assertThat(cc.getCoding())
                                            .hasSize(1)             // 1 coding
                                            .element(0)

                                            // ... with expected values
                                            .satisfies(cdg -> {
                                                    assertThat(cdg.getSystem()).isEqualTo("https://standards.digital.health.nz/ns/nz-citizenship-status-code");
                                                    assertThat(cdg.getCode()).isEqualTo(code);
                                                    assertThat(cdg.getDisplay()).isEqualTo(text);
                                            });
                                    });                                
                            });

                    });
            });
    }
   
    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    // In CentralRegion - NZResidency has more granularity than simple Y/N
    @CsvSource({ "ADT^A01,NZR", "ADT^A04,NZ", "ADT^A08,CNE",
                 "ADT^A01,NZR", "ADT^A04,NZ", "ADT^A08,CNE",
                 "ADT^A01,NZR", "ADT^A04,NZ", "ADT^A08,CNE", })
    void testAdtA01_NZCR_ResidencyExtension(String message, String code) throws IOException {

        String text = code.equals("NZR") ? "NZ residence visa" : code.equals("NZ") ? "New Zealand citizen" : "Child <18 not eligible";

        String nzbCode = code.equals("NZR") ? "yes" : code.equals("NZ") ? "no" : code.equals("CNE") ? "no" : "unknown";
        String nzbText = code.equals("NZR") ? "Permanent Resident" : code.equals("NZ") ? "Not a Permanent Resident" : code.equals("CNE") ? "Not a Permanent Resident" : "Unknown";

        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002|ANG^Church of England|||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||" 
        
           + code + "^" + text + "^RHIP_Resident_Status||N\r"

        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        assertThat(ResourceUtils.getResourceList(e, ResourceType.Patient))
            // Only one Patient
            .hasSize(1)

            // Check the single Patient
            .element(0).satisfies( p -> {

                assertThat(p.hasIdElement()).isFalse();  // We're not generating Id

                // Check NZResidency extension
                assertThat( ((Patient) p).getExtensionByUrl("http://hl7.org.nz/fhir/StructureDefinition/nz-residency"))
                    
                    .isNotNull()
                    .satisfies( resExtn -> { 

                        // Make sure the nzResidency extension has only one field and it's called STATUS
                        assertThat(resExtn.getExtension())
                            .hasSize(1)
                            .element(0).satisfies( status -> {

                                assertThat( status.getUrl()).isEqualTo("status");
                                assertThat( (CodeableConcept) status.getValue())
                                    .isNotNull()
                                    .satisfies( cc -> {
                                         assertThat(cc.getText()).isNull();  // No user-chosen text field
                                         assertThat(cc.getCoding())
                                            .hasSize(2)             // 2 codings
                                            .satisfies(cdgs -> {

                                                // As proscribed by NZBase
                                                assertThat(cdgs).filteredOn(cdg -> cdg.getSystem().equals("https://standards.digital.health.nz/ns/nz-residency-code"))
                                                    .hasSize(1)
                                                    .element(0).satisfies(cdg -> {
                                                        assertThat(cdg.getCode()).isEqualTo(nzbCode);
                                                        assertThat(cdg.getDisplay()).isEqualTo(nzbText);
                                                    });

                                                // raw codes from WebPAS
                                                assertThat(cdgs).filteredOn(cdg -> cdg.getSystem().equals("https://standards.digital.health.nz/ns/central-region/nz-residency-code"))
                                                    .hasSize(1)
                                                    .element(0).satisfies(cdg -> {
                                                        assertThat(cdg.getCode()).isEqualTo(code);
                                                        assertThat(cdg.getDisplay()).isEqualTo(text);
                                                    });
                                            });
                                    });                                
                            });
                    });
            });
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    @CsvSource({ "ADT^A01,ENG,Y,en",     "ADT^A04,ENG,N,en",   "ADT^A08,ENG,U,en",
                 "ADT^A01,MAO,Y,mi",     "ADT^A04,MAO,N,mi",   "ADT^A08,MAO,U,mi",
                 "ADT^A01,zFOO,Y,nan",   "ADT^A04,zFOO,N,nan", "ADT^A08,zFOO,U,nan"})
    void testAdtA01_NZCR_LanguageFields(String message, String code, String interpreter, String ietfCode) throws IOException {

        String text     = code.equals("ENG") ? "English" : code.equals("MAO") ? "Māori" : code.equals("zFOO") ? "Chinese - Fookien" : "Unknown";
        String ietfText = code.equals("ENG") ? "English" : code.equals("MAO") ? "Māori" : code.equals("zFOO") ? "Min Nan Chinese" : "Unknown";

        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|" +
           
                code + "^" + text + "^NHDD-132^" + interpreter + "|" 
           
           +"U^Unknown^HL70002||||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"
        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"
        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH~027 5 666 325^ORN^CP|^WPN^PH\r"
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||^PRN^PH~021 555 888 6^ORN^CP|^WPN^PH\r";

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList).hasSize(1); // from PID

        // Grab the (only) Patient Resource
        Patient patient = (Patient) patientResourceList.get(0);
        assertThat(patient.hasIdElement()).isFalse();  // We're not generating Id

        // Make sure we've got just the one Communication.Language
        assertThat(patient.getCommunication())
            .hasSize(1)
            .element(0).satisfies( cmn -> {

                assertThat(cmn.getPreferred()).isTrue();

                // Language is a CodeableConcept
                assertThat(cmn.getLanguage()).satisfies( cc -> {

                    // Language has two codings
                    assertThat(cc.getCoding())
                        .hasSize(2)
                        .satisfies( cdgs -> {
                            assertThat(cdgs).filteredOn(cdg -> cdg.getSystem().equals("https://standards.digital.health.nz/ns/central-region/pas-language"))
                                .hasSize(1)
                                .element(0).satisfies( cdg -> {
                                    assertThat(cdg.getCode()).isEqualTo(code);
                                    assertThat(cdg.getDisplay()).isEqualTo(text);
                                });

                            assertThat(cdgs).filteredOn(cdg -> cdg.getSystem().equals("urn:ietf:bcp:47"))
                                .hasSize(1)
                                .element(0).satisfies(cdg -> {
                                    assertThat(cdg.getCode()).isEqualTo(ietfCode);
                                    assertThat(cdg.getDisplay()).isEqualTo(ietfText);
                                });
                        });
                });
            });

        // Make sure we've got the interpreter extension
        List<Extension> extnList = patient.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/patient-interpreterRequired");
        assertThat(extnList)
            .hasSize(1)
            .element(0).satisfies( extn -> {

                assertThat( ((BooleanType) extn.getValue()).booleanValue()).isEqualTo(interpreter.equals("Y"));
            });
    }

    @ParameterizedTest
    // ADT_A01, ADT_A04, ADT_A08, all use the same message structure so we can reuse adt_a01 tests for them.
    //
    @ValueSource(strings = { "ADT^A01", "ADT^A04", "ADT^A08"/* , "ADT^A13" */ })
    void testAdtA01_NZCR_ContactNK1(String message) throws IOException {
               
        String hl7message = "MSH|^~\\&|TestSystem||TestTransformationAgent||20150502090000||" + message + "|controlID|P|2.6\r"
        + "EVN||20220316091358\r"
        + "PID|||ZHY4846^^^&WebPAS||MCH STEPHENS^STEVE^^^FR^^D||19720223000000|M|Stephens^Stephen^Frederick^^^^BAD|21^NZ Maori^NZHIS|100 Broadway Avenue^^Palmerston North^^4410^NEW ZEALAND^C~^^^^^NEW ZEALAND^M|1851|^PRN^PH~02758880032^ORN^CP|^WPN^PH|ENG^English^NHDD-132^U|U^Unknown^HL70002|" + 
               
          "|||||21^NZ Maori^NZHIS~11^NZ European / Pakeha^NZHIS~32^Cook Island Maori^NZHIS||||Y||NZ^New Zealand citizen^RHIP_Resident_Status||N\r"

        + "PD1|||Feilding Health Care^^12482^^^&2.16.840.1.113883.2.18.66.3.3.0|66665^Rerekura^Amber-Lea Aroha^^^Dr^^^&2.16.840.1.113883.2.18.66.3.2.0\r"

        + "NK1|1|MCH BROADHURST^ELAINE^^^MRS|03^Sister^webPAS||^PRN^PH~027 8887743^ORN^CP|^WPN^PH\r"                // second Residential ph instance - MOBILE
        + "NK1|2|STEPHANIE^MCH STEPHENS^^^MRS|01^Mother^webPAS||^PRN^PH|^WPN^PH~06 555 7878^WPN^PH\r"            // second Work instance - PHONE
        + "NK1|3|MCH PHILIPS^LOUISE^^^MISS|18^De Facto^webPAS||06 555 3566^PRN^PH~^ORN^CP|06 555 7766^WPN^PH\r";   // one of each

        List<BundleEntryComponent> e = ResourceUtils.createFHIRBundleFromHL7MessageReturnEntryList(ftv, hl7message);

        // Grab the set of Patient Resources... and check some stuff
        List<Resource> patientResourceList = ResourceUtils.getResourceList(e, ResourceType.Patient);
        assertThat(patientResourceList)
            .hasSize(1)
            .element(0).satisfies(pat-> {

                assertThat(pat.hasIdElement()).isFalse();  // We're not generating Id

                // Make sure we've got three Contacts
                assertThat( ((Patient) pat).getContact())
                    .hasSize(3)
                    .allSatisfy( contact -> {

                        // Make sure there's only one relationship
                        assertThat(contact.getRelationship())
                            .hasSize(1)
                            .allSatisfy(reln -> {

                                assertThat(reln.getCoding())
                                    .hasSize(2);
                           });

                        assertThat(contact.getRelationshipFirstRep().getCoding())
                           .filteredOn(cdg -> cdg.getSystem().equals("https://standards.digital.health.nz/ns/central-region/contact-relationship"))
                           .hasSize(1)
                           .element(0).satisfies(cdg -> {

                            switch(Integer.parseInt(cdg.getCode())) {

                                case 03:
                                    assertThat(cdg.getDisplay()).isEqualTo("Sister");
                                    assertThat(contact.getName().getText()).isEqualTo("MRS ELAINE MCH BROADHURST");
                                    assertThat(contact.getTelecom())
                                        .hasSize(1).allSatisfy(tcm -> {
                                            assertThat(tcm.getSystem()).hasToString("SMS");
                                            assertThat(tcm.getUse()).hasToString("MOBILE");
                                            assertThat(tcm.getValue()).isEqualTo("027 8887743");
                                        });
                                    break;

                                case 01:
                                    assertThat(cdg.getDisplay()).isEqualTo("Mother");
                                    assertThat(contact.getName().getText()).isEqualTo("MRS MCH STEPHENS STEPHANIE");
                                    assertThat(contact.getTelecom())
                                        .hasSize(1).allSatisfy(tcm -> {
                                            assertThat(tcm.getSystem()).hasToString("PHONE");
                                            assertThat(tcm.getUse()).hasToString("WORK");
                                            assertThat(tcm.getValue()).isEqualTo("06 555 7878");
                                        });
                                    break;

                                case 18:
                                    assertThat(cdg.getDisplay()).isEqualTo("De Facto");
                                    assertThat(contact.getName().getText()).isEqualTo("MISS LOUISE MCH PHILIPS");
                                    assertThat(contact.getTelecom())
                                        .hasSize(2).allSatisfy(tcm -> {
                                            assertThat(tcm.getSystem()).hasToString("PHONE");

                                            // check the two phone numbers
                                            if(tcm.getUse().equals(ContactPoint.ContactPointUse.HOME)) {
                                                assertThat(tcm.getValue()).isEqualTo("06 555 3566");
                                            } else if(tcm.getUse().equals(ContactPoint.ContactPointUse.WORK)) {
                                                assertThat(tcm.getValue()).isEqualTo("06 555 7766");
                                            }
                                        });
                                    break;
                            }
                           });
                    });
            });


    }
}

