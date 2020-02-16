
VERSION=0.1.0
GRAALOPTS= -J-Xmx3g -J-Xms3G --no-server -J-client --report-unsupported-elements-at-runtime --verbose
NAME=godwit-$(VERSION)-SNAPSHOT-standalone
JAR=target/$(NAME).jar


build:
	lein compile
	lein uberjar

graal:
	native-image $(GRAALOPTS) -jar $(JAR) -H:Name=gwt
	#mv $(NAME) gwt

run-help:
	java -jar $(JAR) --help

run-graal-help:
	./gwt --help

clean:
	rm $(NAME)
