<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Maven Dependency Checker Changelog

## [Unreleased]

## [1.9.0] - 2023-11-04

### Added
- Added the option to access the plugin via the editor context menu

### Fixed
- Fixed deprecation of activity
- Upgraded Gradle libraries
- Support for IntelliJ 2023.3

## [1.8.1] - 2023-08-08

### Fixed
- Support for IntelliJ 2023.2

## [1.8.0] - 2023-06-25

### Added
- Added a startup notification

### Changed
- Improved code to parse Maven dependencies
- Updated IntelliJ platform plugin template

## [1.7.0] - 2023-06-10

### Added
- Added check of Maven plugins declared in POM file.

### Fixed
- Updated multiple libraries

## [1.6.4] - 2023-03-21

### Fixed
- Support for IntelliJ 2023.1

## [1.6.3] - 2022-12-01

### Fixed
- Support for IntelliJ 2022.3

## [1.6.2] - 2022-09-10

### Fixed
- Parse groupId and artifactId from properties section as well if placeholders are used

## [1.6.1] - 2022-08-16

### Fixed
- Support for IntelliJ 2022.2

## [1.6.0] - 2022-06-01

### Changed
- Improved search query for Maven Central

## [1.5.0] - 2022-05-26

### Added
- Parse versions from properties section if placeholders are used

## [1.4.0] - 2022-05-11

### Added
- Dependencies in dependency management section are checked as well

## [1.3.1] - 2022-04-14

### Fixed
- Minor bug leading to exception

## [1.3.0] - 2022-02-26

### Changed
- Improved version comparison.

## [1.2.0] - 2022-02-08

### Changed
- Improved performance of the plugin.
- Increased result dialog window size.

## [1.1.0] - 2022-02-06

### Added
- Option to copy the results to the system clipboard.
- Plugin icon next to the context menu entry.

### Changed
- Shortened the text of the context menu entry to execute the plugin.
- Improved the plugin description.

## [1.0.1] - 2022-02-02

### Fixed
- IDE error occurring when updating the state of the plugin.

## [1.0.0] - 2022-01-31

### Added
- Initial release.