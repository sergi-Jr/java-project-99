setup:
	npm install
	./gradlew wrapper --gradle-version 8.7
	./gradlew build

clean:
	./gradlew clean

build:
	./gradlew clean build

reload-classes:
	./gradlew -t classes

install:
	./gradlew installDist

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

update-js-deps:
	npx ncu -u

check-java-deps:
	./gradlew dependencyUpdates -Drevision=release

.PHONY: build