# Image Multi-Rotator
Image Multi-Rotator is a utility application that helps with creating rotation animations.

I used this with in my Gemmary mod to create the rotating atom textures.

## Requirements
- Java 17
- A way to execute JAR files (terminal/CMD)
- An image that has the same width and height (Examples: 16x16, 32x32, 512x512)

### Usage Format:
```cmd
java -jar "Image-Multi-Rotator.jar" "<input file>" "<output file name>" <degrees per rotation>
```

### Example Input / Ouput
#### Command
```cmd
java -jar "Image-Multi-Rotator-2.0.0-SNAPSHOT.jar Apple.png Apple-animated.png 10
```

#### Input
![Apple Before](/example/Apple.png?raw=true "Apple Before")

`Apple.png`

#### Output
![Apple After](/example/Apple-animated.png?raw=true "Apple After")

`Apple-animated.png`

#### Output Meta
```json
{
  "animation": {
    "frametime": 1
  }
}
```

`Apple-animated.png.mcmeta`
