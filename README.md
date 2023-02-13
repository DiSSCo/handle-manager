# DiSSCo Handle Manager

API that enforces DiSSCo requirements for PID kernel records.

Four Types of PID records can be created: Handle records, DOI records, Digital Specimen records, and
Botany Specimen records. Each subsequent record's attributes builds on the previous record Type,
i.e. a Digital Specimen Record contains all attributes in a DOI record, which contains all
attributes in a Handle record. More information regarding each Type's attributes can be found in the
DiSSCo Data Model documentation (which is a living specification).

## Interfacing with the Handle System

The API performs read/write/update operations on the Handle System via a PostgreSQL database. The
database is configured within DiSSCo's Local Handle Server to automatically syncrhonize with the
Handle System. For more information on the Handle System,
see [CNRI's Handle Technical Manual](http://www.handle.net/tech_manual/HN_Tech_Manual_9.pdf).

## Handle Names

The prefix assigned to DiSSCo is `20.5000.1025/`. Suffixes minted are 9 characters long, with dashes
every 3 characters, [A-Z;0-9] excluding I (capital i) and O (capital o).

Example: `20.5000.1025/E3W-9A2-413`.

## Documentation

The API is documented with a Swagger endpoint.

## Connection Properties

`spring.datasource.url` - JDBC URL of the database

`spring.datasource.username` - Username of the database user with sufficient rights

`spring.datasource.password` - Password for the User

## Keycloak Properties

`keycloak.auth-server-url`

`keycloak.realm`

`keycloak.resource`

`keycloak.ssl-required`

`keycloak.use-resource-role-mappings`

`keycloak.principal-attribute`

`keycloak.confidential-port`

`keycloak.always-refresh-token`

`keycloak.bearer-only`