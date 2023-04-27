import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileDisorganizer {

    public static void main(String[] args) {
        // The path of the folder to disorganize
        String folderPath = "C:\\Users\\draon\\Downloads";

        // Create a File object for the folder to disorganize
        File folder = new File(folderPath);

        // Get a list of all files and folders in the folder
        File[] files = folder.listFiles();

        // Iterate over the list of files and folders
        for (File file : files) {
            if (file.isDirectory()) {
                disorganizeFilesInFolder(file, folderPath);
            }
        }
    }

    private static void disorganizeFilesInFolder(File folder, String destinationFolderPath) {
        if (folder.getName().equals("Folders")) {
            moveContentsOfFolders(folder, destinationFolderPath);
            return;
        }

        File[] filesInFolder = folder.listFiles();

        for (File file : filesInFolder) {
            if (!file.isDirectory()) {
                moveToUniqueFile(file, destinationFolderPath);
            }
        }
    }

    private static void moveContentsOfFolders(File foldersDirectory, String destinationFolderPath) {
        File[] filesInFolder = foldersDirectory.listFiles();

        for (File file : filesInFolder) {
            if (file.isDirectory()) {
                moveToUniqueFolder(file, destinationFolderPath);
            }
        }
    }

    private static void moveToUniqueFolder(File folder, String destinationFolderPath) {
        String folderName = folder.getName();
        int copyNumber = 1;
        File targetFolder = new File(destinationFolderPath, folderName);
        while (targetFolder.exists()) {
            String newFolderName = String.format("%s (%d)", folderName, copyNumber);
            targetFolder = new File(destinationFolderPath, newFolderName);
            copyNumber++;
        }
        folder.renameTo(targetFolder);
    }

    private static void moveToUniqueFile(File file, String destinationFolderPath) {
        String fileName = file.getName();
        int copyNumber = 1;
        File targetFile = new File(destinationFolderPath, fileName);
        while (targetFile.exists()) {
            String newFileName = generateUniqueFileName(fileName, copyNumber);
            targetFile = new File(destinationFolderPath, newFileName);
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

    private static String generateUniqueFileName(String fileName, int copyNumber) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return fileName + " (" + copyNumber + ")";
        } else {
            return fileName.substring(0, lastDotIndex) + " (" + copyNumber + ")" + fileName.substring(lastDotIndex);
        }
    }
}
