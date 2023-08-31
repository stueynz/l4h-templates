# l4h-templates

A small repo containing custom templates (and an extra Utils Java Class) for Central Region's
implementation of [LinuxForHealth HL7 v2 to FHIR Converter](https://github.com/LinuxForHealth/hl7v2-fhir-converter)

## MidCentral Customisations:

* ADT messages don't include IN1 or IN2 Insurance segments
* ADT messages don't include OBX Observation segments
* ADT messages don't include AL1 Allergy segments
* Resultant Patient Resource conforms to [Central Region's Integration Hub](http://build.fhir.org/ig/tewhatuora/centralRegion-integrationHub-ig/)  FHIR Implementation Guide.
  * extension fields from [NZ Base](http://build.fhir.org/ig/HL7NZ/nzbase/branches/master/index.html) FHIR Implementation Guide
    * [nzCitizenship](http://build.fhir.org/ig/HL7NZ/nzbase/branches/master/StructureDefinition-nz-citizenship.html)
    * [nzResidency](http://build.fhir.org/ig/HL7NZ/nzbase/branches/master/StructureDefinition-nz-residency.html)
    * [ethnicity](http://build.fhir.org/ig/HL7NZ/nzbase/branches/master/StructureDefinition-nz-ethnicity.html) (with both level-2 and level-4 codings)
    * [domicile code](http://build.fhir.org/ig/HL7NZ/nzbase/branches/master/StructureDefinition-domicile-code.html)
    * [suburb](http://build.fhir.org/ig/HL7NZ/nzbase/branches/master/StructureDefinition-suburb.html) in Addresses
    * Patient Religion
  * WebPAS weird partially filled in XTN fields are handled, eg:  `|^PRN^PH~027 8887743^ORN^CP|^WPN^PH`  becomes a single `telecom` entry with `use=home` and `system=mobile`
  * NK1 segments become `Patient.Contact` entries
  * Where possible, raw WebPAS codes are included alongside recognised FHIR codes.  eg:  `|"ENG^ENGLISH^NHDD-132^Y|"` becomes a language with two codings:
    ```json
       "language": {
          "coding": [ {
            "system": "https://standards.digital.health.nz/ns/central-region/raw-language",
            "code": "ENG",
            "display": "English"
          }, {
            "system": "urn:ietf:bcp:47",
            "code": "en",
            "display": "English"
          } ]
    ```
