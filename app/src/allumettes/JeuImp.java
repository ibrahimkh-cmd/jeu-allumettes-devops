package allumettes;

/**
 * Implémentation du jeu des allumettes.
 */
public class JeuImp implements Jeu {
    private  int allumettes = 13;
    
    /**
     * Constructeur.
     *
     * @param allumettes le nombre initial d'allumettes
     */
    public JeuImp(int allumettes) {
        this.allumettes = allumettes;
    }
    @Override
    public int getNombreAllumettes() {
        return this.allumettes;
    }

    /** Retire des allumettes en respectant les règles */
    @Override
    public void retirer(int nbPrises) throws CoupInvalideException {
        if (nbPrises < 1) {
            throw new CoupInvalideException(nbPrises, "< 1");
        }
        if (nbPrises > PRISE_MAX) {
            throw new CoupInvalideException(nbPrises, "> " + PRISE_MAX);
        }
        if (nbPrises > allumettes) {
            throw new CoupInvalideException(nbPrises, "> " + allumettes);
        } 
        // Si toutes les vérifications sont passées, on enlève les allumettes.
        allumettes -= nbPrises;
    }
}
