Subtitle Converter
==================

Original README
---------------
This is a java framework for processing subtitle formats and converting them into other formats.

So far the formats supported are EBU's STL, .SCC, .ASS/.SSA .SRT and a subset of W3C's TTML 1.0

The whole project is under licensed under MIT so it can be used freely. Do not hesitate to contact me if this project or I can be of assistance.

You can find the logic working online at: subtitleconverter.net

This work was done as part of my bachelor's thesis for the Universidad Carlos III de Madrid.

Copyright (c) 2012 J. David REQUEJO
j[dot]david[dot]requejo[at] gmail

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Addtional Notes
---------------
Modified by Ethan Hart

To run conversion, first, you must compile Convert.java by doing the following:

    $ cd Subtitle\ Files/src/
    $ javac Convert.java

Once you have Convert.class, you can run the following:

    $ java Convert input-file input-format output-format output-file

Note: Some IO changes have been made for Linux support (filepaths) and are
not guaranteed to work on Windows.

To make a java archive (jar) file, do the following:

    $ cd Subtitle\ Files/src/
    $ ./makejar.sh

This will create a jar which can be run from anywhere. It uses the same the
argument structure as the Convert.class.
