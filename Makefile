
VERSION=1.0
GRAALOPTS= -J-Xmx3g -J-Xms3G --no-server -J-client --report-unsupported-elements-at-runtime -H:+AllowIncompleteClasspath -Djava.util.logging.config.file=resources/logging.properties
NAME=godwit-$(VERSION)-SNAPSHOT-jar-with-dependencies
JAR=target/$(NAME).jar


build: compile assemble graal

compile:
	mvn compile

assemble:
	mvn assembly:single

graal:
	native-image $(GRAALOPTS) -jar $(JAR) -H:Name=gwt

run-help:
	java -jar $(JAR) --help

run-graal-help:
	./gwt --help

clean:
	mvn clean
