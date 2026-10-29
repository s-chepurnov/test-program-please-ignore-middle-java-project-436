FRONTEND_DIST = node_modules/@hexlet/java-online-store-frontend/dist
JAR = build/libs/app.jar
PORT ?= 8080

.PHONY: build contract all

install:
	./gradlew --console=plain -q classes
	npm ci

build:
	rm -rf public/assets public/images public/index.html
	mkdir -p public
	cp -R $(FRONTEND_DIST)/. public/
	./gradlew bootJar

start:
	java -jar $(JAR)

clean:
	./gradlew clean

contract:
	npx tsp compile contract

test-api:
	./gradlew --console=plain test

lint:
	./gradlew --console=plain -q spotlessCheck

lint-fix:
	./gradlew --console=plain -q spotlessApply

all: clean install build start
