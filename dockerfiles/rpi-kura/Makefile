KURA_VERSION=1.3.0
RPI_VERSION=raspberry-pi
DOCKER_IMAGE_NAME=rhiot/kura-$(RPI_VERSION)
DOCKER_IMAGE_TAGNAME=$(DOCKER_IMAGE_NAME):$(KURA_VERSION)

RPI_VERSION_LIST=raspberry-pi raspberry-pi-nn raspberry-pi-bplus raspberry-pi-bplus-nn raspberry-pi-2 raspberry-pi-2-nn

default: build

build:
	docker build -t $(DOCKER_IMAGE_TAGNAME) --build-arg  KURA_VERSION=${KURA_VERSION} --build-arg RPI_VERSION=${RPI_VERSION} .
	docker tag -f $(DOCKER_IMAGE_TAGNAME) $(DOCKER_IMAGE_NAME):latest

push:
	#docker push $(DOCKER_IMAGE_NAME)

buildall:
	for version in ${RPI_VERSION_LIST} ; do \
		make build RPI_VERSION=$$version ; \
  done

pushall: buildall
	for version in ${RPI_VERSION_LIST} ; do \
		make push RPI_VERSION=$$version ; \
  done
