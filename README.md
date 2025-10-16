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

## Deployment

For further instructions on deployment, refer to the [deployment guide](DEPLOYMENT.md).

## Development setup

### Database

When developing, it is helpful to have a local database running. This can be achieved by creating a `docker-compose.yml`
file:

```yaml
services:
  database:
    image: postgres:18
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
      POSTGRES_DB: replic_read
```

### Building

To build and test the project, run
```bash
./gradlew build
```

### Starting the server

To start the server, use the following command:

```bash
./gradlew bootRun
```
in the project root.

Remember that also for development setups you need to provide some environment variables. For more infos, refer to the [deployment guide](DEPLOYMENT.md).

Most IDE's can help you run spring projects, as well as setting up environment variables.

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
- `dev`: The development profile. It is mostly used to configure specific settings for developing like logging, but also lessens some security setup, e.g. allows CORS access from any origin..
- `load-test`: The profile that is used for load testing. It provides additional authentication options that make the
  tests easier to construct.

## API-Documentation

An [OpenAPI](https://www.openapis.org/) specification is automatically generated and deployed when the server is run.
Following endpoints are important:

- `/swagger-ui/index.html/`: The human-readable swagger ui for manual testing and as a development reference.
- `/v3/api-docs`: The JSON-formatted documentation to be used by clients.

## Architecture
An in-depth overview about the architecture, interfaces and third-party libraries can be seen in the [design-report](https://github.com/replic-read/design).

### Internal components
The internal component structure can be visualized as follows:

<img src="https://raw.githubusercontent.com/replic-read/server/499e58f2b770dee041433b7c501e7a20cc94eb8c/images/components-server.svg" alt="Component and interface diagram">

This component structure is reflected in the code by the following gradle modules:
- _Domain_ corresponds to `:domain`
- _Interface_ corresponds to `:inter`
- _Infrastructure_ corresponds to `:infrastructure`

## Roadmap

While the application is in the state that was anticipated from the beginning, following possible future enhancements
have been identified:

- **Extension of the load-tests**, see the [issue](https://github.com/replic-read/server/issues/25) for further
  information.