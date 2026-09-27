package allumettes;

public class Arbitre {
    private Joueur joueur1;
    private Joueur joueur2;
    private static boolean arbitreConfiant;

    public Arbitre(Joueur joueur1, Joueur joueur2) {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
    }

    public void setConfiant(boolean confiant) {
        this.arbitreConfiant = confiant;
    }

    public static boolean getConfiant() {
        return arbitreConfiant;
    }

    public void arbitrer(Jeu jeu) {
        Joueur joueurCourant = joueur1;
        boolean finDePartie = false;

        // Choisir entre le vrai jeu et le proxy (selon si l'arbitre est confiant ou non)
        Jeu jeuUtilise = arbitreConfiant ? jeu : new ProxyJeu(jeu); // Si l'arbitre est confiant, le jeu utilisé sera le vrai jeu, sinon ce sera le proxy
        
        while (!finDePartie) {

            int prise = 0;
            boolean coupValide = false;
            while (!coupValide) {              
                try {
                    System.out.println("Allumettes restantes : " + jeu.getNombreAllumettes());
                    // Demander au joueur combien d'allumettes il prend
                    prise = joueurCourant.getPrise(jeuUtilise); 
                    // Afficher le coup joué
                    if (prise > 1) {
                        System.out.println(joueurCourant.getNom() + " prend " + prise + " allumettes."); 
                    } else {
                        System.out.println(joueurCourant.getNom() + " prend " + prise + " allumette."); 
                    }

                    if (prise < 1) {
                        throw new CoupInvalideException(prise, "< 1");
                    }
                    if (prise > jeu.getNombreAllumettes()) {
                        throw new CoupInvalideException(prise, "> " + jeu.getNombreAllumettes());
                    } 
                    if (prise > Jeu.PRISE_MAX) {
                        throw new CoupInvalideException(prise, "> " + Jeu.PRISE_MAX);
                    }

                    coupValide = true;
                    jeu.retirer(prise); // Seul l’arbitre applique réellement la modification

                } catch (CoupInvalideException e) {
                    System.out.println("Impossible ! Nombre invalide : " + e.getCoup() + " (" + e.getProbleme() + ")");
                    coupValide = false;
                    finDePartie = false;
                    System.out.println();

                } catch (OperationInterditeException e) {
                    // Si un joueur triche, on arrête la partie
                    System.out.println("Abandon de la partie car " + joueurCourant.getNom() + " triche !");
                    return;
                }
            }

            // Vérifier si la partie est terminée
            if (jeu.getNombreAllumettes() == 0) {
                finDePartie = true;
                System.out.println(joueurCourant.getNom() + " perd !");
                Joueur gagnant = (joueurCourant == joueur1) ? joueur2 : joueur1;
                System.out.println(gagnant.getNom() + " gagne !");
            }

            // Changer de joueur après chaque tour
            joueurCourant = (joueurCourant == joueur1) ? joueur2 : joueur1;
            System.out.println();
        }
    }
}
