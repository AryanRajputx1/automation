import java.io.File;
import java.nio.file.*;

class FileSorter {

    public static void main(String[] args) {
        try {

            String downloadFolderPath = "C:\\Users\\rajpu\\Downloads";  // Update this path as needed
            Path downloadPath = Paths.get(downloadFolderPath);

            // Start monitoring the Downloads folder
            monitorDirectory(downloadPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void monitorDirectory(Path path) throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();  // Create a WatchService to monitor folder
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);  // Watch for new files being created

        System.out.println("Monitoring folder: " + path);

        while (true) {
            WatchKey key = watchService.take();  // Wait until something changes (file added)

            // Check for events (new files being added)
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path filePath = (Path) event.context();
                File file = filePath.toFile();

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {  // A new file is created
                    System.out.println("New file detected: " + file.getName());
                    try {
                        sortFileByExtension(file);  // Sort the file based on its extension
                    } catch (Exception e) {
                        System.err.println("Error sorting file: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }

            // Reset the key to continue watching
            boolean valid = key.reset();
            if (!valid) {
                break;  // Stop if the directory is no longer valid
            }
        }
    }

    // Method to sort files based on extension and move them to appropriate folder
    public static void sortFileByExtension(File file) {
        String fileName = file.getName();  // Get the file name
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);  // Get file extension

        String folder = "Misc";  // Default folder


        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")) {
            folder = "Images";
        } else if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("docx")) {
            folder = "Documents";
        } else if (extension.equalsIgnoreCase("mp3") || extension.equalsIgnoreCase("wav")) {
            folder = "Music";
        }

        // Create target folder inside Downloads if it doesn't exist
        File targetFolder = new File(file.getParent(), folder);
        if (!targetFolder.exists()) {
            boolean created = targetFolder.mkdir();  // Create the folder if it doesn't exist
            if (created) {
                System.out.println("Folder created: " + targetFolder.getName());
            } else {
                System.out.println("Failed to create folder: " + targetFolder.getName());
            }
        }


        File targetFile = new File(targetFolder, file.getName());
        boolean moved = file.renameTo(targetFile);  // Rename (move) the file to the target folder
        if (moved) {
            System.out.println("Moved file: " + file.getName() + " to " + folder);
        } else {
            System.out.println("Failed to move file: " + file.getName());
        }
    }
}
