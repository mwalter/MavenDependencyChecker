# Maven Dependency Checker

![Build](https://github.com/mwalter/MavenDependencyChecker/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/18525.svg)](https://plugins.jetbrains.com/plugin/18525)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18525.svg)](https://plugins.jetbrains.com/plugin/18525)

---
<!-- Plugin description -->
Checks if there are any new Maven project dependencies available.

If there are any new releases of the dependencies you use in your project the plugin will display a dialogue containing
all dependencies which can or should be updated in oder to avoid security issues for example. You'll see the version of
the dependencies you use right now and the latest versions available at Maven Central. You can choose to keep the
results by copying them to the clipboard. The plugin does NOT modify your POM file.

Please keep in mind that dependencies which are available at Maven Central will be checked only.

In order to check for updates just select your Maven POM file (pom.xml) in the project explorer view.
Right-click the file and choose <kbd>Check Maven Dependencies</kbd> from the context menu.
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
