# Maven Dependency Checker

![Build](https://github.com/mwalter/MavenDependencyChecker/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

---
<!-- Plugin description -->
## Description
This plugin for the IntelliJ IDEA IDE will check your Maven project dependencies for available updates.</br>
If there are any new releases of the dependencies you use in your project it will display a dialog containing all dependencies
which can or should be updated. You'll see the version of the dependencies you use and the latest version available at Maven Central.

## Usage
Select the Maven POM file (pom.xml) in the project explorer view of IntelliJ IDEA. Right-click the file and choose
"Check for Maven Dependency Updates".
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "MavenDependencyChecker"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/mwalter/MavenDependencyChecker/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
