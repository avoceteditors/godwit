
VERSION=1.0
GRAALOPTS= -J-Xmx3g -J-Xms3G --no-server -J-client --report-unsupported-elements-at-runtime -H:+AllowIncompleteClasspath -Dgraal.CompilerConfiguration=community
NAME=godwit-$(VERSION)-SNAPSHOT-jar-with-dependencies
JAR=target/$(NAME).jar


build: compile assemble 

compile:
	mvn compile

assemble:
	mvn assembly:single

graal:
	native-image $(GRAALOPTS) -jar $(JAR) -H:Name=gwt

run-info:
	./run.sh $(VERSION) info

run-compile:
	./run.sh $(VERSION) compile 


run-graal-help:
	./gwt --help

clean:
	mvn clean
