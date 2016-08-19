# KeyCloak Extensions: Securing Realm Resources With Custom Roles

This example demonstrates how to secure KeyCloak custom realm resources with custom admin roles (different from built-in roles like `admin`).

## Requirements

* KeyCloak 2.1.0

## Build

`mvn install`

## Installation

After the extension has been built, install it as a JBoss/WildFly module via `jboss-cli`:

```
[disconnected /] module add --name=hello --resources=/path/to/custom-admin-roles-1.0-SNAPSHOT.jar --dependencies=org.keycloak.keycloak-server-spi,org.keycloak.keycloak-services,org.keycloak.keycloak-core,javax.ws.rs.api
```

Alternatively, create `$KEYCLOAK_HOME/modules/hello/main/module.xml` to load extension from the local Maven repo:

```
<?xml version="1.0" ?>

<module xmlns="urn:jboss:module:1.1" name="hello">

    <resources>
        <artifact name="hello:custom-admin-roles:1.0-SNAPSHOT"/>
    </resources>

    <dependencies>
        <module name="org.keycloak.keycloak-server-spi"/>
        <module name="org.keycloak.keycloak-services"/>
        <module name="org.keycloak.keycloak-core"/>
        <module name="javax.ws.rs.api"/>
    </dependencies>

</module>
```

## Configuration

`$KEYCLOAK_HOME/standalone/configuration/keycloak-server.json`:

```
{
    "providers": [
        "classpath:${jboss.home.dir}/providers/*",
        "module:hello"
    ],
...
    "theme": {
        "staticMaxAge": 2592000,
        "cacheTemplates": true,
        "cacheThemes": true,
        "folder": {
          "dir": "${jboss.home.dir}/themes"
        },
        "module": {
          "modules": [ "hello" ]
        },
        "default": "hello"
    },
...
```

## Running example

Run KeyCloak and log into the admin console. You should be able to access the "☺ Hello" menu item.

Check for `view-hello` and `manage-hello` roles in:
* `*-realm` clients of master realm;
* `realm-management` clients of regular realms.

The roles will be automatically added to both existing and newly-created realms.
