package allumettes;

import org.junit.*;

public class TestStrategieRapide {

    private StrategieRapide strategieRapide;
    private Jeu jeu;

    /** Initialisation des tests.*/
    @Before
    public void setUp() {
        strategieRapide = new StrategieRapide();
        jeu = new JeuImp(13);
    }

    /** Test de la méthode getPrise. */
    @Test
    public void testGetPrise() {
        Assert.assertEquals(3, strategieRapide.getPrise(jeu));
    }

    /** Test de la methode getPrise quand il reste moins de 3 allumettes. */
    @Test
    public void testGetPrise2() {
        jeu = new JeuImp(2);
        Assert.assertEquals(2, strategieRapide.getPrise(jeu));
    }
    @Test
    public void testGetPrise1() {
        jeu = new JeuImp(1);
        Assert.assertEquals(1, strategieRapide.getPrise(jeu));
    }

    /** Test de la méthode getPrise avec un jeu null. La méthode doit lever une
     * exception. */
    @Test(expected = AssertionError.class)
    public void testGetPriseNull() {
        strategieRapide.getPrise(null);
    }
}
