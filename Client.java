/**
 * client.java
 * mikael Pothier et raphael Fortin
 */

import java.net.*;
import java.io.*;
class Client{
   static int port =80;
   
   public static void createClient(int port){
   Socket socket = null;  
      try
      {
		 //met le socket au port recu et au local host
         socket=new Socket("localhost",port);
         
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader reader = new BufferedReader(
                                 new InputStreamReader( socket.getInputStream() ) );                                 
           
            boolean fini =false;
            String texte;
			//tant que le texte ne finis pas par fichier(s) on recois le texte
            while(!fini){
               texte = reader.readLine();
               if(texte == null || texte.endsWith("fichier(s)")){
                  if(texte.endsWith("fichier(s)")){
                     System.out.println(texte);
                  }
                  fini=true;
               }
               else{
                  System.out.println(texte);
               }
            }
			
               System.out.print("Fichier a recuperer: ");
              
               texte = clavier.readLine();
			   //pour garder le nom du fichier (si on ne le garde pas il ne pourra pas le cree et si on cree le fichier avant de verifier si il est valide il va creer un fichier invalide)
			   String nomFichier = texte;
               if(texte != null){
                  writer.println("GET " + texte);
               }
               writer.flush();
			   //on va chercher le fichier a lire(il va donner un code derreur 
               texte = reader.readLine();
			   //verification si il y a un erreur (exit si il y en a une)
			   if(texte.equals("=>404 fichier inexistant")){
					System.out.println("le fichier n'est pas valide");
					System.exit(1);
			   }
			   else if(texte.equals("501 commande inixistant") || texte.equals("=>400 Mauvaise requete")){
					System.out.println("la requete est incorrect");
					System.exit(1);
			   }
			   
			   InputStream is = new BufferedInputStream(socket.getInputStream());
			   //si il n,y a aucune erreur(200) on cree le fichier et on ecrit dedans
			   BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(nomFichier));
			   //si le texte = au prompt + le code 200 sa veut dire qu'il est existant
               if(texte.equals("200 OK")){
                  System.out.println("200 OK");
                  
				  //permet d'ecrire dans le nouveau fichier dans le repertoire du projet
                  byte[] byteBuffer = new byte[1024];
                  int i=0;
                  i=is.read(byteBuffer);
                  while(i!=-1){
                     bos.write(byteBuffer,0,i);
                     bos.flush();
                     i=is.read(byteBuffer);
                  }
               }
			//fermeture des flux
		    bos.close();			
            writer.close();
            reader.close();
            is.close();
            socket.close();
      }
      catch(Exception e){
      
         System.err.println(e);
         
      }
   }
   private void afficherEnTete(){
      
   }
   public static void main( String args[] )
   {
      createClient(port);
   }
}