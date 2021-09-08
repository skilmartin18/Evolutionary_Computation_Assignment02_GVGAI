package handle_files;

import java.io.File;  // Import the File class
import java.io.FileWriter; // to write to files
import java.io.IOException;
public class handle_files 
{
    // example usage:
    // import handle_files.handle_files;
    // handle_files.write_to_file("poopypant","i poop my pant");
		// handle_files.write_to_file("poopypant", "i poop my pant");
    public static void write_to_file(String filename, String text) 
    {
        try {
            FileWriter textFile = new FileWriter(filename+".txt",true);
            textFile.write(text);
            textFile.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    } 


}
