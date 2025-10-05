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

# Replic-Read server component
Welcome to the GitHub repository for the server component of the Replic-Read system.

## Contributing
To contribute, create a fork of the GitHub repository, and clone it into your local environment:
```bash
git clone https://github.com/<username>/server
```

You can then make your local changes. After you're done, check if te project compiles and all tests run by executing
```bash
./gradlew build
```
in the project root.
After that, open a pull-request so we can merge your changes.

## Architecture
An in-depth overview about the architecture, interfaces and third-party libraries can be seen in the [design-report](https://github.com/replic-read/design).

### Internal components
The internal component structure can be visualized as follows:
<img src="https://raw.githubusercontent.com/replic-read/server/499e58f2b770dee041433b7c501e7a20cc94eb8c/images/components-server.svg">

This component structure is reflected in the code by the following gradle modules:
- _Domain_ corresponds to `:domain`
- _Interface_ corresponds to `:inter`
- _Infrastructure_ corresponds to `:infrastructure`