/**
 * ServeurWeb.java
 * mikael Pothier et raphael Fortin
 */

import java.net.*;
import java.io.*;
class ServeurWeb 
{
   static Socket client = new Socket();
   static int personne=0;
   static ServerSocket serveur;
   static final int DELAI = 500;
   
   public static void main( String args[] ){
      verifierNbParam(args.length);   
      int port=verifierPort(args);
      String fichier =  verifierFichier(args);
      traitement(port,fichier);
   }
   //verifier si le fichier est existant si il n'y a moin de deux arguments on met le fichier par defaut
   private static String verifierFichier(String args[]){
	  //fichier par defaut
      String fichier = "c:/www";
      if(args.length ==2)
      {
         try
         {
            File dossier = new File(args[1]);
            if(dossier.isDirectory())
            {
               fichier = args[1];
            }
            else
            {
               System.out.println("Le fichier est invalide");
               System.exit(1);
            }
         }
         catch(Exception e)
         {
            System.out.println("Le fichier est invalide");
            System.exit(1);
         }
      }
	  //fait le dossier par defaut
      else
      {
          File dossier = new File(fichier);
		  //si le fichier par defaut n'est pas crée
          if(!dossier.isDirectory())
          {
            System.out.println("Le fichier par defaut n'existe pas");
            System.exit(1);
          }
      }
      return fichier;
   }
   //crée le serveur et il connecte les utilisateur
   private static void traitement(int port,String fichier){
      try
      {
         boolean fini=false;
         serveur = new ServerSocket( port );
         System.out.println("le Serveur est en ligne au port(TCP):" + port + " et la racine est:" + fichier);
		 //si il dépasse le delai il y aura une exception de type SocketException à cause que le serveur est inactif
         serveur.setSoTimeout( DELAI );
         
         Thread fin = new Thread(new Terminateur());
         fin.start();
         while(!fini)
         {
			//connecter le client au serveur
            try
            {
               client = serveur.accept();
               Thread conn = new Thread(new Connect(client,fichier));
               conn.start();
            }
            catch(SocketTimeoutException ste)
            {
               if(!fin.isAlive())
               {
                  fini=true;
                  System.out.println("le serveur est ferme");
				  //ferme tout les clients
                  client.close();
               }
            }
         }
      }
      catch ( IOException ioe )
      {
         System.out.println( ioe );
      }
   }
   //vérifie qu'il y a asser de parametre si il y en a trop il ferme le serveur
   private static void verifierNbParam(int nbParam){
      final int paramMax=2;
      if (nbParam > paramMax)
      {
         System.out.println("Il y a trop de parametre. Veuillez rentrer jusqu'a deux parametres.");
         System.exit(1);
      }
   }
   //vérifier que le port est entre les borne min et max sinon il ferme le serveur. si il n'y a aucun port(soit 0 argument) on met le port 80 par défaut.
   private static int verifierPort(String args[]){
      final int PORTMAX= 65535;
      int port =80;
      if(args.length >=1)
      {
         try
         {
			//tableau de string dont on doit le parse en int
            port = Integer.parseInt(args[0]);
            if(port<= 0 || port > PORTMAX)
            {
               System.out.println("le port n'est pas valide");
               System.exit(1);
            }
         }
		 //si le port recus n'est pas un nombre il va sortir une erreur et aller dans le catch
         catch(Exception e)
         {
            System.out.println("Le port est incorrect");
            System.exit(1);
         }
      }
      return port;   
   }
}