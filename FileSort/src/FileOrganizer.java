import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class FileOrganizer {
    public static void main(String[] args) {
        // The path of the folder to move files and folders from
        String sourceFolderPath = "C:\\Users\\draon\\Desktop";

        // The path of the folder to move files and folders to and organize
        String destinationFolderPath = "C:\\Users\\draon\\Downloads";

        // Move all files and folders from the Desktop to the Downloads folder
        moveAllFilesAndFolders(sourceFolderPath, destinationFolderPath);

        // Organize the files and folders in the Downloads folder
        organizeFilesAndFolders(destinationFolderPath);
    }

    private static void moveAllFilesAndFolders(String sourceFolderPath, String destinationFolderPath) {
        File sourceFolder = new File(sourceFolderPath);
        File[] files = sourceFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                File destinationFile = new File(destinationFolderPath, file.getName());
                if (file.isFile()) {
                    try {
                        Files.move(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.err.println("Failed to move file:");
                        System.err.println("Source: " + file.getAbsolutePath());
                        System.err.println("Destination: " + destinationFile.getAbsolutePath());
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    file.renameTo(destinationFile);
                }
            }
        }
    }

    private static void organizeFilesAndFolders(String destinationFolderPath) {
        // Create a map to store file types as keys and the corresponding File objects as values
        Map<String, File> fileTypeFolderMap = new HashMap<>();

        // Create a File object for the folder to organize
        File folder = new File(destinationFolderPath);

        // Get a list of all files and folders in the folder
        File[] files = folder.listFiles();

        // Iterate over the list of files and folders
        for (File file : files) {
            // Check if the current file is a directory
            if (file.isDirectory()) {
                // If the current file is a directory, check if it should be excluded
                String fileName = file.getName();
                if (fileName.endsWith(" Files")) {
                    continue;
                }

                // Move the folder to the "Folders" directory
                moveToFolder(file, destinationFolderPath, "Folders");
            } else {
                // Get the file extension (the part of the file name after the last '.' character)
                String fileExtension = getFileExtension(file);

                // If the file does not have an extension, treat it as a file with a dot prefix
                if (fileExtension == null) {
                    fileExtension = "dot-prefixed";
                }

                // Convert the file extension to upper case
                fileExtension = fileExtension.toUpperCase();

                // Get the folder for the file extension, creating it if necessary
                File fileTypeFolder = fileTypeFolderMap.computeIfAbsent(fileExtension, key -> {
                    File newFolder = new File(destinationFolderPath, key + " Files");
                    newFolder.mkdir();
                    return newFolder;
                });

                // Move the file to the file type folder
                moveToUniqueFile(file, fileTypeFolder);
            }
        }
    }


    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return null;
    }

    private static void moveToFolder(File folder, String destinationFolderPath, String folderName) {
        File parentFolder = new File(destinationFolderPath, folderName);
        parentFolder.mkdir();
        File newFolder = new File(parentFolder, folder.getName());
        folder.renameTo(newFolder);
    }

    private static void moveToUniqueFile(File file, File fileTypeFolder) {
        String fileName = file.getName();
        String fileExtension = getFileExtension(file);
        int copyNumber = 1;
        File targetFile = new File(fileTypeFolder, fileName);
        while (targetFile.exists()) {
            String newFileName = String.format("%s (%d).%s",
                    fileName.substring(0, fileName.lastIndexOf('.')),
                    copyNumber,
                    fileExtension);
            targetFile = new File(fileTypeFolder, newFileName);
            copyNumber++;
        }
        try {
            Files.move(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to move file:");
            System.err.println("Source: " + file.getAbsolutePath());
            System.err.println("Destination: " + targetFile.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
