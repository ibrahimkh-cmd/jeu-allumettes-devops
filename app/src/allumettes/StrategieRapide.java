package allumettes;

public class StrategieRapide implements Strategie {

    @Override
    public int getPrise(Jeu jeu) {
        assert jeu != null;
        return Math.min(jeu.getNombreAllumettes(), Jeu.PRISE_MAX);
    }
}
