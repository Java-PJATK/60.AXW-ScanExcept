// AXW-ScanExcept/ScanExcept.java
 
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ScanExcept {
    public static void main (String[] args) {
          // no checked exception here
        @SuppressWarnings("resource")
        Scanner console = new Scanner(System.in);

        Scanner scfile = null;
        try {
            scfile = new Scanner(Paths.get("file.txt"), "UTF-8");
            int count = 0;
            while (scfile.hasNextLine())
                System.out.printf("%2d: %s%n", ++count,
                                  scfile.nextLine());
        } catch(IOException e) {
            System.out.println("Exception: " + e +
                               "\n**** Now the stack:");
            e.printStackTrace();
            System.out.println("**** CONTINUING...");
        } finally {
            System.out.println("FINALLY executed");
        }

        System.out.println("Enter anything...");
        String s = console.next();
        System.out.println("Read from console: " + s);

          // Active only when run with '-ea' option!
          // Should be used for debugging only.
          // No side effects allowed!
        assert scfile != null : "apparently no file";

        if (scfile != null) scfile.close();
    }
}
