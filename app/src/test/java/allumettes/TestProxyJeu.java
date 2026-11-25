package allumettes;

import org.junit.*;

public class TestProxyJeu {

    /** Le proxy doit refuser toute modification et laisser le vrai jeu intact. */
    @Test
    public void testRetirerInterdit() throws CoupInvalideException {
        Jeu jeuReel = new JeuImp(13);
        try {
            new ProxyJeu(jeuReel).retirer(1);
            Assert.fail("OperationInterditeException attendue");
        } catch (OperationInterditeException e) {
            Assert.assertEquals(13, jeuReel.getNombreAllumettes());
        }
    }
}
