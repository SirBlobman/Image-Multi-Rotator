package com.SirBlobman.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class MultiRotator {
    public static void main(String[] args) {
        File pngFile = null;
        File outputFile = null;
        double degreesPerRotation = 90.0D;
        if(args.length > 0) {
            pngFile = new File(args[0]).getAbsoluteFile();
            System.out.println("Using input file '" + pngFile + "'");
            System.out.println();
            
            if(args.length > 1) {
                outputFile = new File(args[1]).getAbsoluteFile();
                System.out.println("Using output file '" + outputFile + "'");
                System.out.println();
            }
            
            if(args.length > 2) {
                try {
                    degreesPerRotation = Double.parseDouble(args[2]);
                } catch(NumberFormatException ex) {
                    System.out.println("Invalid Number '" + args[2] + "'. Defaulting to 90.0");
                    System.out.println();
                    ex.printStackTrace();
                    degreesPerRotation = 90.0D;
                }
            }
        }
        
        System.out.println("Degrees Per Rotation: " + degreesPerRotation);
        System.out.println();
        
        final int maxRotations = (int) Math.floor(360.0D / degreesPerRotation);
        System.out.println("Max Rotations: " + maxRotations);
        System.out.println();
        
        if(pngFile == null || !pngFile.exists() || !pngFile.getName().endsWith(".png")) pngFile = openFileChooserPNG();
        
        if(pngFile != null) {
            System.out.println("Reading image '" + pngFile + "'...");
            System.out.println();
            BufferedImage originalImage = readImage(pngFile);
            final int imageWidth = originalImage.getWidth();
            final int imageHeight = originalImage.getHeight();
            System.out.println("W: " + imageWidth + " H: " + imageHeight);
            System.out.println();
            
            if(imageWidth == imageHeight && imageWidth > 0) {
                final int comboImageHeight = (imageHeight * maxRotations);
                System.out.println("Combo Image Height: " + comboImageHeight);
                System.out.println();
                BufferedImage comboImage = new BufferedImage(imageWidth, comboImageHeight , BufferedImage.TYPE_INT_ARGB);
                
                for(int i = 0; i < maxRotations; i++) {
                    double degrees = (i * degreesPerRotation);
                    BufferedImage rotated = rotateImage(originalImage, degrees);
                    
                    final int comboY = (imageHeight * i);
                    comboImage = drawPart(comboImage, rotated, 0, comboY);
                    System.out.println("Finished image rotation " + i);
                    System.out.println();
                }
                
                if(outputFile == null) {
                    System.out.println("Opening file saving dialog...");
                    System.out.println();
                    outputFile = openFileSaverPNG();
                    System.out.println("Selected File: '" + outputFile + "'");
                    System.out.println();
                }
                
                if(outputFile != null) {
                    File folder = outputFile.getParentFile();            
                    File mcmetaFile = new File(folder, outputFile.getName() + ".mcmeta");
                    
                    try {
                        if(!outputFile.exists()) {
                            folder.mkdirs();
                            outputFile.createNewFile();
                        }
                        
                        if(!mcmetaFile.exists()) {
                            folder.mkdirs();
                            mcmetaFile.createNewFile();
                        }
                        
                        JsonObject json = new JsonObject();
                        JsonObject animationJson = new JsonObject();
                        animationJson.addProperty("frametime", 1);
                        json.add("animation", animationJson);
                        
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String prettyJson = gson.toJson(json);
                        
                        FileWriter fw = new FileWriter(mcmetaFile);
                        PrintWriter pw = new PrintWriter(fw);
                        pw.print(prettyJson);
                        pw.flush();
                        pw.close();
                        fw.close();
                        System.out.println("Created '" + mcmetaFile + "'");
                        System.out.println();
                        
                        System.out.println("Saving '" + outputFile + "'...");
                        System.out.println();
                        ImageIO.write(comboImage, "png", outputFile);
                        System.out.println("Done!");
                        
                        for(int i = 0; i < 20; i++) System.out.println();
                    } catch(Throwable ex) {
                        System.out.println("Failed to save png rotations to '" + outputFile + "' and '" + mcmetaFile + "':");
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Invalid output file!");
                }
            }
        } else {
            System.out.println("The file '" + pngFile + "' does not exist!");
        }
    }
    
    private static double toRadians(double degrees) {
        double pi = Math.PI;
        double divide = (pi / 180.0D);
        return (degrees * divide);
    }
    
    private static BufferedImage rotateImage(BufferedImage image, double degrees) {
        final double radians = toRadians(degrees);
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        
        final int centerX = (imageWidth / 2);
        final int centerY = (imageHeight / 2);
        
        BufferedImage rotatedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) rotatedImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        AffineTransform transformer = new AffineTransform();
        transformer.rotate(radians, centerX, centerY);
        graphics.drawImage(image, transformer, null);
        return rotatedImage;
    }
    
    private static File openFileChooserPNG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a PNG File...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        ImagePreview imagePreview = new ImagePreview(fileChooser);
        fileChooser.setAccessory(imagePreview);
        
        FileFilter fileFilter = new FileFilter() {
            @Override
            public String getDescription() {
                return "PNG Images";
            }
            
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                return fileName.endsWith(".png");
            }
        };
        fileChooser.setFileFilter(fileFilter);
        
        int response = fileChooser.showOpenDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) return fileChooser.getSelectedFile();
        
        return null;
    }
    
    private static File openFileSaverPNG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PNG and MCMeta...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileFilter fileFilter = new FileFilter() {
            @Override
            public String getDescription() {
                return "PNG Images";
            }
            
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                return fileName.endsWith(".png");
            }
        };
        fileChooser.setFileFilter(fileFilter);

        int response = fileChooser.showOpenDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) {
            File pngFile = fileChooser.getSelectedFile();
            return pngFile;
        } else return null;
    }
    
    private static BufferedImage readImage(File file) {
        try {
            return ImageIO.read(file);
        } catch(Throwable ex) {
            return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
        }
    }
    
    private static BufferedImage drawPart(BufferedImage image, Image part, int x, int y) {
        Graphics graphics = image.getGraphics();
        graphics.drawImage(part, x, y, null);
        return image;
    }
}