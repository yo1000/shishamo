# shishamo
[![Build Status](https://travis-ci.org/yo1000/shishamo.svg?branch=kotlin)](https://travis-ci.org/yo1000/shishamo)
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.yo1000:shishamo)](https://sonarqube.com/dashboard?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=ncloc)](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=ncloc)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=bugs)](https://sonarcloud.io/component_measures/domain/Reliability?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=vulnerabilities)](https://sonarcloud.io/component_measures/domain/Security?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=code_smells)](https://sonarcloud.io/component_measures/domain/Maintainability?id=com.yo1000:shishamo)
[![Measure Coverage](https://sonarqube.com/api/badges/measure?key=com.yo1000:shishamo&metric=duplicated_lines_density)](https://sonarcloud.io/component_measures/metric/duplicated_lines_density/list?id=com.yo1000:shishamo)

shishamo is [MySQL](https://www.mysql.com/) metadata visualizer.

"shishamo" means capelin in Japanese, which is known as dolphin's bait.  
Sakila will definitely like it too.

## Quick Start

```
git clone git@github.com:su-kun1899/shishamo.git
cd shishamo/
./mvnw spring-boot:run \
    -Dspring.datasource.url=jdbc:mysql://<Your mysql host: localhost>>:<Your mysql port: 3306>/<Your mysql schema> \
    -Dspring.datasource.schema=<Your mysql schema> \
    -Dspring.datasource.username=<Your mysql user> \
    -Dspring.datasource.password=<Your mysql password>
```

URL:
http://localhost:8080/

### Demo with embedded MySql

```
./mvnw spring-boot:run -Dshishamo.embedded.mysql=true
```

URL:
http://localhost:8080/


## Embedded MySql

You can use embedded mysql server for demo, testing, development.

Example:  
- `java -jar -Dshishamo.embedded.mysql=true shishamo.jar`
- `./mvnw spring-boot:run -Dshishamo.embedded.mysql=true`  

Also you can custormize the configuration by `src/main/resources/embedded-mysql.yml`  

Notice:
Command line argument has more priority than configuration file.
