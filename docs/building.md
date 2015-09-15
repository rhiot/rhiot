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