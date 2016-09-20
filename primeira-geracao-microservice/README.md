# primeiraGeracaoMicroservice

This application was generated using JHipster, you can find documentation and help at [https://jhipster.github.io](https://jhipster.github.io).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:


## Building for production

To optimize the primeiraGeracaoMicroservice client for production, run:

    ./mvnw -Pprod clean package

To ensure everything worked, run:

    java -jar target/*.war

Performance tests are run by [Gatling]() and written in Scala. They're located in `src/test/gatling` and can be run with:

    ./mvnw gatling:execute

    
## Continuous Integration

To setup this project in Jenkins, use the following configuration:

* Project name: `primeiraGeracaoMicroservice`
* Source Code Management
    * Git Repository: `git@github.com:xxxx/primeiraGeracaoMicroservice.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pprod clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

[JHipster]: https://jhipster.github.io/
[Gatling]: http://gatling.io/
