package allumettes;

public class StrategieExpert implements Strategie {
    
    @Override
    public int getPrise(Jeu jeu) {
        /**
         * on essai toujours que le joueur adverse se retrouve avec un nombre
         * d'allumettes qui vaut 1+n*(PRISE_MAX+1) allumettes.(avec n entier naturel)
         */
        int reste = (jeu.getNombreAllumettes() - 1) % (Jeu.PRISE_MAX + 1);
        
        if (reste != 0) {
            return reste;
        } else {
            return 1; //on retourne 1 par defaut en esperant que dans le prochain coup 
                      //on se retrouve dans le cas ideal(càd reste != 0 )   
        }
    }
}
