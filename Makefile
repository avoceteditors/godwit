
VERSION=1.0
GRAALOPTS= -J-Xmx3g -J-Xms3G --no-server -J-client --report-unsupported-elements-at-runtime -H:+AllowIncompleteClasspath
NAME=godwit-$(VERSION)-SNAPSHOT-jar-with-dependencies
JAR=target/$(NAME).jar


build: compile assemble run-info

compile:
	mvn compile

assemble:
	mvn assembly:single

graal:
	native-image $(GRAALOPTS) -jar $(JAR) -H:Name=gwt

run-info:
	./run.sh $(VERSION) info

run-graal-help:
	./gwt --help

clean:
	mvn clean
