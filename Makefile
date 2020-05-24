
VERSION=1.0
GRAALOPTS= -J-Xmx3g -J-Xms3G --no-server -J-client --report-unsupported-elements-at-runtime -H:+AllowIncompleteClasspath -Dgraal.CompilerConfiguration=community --initialize-at-build-time -H:ReflectionConfigurationFiles=reflection_config.json -H:IncludeResourceBundles=org.apache.xml.serializer.utils.SerializerMessages -Djava.util.logging.config.file=~/.config/godwit/logging.properties 
NAME=godwit-$(VERSION)-SNAPSHOT-jar-with-dependencies
JAR=target/$(NAME).jar

ETC=~/.config/godwit


java: compile assemble 

build: java graal install

graal: reflect graal 


compile:
	mvn compile

assemble:
	mvn assembly:single

reflect:
	./bin/graal-helper.py

build-graal:
	native-image $(GRAALOPTS) -jar $(JAR) -H:Name=bin/gwt

run-info:
	./run.sh $(VERSION) info

run-compile:
	./run.sh $(VERSION) compile 


run-graal-help:
	./gwt --help

clean:
	mvn clean

docs:
	mvn javadoc:javadoc

install: $(ETC)
	install bin/gwt* ~/.local/bin
	install -m 644 etc/* ~/.config/godwit

$(ETC):
	install -d $(ETC)
