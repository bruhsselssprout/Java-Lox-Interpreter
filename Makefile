JAVAC := javac
JAVA := java
MAIN := com.craftinginterpreters.lox.Lox
SOURCES := $(wildcard com/craftinginterpreters/lox/*.java)
FILE := block_comments.lox

compile:
	$(JAVAC) $(SOURCES)

run: compile
	$(JAVA) $(MAIN)

test: compile
	$(JAVA) $(MAIN) $(FILE)

clean:
	find . -name '*.class' -delete
