# shishamo

[![CircleCI](https://circleci.com/gh/yo1000/shishamo.svg?style=svg)](https://circleci.com/gh/yo1000/shishamo)
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.yo1000:shishamo)](https://sonarqube.com/dashboard?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=ncloc)](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=ncloc)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=bugs)](https://sonarcloud.io/component_measures/domain/Reliability?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=vulnerabilities)](https://sonarcloud.io/component_measures/domain/Security?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=code_smells)](https://sonarcloud.io/component_measures/domain/Maintainability?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=duplicated_lines_density)](https://sonarcloud.io/component_measures/metric/duplicated_lines_density/list?id=com.yo1000:shishamo)

shishamo is a [MySQL](https://www.mysql.com/) metadata visualizer.

"shishamo" means capelins in Japanese, which is dolphin's favorite.
Sakila definitely likes it too.

## Quick Start

```console
$ git clone https://github.com/yo1000/shishamo.git
$ cd shishamo/
$ ./mvnw clean install
$ ./mvnw spring-boot:run                    \
    -Dspring.datasource.name=<schema>       \
    -Dspring.datasource.username=<username> \
    -Dspring.datasource.password=<password> \
    -Dspring.datasource.url=jdbc:mysql://<host>:<port>/<database>   \
    -pl shishamo-web
```

URL:
http://localhost:8080/

### Demo with embedded MySQL

```console
$ ./mvnw clean install
$ ./mvnw spring-boot:run            \
    -Dspring.profiles.active=demo   \
    -Pembedded                      \
    -pl shishamo-web
```

URL:
http://localhost:8080/


## Embedded MySQL

You can use embedded MySQL server for demo, testing, and development.

Example:

```console
$ ./mvnw clean install
$ ./mvnw spring-boot:run            \
    -Dspring.profiles.active=demo   \
    -Pembedded                      \
    -pl shishamo-web
```


```console
$ ./mvnw clean install
$ ./mvnw package    \
    -Pembedded      \
    -pl shishamo-web
$ java -jar shishamo-web/target/shishamo.jar    \
    --spring.profiles.active=demo
```

Also you can change the configuration in `src/main/resources/embedded-mysql.yml`  

Notice:
You can override the default configuration by providing arguments on the command line.
