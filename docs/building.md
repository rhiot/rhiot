# Building Rhiot

All you need to build the project is Maven 3 and Java 8 JDK

    git clone https://github.com/rhiot/rhiot.git
    cd rhiot
    mvn install

## Building project using Docker

If you don't want to install Java and Maven on your machine, you can use our Docker building image.

### 1st step (once)

    git clone https://github.com/rhiot/rhiot.git
    cd rhiot
    docker build -t rhiot build


### 2nd step

    cd rhiot
    docker run -v `pwd`:/rhiot --privileged -i -t rhiot

## Releasing the project

If you are interested in cutting a release of the project follow steps described below:

### Before you start

You have to had a DockerHub account setting configured in your `~/.m2/settings.xml`:
    
    <server>
      <username>rhiot</username>
      <password>secret</password>
      <id>registry.hub.docker.com</id>
    </server>

### Cutting the release

Execute the following command in the project main directory:

    mvn release:prepare release:perform

### After the release

* update the version on the main page of the project (`readme.md`)
* update release guide (`docs/release-notes.md`) using GitHub tickets marked as done in the given version