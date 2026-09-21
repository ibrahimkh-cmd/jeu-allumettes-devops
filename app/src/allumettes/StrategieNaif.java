package allumettes;

import java.util.Random;

public class StrategieNaif implements Strategie {
    private static final Random random = new Random();

    @Override
    public int getPrise(Jeu jeu) {
        return 1 + random.nextInt(Jeu.PRISE_MAX); // Pour générer un nombre aléatoire entre 1 (inclus) et PRISE_MAX (inclus)
    }
}