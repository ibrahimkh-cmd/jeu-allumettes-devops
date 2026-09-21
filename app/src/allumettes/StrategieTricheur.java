package allumettes;

public class StrategieTricheur implements Strategie {

    @Override
    public int getPrise(Jeu jeu) {
        System.out.println("[Je triche...]");

        int allumettesRestantes = Jeu.PRISE_MAX - 1;

        boolean confiance = Arbitre.getConfiant();
        if (confiance) {
            System.out.println("[Allumettes restantes : " + 
            allumettesRestantes + "]");
 }

        if (jeu.getNombreAllumettes() >= Jeu.PRISE_MAX) {
            int nombreRestant = jeu.getNombreAllumettes();
            for (int i = 0; i < nombreRestant - 2; i++) {
                try {
                    jeu.retirer(1);
                } catch (CoupInvalideException e) {
                }
            }
        }

        return 1;
    }
}
