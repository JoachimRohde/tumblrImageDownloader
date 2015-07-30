tumblrImageDownloader
=====================

Intro
------------
This little program downloads all the images of tumblr page. 

Since I needed this only once for myself the code is quite hacky and dirty. But 
since it done it's job, I guess it might be also useful to someone else.


How to use it
-------------

java -jar tumblrImageDownloader [sitename...] [options...]

You can specify more than one site. Either you provide a full URL like e.g.
http://spongebob-closeups.tumblr.com/ or just the name of the tumblr site (spongebob-closeups).

Available options are:
 -p[path]        Specify download directory
 -ds             don't stop downloading a site after the first image was found which has been already downloaded 
 -u              Check for updates. This option will search for subdirectories in the destination directory and use the subdirectory-name as the name of the tumblr-page.

If you are not providing any download directory, images will be saved in: [home-directory]/tumblrImages/
For each specified sitename a subdirectory is created into which the images are downloaded.

What's missing / Known quirks
-----------------------------
- The program does not distinguish between different dimensions of an image, if several are provided, rather downloads them all.
- You cannot pass a parameter to start with page x
- No filtering (for e.g. filesize, dimension, format, etc.)
- The code is a mess. Really. Every time an exception occurred I just surrounded the line with a try-catch. 
  The "parsing" of the HTML... well... it's messy. Do not expect this program to work if tumblr decides to change anything in their HTML.
- Way too much debugging output.


Download
------------
A precompiled version can be downloaded here: http://www.joachimrohde.com/downloads/tumblrImageDownloader-0.1.jar

Licence
-------
   Copyright 2014 Joachim F. Rohde

The MIT License (MIT)

Copyright (c) 2014 Joachim F. Rohde

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.





