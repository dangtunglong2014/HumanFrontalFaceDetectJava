/*Author: Long Dang */
/*Version: 2.0 (using Java)*/
/*Starting date: 1/08/2017 */

import java.io.*;  
import java.util.Scanner;

public class FaceDetect {

   public static void main(String []args) throws IOException{
      System.out.println("****************************************************");
      System.out.println("*********** HUMAN FRONTAL FACE DETECTION ***********");
      System.out.println("****************************************************");
      System.out.println("****** Detect human frontal face in the photo ******");
      System.out.println("****************************************************");
      System.out.println("STEP 1: Move the photo to the source code directory");
      System.out.println("STEP 2: Type the name of the photo here:");
      String s;
      Scanner sc = new Scanner(System.in);
      s = sc.nextLine();
      MyImage img = new MyImage();
      img.faceDetect(s);
      System.out.println("****************************************************");
   }
}