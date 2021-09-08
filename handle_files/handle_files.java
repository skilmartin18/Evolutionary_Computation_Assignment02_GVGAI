package handle_files;

import java.io.FileWriter; // to write to files
import java.io.IOException; // handles exceptions, required otherwise passes an error
public class handle_files 
{
    // example usage:
    // import handle_files.handle_files;
    // handle_files.write_to_file("poopypant","i poop my pant");
		// handle_files.write_to_file("poopypant", "i poop my pant");
    // does not print a \n after text
    // filename should be filename excluding .txt.
    public static void write_to_file(String filename, String text) 
    {
        try 
        {
          FileWriter textFile = new FileWriter(filename+".txt",true);
          textFile.write(text);
          textFile.close();
          System.out.println("Successfully wrote to the file.");
        } catch (IOException e) 
        {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
    } 
}
