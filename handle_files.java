import java.io.File;  // Import the File class
import java.io.FileWriter; // to write to files
import java.io.IOException;
public class handle_files 
{

    public static void write_to_file(String filename) 
    {
        try {
            FileWriter textFile = new FileWriter(filename+".txt");
            textFile.write("Files in Java might be tricky, but it is fun enough!");
            textFile.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    } 


}
