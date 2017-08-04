# Publish over Dropbox plugin for Jenkins

Based on publish-to-ftp and extending publish-to and basic-credentials this Jenkins plugin publishes artifacts in a post-build to dropbox folders without the need to run a sync client on your build server.

# Installation

There are 3 options:

1. Install the plugin through the Jenkins plugin manager. (Version 1.0.5 is available at this time)
2. ~~Install a beta~~
3. Compile your own
  1. Create an own Dropbox app in the on https://developer.dropbox.com/
  2. Clone the sources and update the Config.java with your personal client id and client secret.
  3. Run the "mvn hpi:hpi" on the project source code
  4. Install the generated publish-over-dropbox.hpi on the advanced section of the plugin manager of your Jenkins installation.
  
# Configuration

The complete documentatin of the published plugin is hosted at:
https://wiki.jenkins.io/display/JENKINS/Publish+over+Dropbox+Plugin

# Credits

This project builds upon the [publish-over-plugin](https://github.com/jenkinsci/publish-over-plugin) by Anthony Robinson

Also this project builds upon the [credentials-plugin](https://github.com/jenkinsci/credentials-plugin) by CloudBees, Inc., Stephen Connolly

And much inspiration was found in the [publish-over-ftp-plugin](https://github.com/jenkinsci/publish-over-ftp-plugin) by Anthony Robinson

Uses a copy of the [RuntimeTypeAdapterFactory](https://github.com/google/gson/blob/master/extras/src/main/java/com/google/gson/typeadapters/RuntimeTypeAdapterFactory.java) by Google Inc. licensed under the Apache V2 License.

# Licence

The MIT License (MIT)

Copyright (c) 2015 René de Groot and other contributors.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
