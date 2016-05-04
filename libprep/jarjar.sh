#!/usr/bin/env bash
java -jar jarjar-1.4.jar process jarjar.rules asm-all-4.1.jar ../lib/asm-all-asm4.jar
java -jar jarjar-1.4.jar process jarjar.rules parboiled-java-1.1.7.jar ../lib/parboiled-java-asm4.jar
java -jar jarjar-1.4.jar process jarjar.rules parboiled-java-1.1.7.jar ../lib/parboiled-java-asm4.jar

