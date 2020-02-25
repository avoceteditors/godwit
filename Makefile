
VERSION=1.0
GRAALOPTS= -J-Xmx3g -J-Xms3G --no-server -J-client --report-unsupported-elements-at-runtime --verbose -H:+AllowIncompleteClasspath
NAME=godwit-$(VERSION)-SNAPSHOT-jar-with-dependencies
JAR=target/$(NAME).jar


build:
	mvn package

graal:
	native-image $(GRAALOPTS) -jar $(JAR) -H:Name=gwt

run-help:
	java -jar $(JAR) --help

run-graal-help:
	./gwt --help

clean:
	rm $(NAME)
