# Maven Dependency Checker

![Build](https://github.com/mwalter/MavenDependencyChecker/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/18525.svg)](https://plugins.jetbrains.com/plugin/18525)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18525.svg)](https://plugins.jetbrains.com/plugin/18525)

---

## Description

<!-- Plugin description -->
Checks if there are any new Maven project dependencies or build plugins available.

A notification dialogue will show you which dependencies or plugins used in the project can and should be updated in
order to avoid security issues for example.

You'll see the version of the dependencies you use right now and the latest versions available at [Maven Central][central].
You can choose to keep the comparison results by copying the information to the system clipboard or open a generated
POM file with updated version information in an editor tab.

The plugin does NOT modify your POM file.

In order to check for updates just select your Maven POM file (pom.xml) in the project explorer view.
Right-click the file and choose <kbd>Check Maven Dependencies</kbd> from the context menu. You can right-click 
somewhere in the text editor view as well if the POM file is opened in the editor.

If for some reason you do want the plugin to check for major version updates of your dependencies you can diasable
the check in the plugin's settings. Minor and patch version updates are checked nevertheless.

Please keep in mind that Maven dependencies and plugins which are available at [Maven Central][central] will be checked only.
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "MavenDependencyChecker"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/mwalter/MavenDependencyChecker/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Development

The plugin is written in Java using Jetbrains' plugin template and framework. The code is hosted on [GitHub][github].
Builds are performed with the Jetbrains Gradle plugin and automated by [GitHub Actions][actions].

## Bug Reporting and Feature Requests

If you encouter a bug please create an issue in the plugin's [issue tracker][tracker]. Feel free to request new
features.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[github]: https://github.com/mwalter/MavenDependencyChecker
[actions]: https://github.com/actions
[tracker]: https://github.com/mwalter/MavenDependencyChecker/issues
[central]: https://central.sonatype.com/