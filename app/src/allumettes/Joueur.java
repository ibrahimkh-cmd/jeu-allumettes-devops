package allumettes;

/**
 * Classe représentant un joueur dans le jeu.
 * Un joueur a un nom et une stratégie qui détermine ses prises.
 */
public  class Joueur {

    private String nom;
    private Strategie strategie; 

    /**
     * Constructeur du joueur.
     * @param nom Le nom du joueur.
     * @param strategie La stratégie choisie par le joueur.
     */

    public Joueur(String nom, Strategie strategie) {
        this.nom = nom;
        this.strategie = strategie;
    }

    
    /**
     * Retourne le nom du joueur.
     * @return Le nom du joueur.
     */
    public String getNom() {
        return this.nom;
    }
    /**
     * Détermine la prise du joueur en fonction de sa stratégie.
     * @param jeu L'état actuel du jeu.
     * @return Le nombre d'allumettes à prendre selon la stratégie du joueur.
     */

    public int getPrise(Jeu jeu) {
        return strategie.getPrise(jeu);
        }    
}
