# 変数
JAVA := java
JAVAC := javac
JAR := jar

SRCS := $(wildcard *.java */*.java)
JAVAFX_MODULES := javafx.controls,javafx.base,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web

OPTS := -p $(JAVAFX_PATH)/lib --add-modules $(JAVAFX_MODULES)
JAVA_OPTS := $(OPTS) -classpath bin
JAVAC_OPTS := $(OPTS) -sourcepath src -d bin

# コマンド
run: Main.class
	cp -r src/fxml bin
	$(JAVA) $(JAVA_OPTS) Main

Main.class: $(SRCS)
	$(JAVAC) $(JAVAC_OPTS) src/Main.java

clean:
	rm -rf bin dist **/*.args

clean-hard:
	make clean
	rm -rf .*.*.un* .*.un* **/.*.*.un* **/.*.un* **/**/.*.*.un* **/**/.*.un*

.PHONY: dist-darwin, dist-win, dist-linux
