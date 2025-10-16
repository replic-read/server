# Deployment

## Environment variables

Independent of the deployment method, the server is configured via environment variables. The following table gives an
overview of them. Variables without a default value are required.

**Note:** Only because a variable has a default value does not mean that without setting them, the application runs as
expected. Some functionality might not work.

| Variable                | Description                                                                               | Default value                                  | Example                              |
|-------------------------|-------------------------------------------------------------------------------------------|------------------------------------------------|--------------------------------------|
| RERE_BASE_URL           | The base url of the frontend. Must not end with a '/'.                                    | <Empty string>                                 | https://example.com                  |
| RERE_CONTENT_ROOT       | The directory in which the replic content files should be saved. Must not end with a '/'. | Empty string, i.e. the root of the filesystem. | /home/john/documents/replic_content  |
| RERE_AUTH_REFRESH_EXP   | The lifespan of refresh tokens, in milliseconds.                                          | 2592000000                                     | 2592000000                           |
| RERE_MAIL_EXP           | The lifespan of mail-verification tokens, in milliseconds.                                | 900000                                         | 900000                               |
| RERE_AUTH_ACCESS_EXP    | The lifespan of access tokens, in milliseconds.                                           | 900000                                         | 900000                               |
| RERE_AUTH_ACCESS_SECRET | The secret used to sign the access-tokens.                                                | d2cf0591-a08d-4547-8cec-363daee402b0           | i-am-a-GR/&"BD§)"D§"-secret          |
| RERE_MAIL_USERNAME      | The username to authenticate with on the SMTP server. Usually the email address.          | user@example.com                               | user@example.com                     |
| RERE_MAIL_PASSWORD      | The password to authenticate with on the SMTP server.                                     | examplepassword                                | examplepassword                      |
| RERE_MAIL_HOST          | The SMTP host.                                                                            | smtp.example.com                               | smtp.example.com                     |
| RERE_MAIL_PORT          | The SMTP port.                                                                            | 465                                            | 465                                  |
| RERE_MAIL_SSL           | Whether to use SSL encryption for communication with the SMTP server.                     | true                                           | true                                 |
| RERE_ADMIN_USERNAME     | Username of the admin account.                                                            | root                                           | root                                 |
| RERE_ADMIN_EMAIL        | Email of the admin account.                                                               | root@example.com                               | root@example.com                     |
| RERE_ADMIN_PASSWORD     | Password of the admin account.                                                            | root                                           | root                                 |
| RERE_DB_USERNAME        | Username for authenticating on the database.                                              |                                                | root                                 |
| RERE_DB_PASSWORD        | Password for authenticating on the database.                                              |                                                | root                                 |
| RERE_DB_URL             | The JDBC-URL to the database.                                                             |                                                | jdbc/postgres://database/replic_read |
| RERE_SPRING_PROFILES    | A comma-separated list of spring profiles to run                                          | default                                        | dev,load-test                        |

## Containerized

Each release comes with a container image that can be found on
the [packages page](https://github.com/replic-read/server/pkgs/container/server).

The following is a working example of a setup in a `docker-compose.yml` file:

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

  server:
    image: ghcr.io/replic-read/server:0.0.3
    ports:
      - "8080:8080"
    depends_on:
      - database

    environment:
      RERE_DB_URL: jdbc:postgresql://database/replic_read
      RERE_DB_USERNAME: admin
      RERE_DB_PASSWORD: admin
```

## In a JVM

Each release comes with a `.jar` file that can be found on the same release packe.

It can be executed like any other jar file, using

```bash

java -jar app.jar
```

Don't forget to provide the environment variables.