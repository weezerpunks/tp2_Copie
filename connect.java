/**
 * Connect.java
 * mikael Pothier et raphael Fortin
 */

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
class Connect implements Runnable
{
   Socket client;
   File fichier;
   String racine;
   File tabFile[];
   public static final int DELAI = 30000;
   Connect(){}
   Connect(Socket Client,String fichier)
   {
      this.client = Client;
      racine = fichier;
      this.fichier = new File(fichier);
	  //fait une liste de tout les fichier dans le repertoire recus
      this.tabFile = this.fichier.listFiles();
   }
   public void run()
   {
      try
      {
         BufferedReader reader = new BufferedReader(
                                 new InputStreamReader( client.getInputStream() ) );
         PrintWriter writer = new PrintWriter(
                              new OutputStreamWriter( client.getOutputStream() ),true );
							  
         //writer.println("Serveur Web fait par Mikael pothier et Raphael Fortin (livrable 2)");
         ecrire(reader,writer);
		 reader.close();
		 writer.close();
      }
      catch(IOException ioe)
      {
         System.out.println( ioe );
      }      
   }
   //afficher les fichier + leur grosseur + la derniere fois qu'ils ont été modifier ainsi que le nombre de fichier et repertoir
   public void lireFichier(PrintWriter writer){
      int nbFichier=0;
      writer.println("Contenu du repertoire courant: " + fichier.getAbsolutePath());
      for (int i=0; i < tabFile.length;++i){
         if(tabFile[i].isFile())
         {
						//maniere pour bien espace les caractéristique des fichier (%n donne un changement de ligne)
           writer.printf("%-40s %-10d %tD %n","   "+tabFile[i].getName(),tabFile[i].length(),tabFile[i].lastModified()); 
         }
         else
         {
            writer.printf("%-50s %tD %n","[ ]"+tabFile[i].getName(),tabFile[i].lastModified());
         }
         ++nbFichier;
      }
      writer.println(nbFichier + "fichier(s)");
   }
   
   //verifie que la commande est bonne 
   private void verifierCommande(String[] param, PrintWriter writer){
      if(param.length !=3)
      {
         writer.println("400 Mauvaise requete");
         fichier=null;
      }
      else if(!param[0].equalsIgnoreCase("GET"))
      {
         writer.println("501 commande inixistant");
         fichier=null;
      }
      else
      {
         try
         {
            File dossier = new File(fichier.getAbsolutePath()+"\\"+param[1]);
            if(!dossier.exists() && !dossier.isDirectory())
            {
               writer.println("404 fichier inexistant");
               fichier=null;
            }
            else
            {
               //writer.println("HTTP/1.0 200 OK");
               fichier=dossier;
               headerWeb(writer,fichier);
            }
         }
		 //si il rentre dans le catch s'est que le fichier n'existe pas
         catch(Exception e)
         {
            writer.println("404 fichier inexistant");
            fichier=null;
         }
      }
   }
   
   private void headerWeb(PrintWriter writer,File fichier){
      writer.println("HTTP/1.0 200 OK");
      Date date = new Date();
      writer.println("Date: " + getDateRfc822( date ) );
      writer.println("Server: Super Duper ServeurWeb Raphael Fortin et Mikael Pothier");
      writer.println("Content-Type: " + getMIME(fichier));
      Date lastM = new Date(fichier.lastModified());
      writer.println("Last-modified: " +  getDateRfc822( lastM ));
      writer.println("Content-length: " + (fichier.length()/4));      
      
      
      writer.println();
   }
   
   //Fonction a Francois pour convertir les dates
   public String getDateRfc822( Date date ){
      SimpleDateFormat formatRfc822
         = new SimpleDateFormat( "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z",
            Locale.US );
      
      return formatRfc822.format( date );
   }
   
   //Permet de recevoir le type MIME
   public String getMIME(File fichier){
      
      String MIME = "";
      String name = fichier.getName();
      String[] part = name.split("\\.");
      switch (part[1]){
            case "txt": 
                  MIME = "text/plain";
            break;
            
            case "html":
                  MIME = "text/html";
            break;
            
            case "gif":
                  MIME = "image/gif";
            break;
            
            case "jpeg":
                  MIME = "image/jpeg";
            break;
            
            case "jpg":
                  MIME = "image/jpeg";
            break;
            
            case "png":
                  MIME = "image/png";
            break;
      }
      
      return MIME;
   }
   
   
   //affiche le ficher en lissant byte par byte
   private void afficherFichier(PrintWriter writer, File fichier){
      try
      {
		//lis a partir du fichier recus du serveur
         FileInputStream fis = new FileInputStream(fichier);
		 //écris dans le fichier(ou console) que le client envoye au serveur
         OutputStream os = new BufferedOutputStream(client.getOutputStream());
         
         byte[] bytebuffer = new byte[1024];
		 // si il =-1 s'est qu'il a terminer
         while (fis.read(bytebuffer) !=-1)
         {
            os.write(bytebuffer);
         }
         fis.close();
         os.close();
      }
      catch(IOException ioe)
      {
         writer.println(ioe);
      }
   }
   
   //ancienne fonction qui fait le traitement général
   public void ecrire(BufferedReader reader,PrintWriter writer) {
      //lireFichier(writer);
      boolean fini = false;
      try
      {
         //writer.print("=>");
         //writer.flush();
		 //si le client est innactif apres le delai il y aura une erreur SoketTimeoutException
         client.setSoTimeout( DELAI );
         String requete = reader.readLine();
         String ligne = requete;
         //parse la ligne en enlevant les espace et en separant les deux mots
         String[] param = requete.trim().split("\\s+");
         verifierCommande(param,writer);
         if(fichier.isFile())
         {
            afficherFichier(writer,fichier);
         }
		 //sinon c'est un repertoire
         else
         {
            tabFile = fichier.listFiles();
            lireFichier(writer);
         }
      }
      catch( SocketTimeoutException ste )
      {
         // le délai d'inactivité est expiré
         fini = true;
      }           
      catch(IOException ioe)
      {
         System.out.println("la connexion a ete interrompu");
      } 
      catch(Exception e)
      {
         System.out.println("la connexion a ete interrompu");
      }
      finally
      {
         try
         {
            client.close();
         }
         catch(IOException ioe)
         {
            System.out.println(ioe);
         }
      }
   }
}