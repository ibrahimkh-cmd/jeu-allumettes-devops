package allumettes;

public class ProxyJeu implements Jeu {
    private Jeu jeuReel;

    public ProxyJeu(Jeu jeu) {
        this.jeuReel = jeu;
    }

    @Override
    public int getNombreAllumettes() {
        return jeuReel.getNombreAllumettes();
    }

    @Override
    public void retirer(int nbPrises) {
        // Le joueur ne peut pas utiliser la méthode retirer du proxy, donc si il essaye, 
        // on lève l'exception ci-dessous.
        throw new OperationInterditeException("Vous n'avez pas le droit de modifier le jeu.");
    }
}
