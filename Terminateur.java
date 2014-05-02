/**
 *Terminateur.java
 *mikael Pothier et raphael Fortin
 */

import java.io.*;
//thread qui permet au serveur de continuer de marcher et de pouvoir le fermer
public class Terminateur implements Runnable {

   public void run ()
   {
      try
      {
         String Lettre;
         BufferedReader reader = new BufferedReader(
                           new InputStreamReader(System.in));

         Lettre = reader.readLine();
		 //si lettre q on ferme le serveur
         while(!Lettre.trim().equalsIgnoreCase("q"))
         {
            Lettre = reader.readLine();
         }
		  reader.close();
      }
      catch(Exception ioe)
      {
         System.err.println("Serveur ferme");
         System.exit(5);
      }
   }
}