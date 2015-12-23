ARCH=$(arch)
RPI_OS=hypriot/rpi-java
RPI_DOCKER_IMAGE_NAME=makefilerpi-kura

ifneq (`arch`,arm)
	RPI_OS=debian
endif

all : searchreplace
	@echo "Docker building ..."
	docker build -t ${RPI_DOCKER_IMAGE_NAME} .

searchreplace : Dockerfile
	@echo "Create a specific Dockerfile depending on "`arch`
	sed -i "" -e "s/FROM.*/FROM ${RPI_OS}/" "Dockerfile"

run : all
	docker run -d -p 80:80 -t ${RPI_DOCKER_IMAGE_NAME}
