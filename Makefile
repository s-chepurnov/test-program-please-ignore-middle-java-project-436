JAR = build/libs/app.jar
PORT ?= 8080

.PHONY: build

install:
	./gradlew classes

build:
	./gradlew bootJar

start:
	java -jar $(JAR)
