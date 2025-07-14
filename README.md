# Grafikon

Timetables for model railway. Useful for meets with modules (like FREMO, Free-mo etc...).

## Examples

Examples of gtm files with timetables can be found here: [gtm_examples.zip](https://jub.parostroj.net/grafikon/gtm_examples.zip)

## Zip Archive

1. download one of the releases (zip file `grafikon-<x.y.z>.zip` - [releases](https://github.com/jub77/grafikon/releases))
2. unpack it
3. check if you have installed java
4. start Grafikon
    * using `java -jar grafikon.jar` from command line
    * or double-click on `grafikon.jar`
    * or use `grafikon.cmd` (`grafikon.sh`)

Outputs can be added from menu (output templates bundled with program as default):
*Outputs > Create default outputs*. The outputs can be then generated from *Outputs > Outputs...*
by pressing *Generate all* button.

In order to use Groovy string templates as well as output templates directly in the gtm,
the type of the diagram in *Settings > Settings...* has to be set to *raw*. All Groovy
scripts and templates are stripped from loaded gtm otherwise.

![Grafikon](https://jub.parostroj.net/grafikon/grafikon.png)

## Binaries with Bundled Java

Binaries are available for Windows and Linux and contains startup binary and bundled java
(`grafikon-<x.y.z>-<windows|linux>-x86_64.zip`).

1. download zip file
2. unpack it
3. start the binary

## Installer for Windows

Installer is available for Windows (`grafikon-<x.y.z>.msi`) and has the same content as the binary bundled with Java.

1. download the installer
2. install
3. start from Start menu

## Deb Package for Linux

Package is available for Linux and has the content as the binary bundled with Java

1. download the package
2. install
3. start from the desktop

## Zip with Bundled Java for macOS

The package for macOS contains only application bundled with Java without generated binaries.

1. download zip file
2. unpack it
3. start `grafikon-image/bin/grafikon`

## Simple String Substitution Templating

Simple string templates can be used e.g. in name template of train. The following substitutions
are available:

- `${variable}` - replace with variable
- `${if:bool-variable:string}` - if the variable is true, the string is substituted, otherwise empty string
- `${prefix:variable:string}` - prefix variable (if not empty) value with string, otherwise replace with empty string
- `${suffix:variable:string}` - add string after variable (if not empty), otherwise replace with empty string
- `${translate:variable:locale-variable|#locale-string}` - translation
- `${default:variable:default-string}` - if variable is an empty string, it uses default value

Variable can have fields (`${type.abbr}`) or index (`${stations[1]}` - first, last or number).

## Distances Between Stations

Distances between station are measured from and to center of stations. The length of the station is a separate value
and is not included in the measurement.

## Diagram Type

Can be configured in *Settings > Settings...*.  

- **normal** - default type with simple templating only and no output templates bundled in gtm
- **raw** - allow groovy templating and scripting, output templates can be bundled in gtm
