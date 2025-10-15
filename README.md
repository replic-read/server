<p style="text-align:center;">
    <picture>
        <source 
            media="(prefers-color-scheme: dark)" 
            srcset="https://raw.githubusercontent.com/replic-read/server/a3aff8bbcc678035cd68390b416ceb702b5d3f59/images/Logo-dark.svg"
        >
        <source 
            media="(prefers-color-scheme: light)" 
            srcset="https://raw.githubusercontent.com/replic-read/server/a3aff8bbcc678035cd68390b416ceb702b5d3f59/images/Logo-light.svg"
        >
        <img 
            src="https://raw.githubusercontent.com/replic-read/server/a3aff8bbcc678035cd68390b416ceb702b5d3f59/images/Logo-light.svg"
            alt="Replic-Read Logo" 
            width="70%" 
        />
    </picture>
    <br/>
    <br/> 
</p>

[![Quality Gate Status](https://sonar.bumiller.me/api/project_badges/measure?project=server&metric=alert_status&token=sqb_85f2dbd8a01b37bb3d98220a34b0ea7cafcbf2e6)](https://sonar.bumiller.me/dashboard?id=server)
[![Coverage](https://sonar.bumiller.me/api/project_badges/measure?project=server&metric=coverage&token=sqb_85f2dbd8a01b37bb3d98220a34b0ea7cafcbf2e6)](https://sonar.bumiller.me/dashboard?id=server)
[![Duplicated Lines (%)](https://sonar.bumiller.me/api/project_badges/measure?project=server&metric=duplicated_lines_density&token=sqb_85f2dbd8a01b37bb3d98220a34b0ea7cafcbf2e6)](https://sonar.bumiller.me/dashboard?id=server)
[![Lines of Code](https://sonar.bumiller.me/api/project_badges/measure?project=server&metric=ncloc&token=sqb_85f2dbd8a01b37bb3d98220a34b0ea7cafcbf2e6)](https://sonar.bumiller.me/dashboard?id=server)
<br>
![Scala](https://img.shields.io/badge/scala-%23DC322F.svg?style=for-the-badge&logo=scala&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

# Replic-Read server component
Welcome to the GitHub repository for the server component of the Replic-Read system.

## Development setup

### Database

The [Docker-compose file](docker-compose.yml) in the root of the project is preconfigured with a postgres database.
Ensure that the `spring.datasource.*` values `username`, `password` and `url` match the values you provided in the
compose file.

You can start the database by running

```bash
docker compose up -d
```

in the project root directory.

### Email

You also need to adjust the `rere.mail.*` values. Most important are:

- `username`: The username for authentication with the SMTP server. This usually is the email address.
- `password`: The password for the username.
- `smtpHost`: The host domain of the SMTP server.

Note: using `*@gmail.com` addresses need special setup.
Refer to
this [Medium article](https://medium.com/tuanhdotnet/tips-for-sending-mail-from-a-spring-boot-application-using-google-as-mail-server-fcf5ab042594)
for help.

### Building

To build and test the project, run
```bash
./gradlew build
```

### Starting the server

For the values provided above to work, the `dev` profile needs to be activated.
For this, run

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```
in the project root.

Most IDE's offer simpler method to run spring applications.

### Load tests

The `load-test/` directory contains a [Gatling](https://gatling.io/) project where common simulations and scenarios are
set up.

The project is managed by [SBT](https://www.scala-sbt.org/), a lightweight build-tool for scala.

To run the load tests, execute

```bash
sbt Gatling/test -DbaseUrl=<base-url>
```

in `load-test/` where <base-url> is the base url of the server that should be tested.

## Profiles

The server has three spring profiles:

- `default`: The default spring profile. This is used for production.
- `dev`: The development profile. It is mostly used to load custom development configuration using
  the [application-dev.yml](src/main/resources/application-dev.yml).
- `load-test`: The profile that is used for load testing. It provides additional authentication options that make the
  tests easier to construct.

## Architecture
An in-depth overview about the architecture, interfaces and third-party libraries can be seen in the [design-report](https://github.com/replic-read/design).

### Internal components
The internal component structure can be visualized as follows:

<img src="https://raw.githubusercontent.com/replic-read/server/499e58f2b770dee041433b7c501e7a20cc94eb8c/images/components-server.svg" alt="Component and interface diagram">

This component structure is reflected in the code by the following gradle modules:
- _Domain_ corresponds to `:domain`
- _Interface_ corresponds to `:inter`
- _Infrastructure_ corresponds to `:infrastructure`