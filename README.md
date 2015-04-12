# Publish over Dropbox plugin for Jenkins

Based on publish-to-ftp and extending publish-to and basic-credentials this Jenkins plugin publishes artifacts in a post-build to dropbox folders without running a syncing client on your build server.

# Installation

There are 3 options:

1. Run the maven hpi:run on the project source code
2. Install a pre-build publish-over-dropbox.hpi in the advanced section of the plugin manager of your Jenkins installation. The latest build is available at: https://github.com/rcgroot/jenkins-publish-over-dropbox-plugin/releases/tag/1.0-rc1
3. Install the plugin through the plugin manager. (Not available at this time)

# Configuration

To publish artifacts to a Dropbox location there are three levels of configuration. Each level is dependent on the previous. 

Tip: Consult the help at each input field.

### Create account credentials

The highest level is connecting a Dropbox account to Jenkins. Dropbox account can be created in the **Jenkins > Credentials**.

<img src="resources/documentation/01-credentials.png"/>

### Create locations

The second level is to create locations for an account. Locations can be created in **Jenkins > Manage Jenkins > Configure System**.

<img src="resources/documentation/02-location.png"/>

### Create job post build steps

The last level is to actual publish file to a Dropbox location. Publishing can be done as **Post-build Actions** in your project configuration.

<img src="resources/documentation/03-postbuild.png"/>
