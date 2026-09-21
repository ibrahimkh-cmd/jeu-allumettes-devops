package allumettes;


/** Lance une partie des 13 allumettes en fonction des arguments fournis
 * sur la ligne de commande.
 * @author	Xavier Crégut
 * @version	$Revision: 1.5 $
 */
public class Jouer {

	/** Lancer une partie. En argument sont donnés les deux joueurs sous
	 * la forme nom@stratégie.
	 * @param args la description des deux joueurs
	 */
	public static void main(String[] args) {
		try {
			verifierNombreArguments(args);

			int indiceJoueur1 = 0;
			boolean confiant = false;
			//savoir si l'arbitre est confiant
			if (args[0].equals("-confiant")) {
				confiant = true;
				indiceJoueur1 = 1;
			}

			//creation des joueurs
			Joueur joueur1 = creerJoueur(args[indiceJoueur1]); 
			Joueur joueur2 = creerJoueur(args[indiceJoueur1 + 1]); 		

			//creation du jeu 
			final int NOMBRE_ALLUMETTES = 13;
			Jeu jeu = new JeuImp(NOMBRE_ALLUMETTES);

			//creation de l'arbitre 
			Arbitre arbitre = new Arbitre(joueur1, joueur2);
			
			//demarrer la partie 
			arbitre.setConfiant(confiant);
			arbitre.arbitrer(jeu);
		} catch (ConfigurationException e) {
			System.out.println();
			System.out.println("Erreur : " + e.getMessage());
			afficherUsage();
			System.exit(1);
		}
	}

	/**
	 * creer un joueur a partir d un argument sous la forme nom@strategie .
	 * @param joueurStrategie
	 * @return joueur 
	 */
	public static Joueur creerJoueur(String joueurStrategie) {
		String [] arguments = joueurStrategie.split("@");  
		final int nbJoueurs = 2;
		if (arguments.length !=  nbJoueurs) {
			throw new ConfigurationException("Format de joueur incorrecte" + joueurStrategie );
		} 	
		String nom = arguments[0];
		String strategie = arguments[1];
		
		switch (strategie) {
			case "humain" :
				return new Joueur(nom, new StrategieHumain(nom));
			case "naif" :
				return new Joueur(nom, new StrategieNaif());
			case "tricheur" :
				return new Joueur(nom, new StrategieTricheur());
			case "rapide" :
				return new Joueur(nom, new StrategieRapide());
			case "expert" :
				return new Joueur(nom, new StrategieExpert());				
			default :
				throw new ConfigurationException("Strategie inconnue " +strategie);	
		} 
	} 

	private static void verifierNombreArguments(String[] args) {
		final int Nb_JOUEURS = 2;
		if (args.length < Nb_JOUEURS || args.length > Nb_JOUEURS + 1 ) {
			throw new ConfigurationException("Trop peu d'arguments : "
					+ args.length);
		}
	}
	/** Afficher des indications sur la manière d'exécuter cette classe. */
	public static void afficherUsage() {
		System.out.println("\n" + "Usage :"
				+ "\n\t" + "java allumettes.Jouer joueur1 joueur2"
				+ "\n\t\t" + "joueur est de la forme nom@stratégie"
				+ "\n\t\t" + "strategie = naif | rapide | expert | humain | tricheur"
				+ "\n"
				+ "\n\t" + "Exemple :"
				+ "\n\t" + "	java allumettes.Jouer Xavier@humain "
					   + "Ordinateur@naif"
				+ "\n"
				);
	}

}
