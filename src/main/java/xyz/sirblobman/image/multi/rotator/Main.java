package xyz.sirblobman.image.multi.rotator;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public final class Main {
    public static void main(String... args) {
        print("** Image Rotator **");
        print("");
        
        if(args.length < 3) {
            print("Missing arguments.");
            print("Usage: java -jar <jar> <input.png> <output.png> <degrees>");
            System.exit(1);
            return;
        }
        
        String inputFileName = args[0];
        String outputFileName = args[1];
        String rotationDegreesString = args[2];
        double rotationDegrees;
        
        try {
            rotationDegrees = Double.parseDouble(rotationDegreesString);
        } catch(IllegalArgumentException ex) {
            print("Invalid degrees value.");
            print("Usage: java -jar <jar> <input.png> <output.png> <degrees>");
            System.exit(1);
            return;
        }
        
        int result = rotate(inputFileName, outputFileName, rotationDegrees);
        System.exit(result);
    }
    
    private static void print(String message) {
        if(message == null) {
            System.out.println();
            return;
        }
        
        System.out.println(message);
    }
    
    public static int rotate(String inputFileName, String outputFileName, double degrees) {
        Path inputPath = Path.of(inputFileName);
        if(!Files.isRegularFile(inputPath)) {
            print("'" + inputFileName + "' does not exist.");
            return 1;
        }
        
        Path outputPath = Path.of(outputFileName);
        if(Files.exists(outputPath)) {
            print("'" + outputFileName + "' already exists.");
            return 1;
        }
        
        String outputMetaFileName = (outputFileName + ".mcmeta");
        Path outputMetaPath = Path.of(outputMetaFileName);
        if(Files.exists(outputMetaPath)) {
            print("'" + outputFileName + ".mcmeta' already exists.");
            return 1;
        }
        
        try {
            print("Input File: " + inputFileName);
            print("Output File: " + outputFileName);
            print("Meta File: " + outputMetaFileName);
            print("Rotation Degrees: " + degrees);
            
            rotateImage(inputPath, outputPath, degrees);
            createMetaFile(outputMetaPath);
        } catch(IOException ex) {
            print("Failed to rotate '" + inputFileName + "' because an error occurred:");
            ex.printStackTrace();
            return 1;
        }
        
        return 0;
    }
    
    private static void createMetaFile(Path outputPath) throws IOException {
        JsonObject json = new JsonObject();
        JsonObject animationJson = new JsonObject();
        animationJson.addProperty("frametime", 1);
        json.add("animation", animationJson);
    
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);
        
        Files.writeString(outputPath, prettyJson, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
    }
    
    private static void rotateImage(Path inputPath, Path outputPath, double rotationDegrees) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputPath.toFile());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        print("Image Width: " + originalWidth);
        print("Image Height: " + originalHeight);
        
        if(originalWidth == 0 || originalHeight == 0) {
            throw new IOException("Image width and height must not be zero.");
        }
        
        if(originalWidth != originalHeight) {
            print(originalWidth + " != " + originalHeight);
            throw new IOException("Image width and height must be the same.");
        }
    
        int maxRotations = (int) Math.floor(360.0D / rotationDegrees);
        int comboImageHeight = (originalHeight * maxRotations);
        BufferedImage comboImage = new BufferedImage(originalWidth, comboImageHeight, BufferedImage.TYPE_INT_ARGB);
        print("Max Rotations: " + maxRotations);
        
        for(int i = 0; i < maxRotations; i++) {
            double degrees = (i * rotationDegrees);
            BufferedImage rotatedImage = rotateImage(originalImage, degrees);
            
            int comboY = (originalHeight * i);
            drawPart(comboImage, rotatedImage, comboY);
            print("Finished Image Rotation " + i);
        }
        
        writeImage(outputPath, comboImage);
    }
    
    private static double toRadians(double degrees) {
        double multiply = (degrees * Math.PI);
        return (multiply / 180.0D);
    }
    
    private static BufferedImage rotateImage(BufferedImage original, double degrees) {
        double radians = toRadians(degrees);
        int width = original.getWidth();
        int height = original.getHeight();
        
        int centerX = (width / 2);
        int centerY = (height / 2);
        
        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) rotatedImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
        AffineTransform transform = new AffineTransform();
        transform.rotate(radians, centerX, centerY);
        
        graphics.drawImage(original, transform, null);
        return rotatedImage;
    }
    
    private static void drawPart(BufferedImage image, Image part, int y) {
        Graphics graphics = image.getGraphics();
        graphics.drawImage(part, 0, y, null);
    }
    
    private static void writeImage(Path outputPath, BufferedImage image) throws IOException {
        File outputFile = outputPath.toFile();
        if(!ImageIO.write(image, "PNG", outputFile)) {
            throw new IOException("No valid writer found for formatName PNG.");
        }
    }
}
