# Image Multi-Rotator

This is a utility program to create mods

It takes a png file and rotates it as many times as you need

I used this with my gemmary mod to create the HD atom textures

## Requirements
- 4GB of RAM
- A way to execute JAR files (terminal/CMD)
- An image that has the same width and height (Example: 512x512)

### Example Usage
Format:

```cmd
java -Xmx4G -jar "ImageMultiRotator-{version}.jar" "<input file>" "<output file name>" <degrees per rotation>
```

Example:

```cmd
java -Xmx4G -jar "ImageMultiRotator-1.0.jar" "tnt.png" "tnt-rotated.png" 90.0
```

The above example will take the file "tnt.png" and create a "tnt-rotate.png" file with 4 rotations.

This will also create a "tnt-rotate.png.mcmeta" file so you can use it in a mod/texture pack
