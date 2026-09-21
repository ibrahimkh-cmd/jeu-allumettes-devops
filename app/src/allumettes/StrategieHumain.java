package allumettes ;
import java.util.Scanner;



public class StrategieHumain implements Strategie{

    /** Scanner pour la saisie, ouvert une seule fois pour tout le programme. */
    private static Scanner scanner = new Scanner(System.in);

    /** Nom du joueur. */
    private String nom;

    public StrategieHumain(String nom) {
        this.nom = nom;
    } 

    @Override
    public int getPrise(Jeu jeu) {
        int prise = 0; // Initialisation de 'prise' ici pour qu'elle soit définie et pour que la fonction compile.
                        // La valeur de 'prise' sera modifiée dans la boucle while ci-dessous.
        boolean coupValide = false;

        while(!coupValide) {
            System.out.print(nom +", combien d'allumettes ? ");
            String reponseUtilisateur = scanner.next();
            if(reponseUtilisateur.equals("triche")){ //si l'utilisateur decide de tricher 
                if (jeu.getNombreAllumettes() > 1 ) {
                    try{
                        jeu.retirer(1); //retirer discretement une allumette .
                    } catch(CoupInvalideException e) {

                    }  
                    System.out.println("[Une allumette en moins, plus que "+jeu.getNombreAllumettes()+". Chut !]");
                } else {
                    System.out.println("[Il ne reste qu'une allumette,pas la peine de tricher, tu as perdu !]"); 
                }
                coupValide = false;
            } 
            else {
                try {
                    prise = Integer.parseInt(reponseUtilisateur);
                    coupValide = true;//sortir de la boucle si la saisie est correcte
                } catch (NumberFormatException e) {
                    System.out.println("Vous devez donner un entier.");
                    coupValide = false; 
                }
            }
           
        } 
        return prise;
    }  
    
}

 
