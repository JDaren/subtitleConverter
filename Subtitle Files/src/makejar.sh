#!/bin/sh

javac Convert.java
jar cvfm subtitleConvert.jar manifest.txt Convert.class IOClass.class subtitleFile/*class
