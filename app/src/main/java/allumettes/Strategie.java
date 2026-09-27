package allumettes;

/**
 * Interface représentant une stratégie de jeu.
 */
public interface Strategie {
     /**
     * Détermine le nombre d'allumettes à retirer.
     *
     * @param jeu l'instance du jeu en cours
     * @return le nombre d'allumettes à retirer
     */
     int getPrise(Jeu jeu);
}
