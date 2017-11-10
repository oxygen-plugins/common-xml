# common-xml
Common XML utils - This project contains frequently used XML utils, so they can be used in several projects.

## Release Notes

### Version 0.1.1

- Fix for Entity parsing: Support Character entities (e.g. &amp;amp; / &amp;#x00A0;)
- Fix for NodeInfo: if node baseUri is null use the baseUri of the parent node.

### Version 0.1.0

- XInclude support
- Some smaller fixes
- Added helper classes for array handling, reflections, regex, threading and compare XML
- Added this release notes
- Maven changes:
    - Use woodstox 5.2.1
    - Use a local distribution repository

 

### Version 0.0.2

- Added ChangePropertyListener

### Version 0.0.1
- First public release