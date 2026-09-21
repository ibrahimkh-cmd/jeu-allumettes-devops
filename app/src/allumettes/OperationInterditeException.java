package allumettes;

/**
 * Cette exception sera levée si le joueur veut appeler la méthode
 * retirer de la procuration.
 */
public class OperationInterditeException extends RuntimeException {
    public OperationInterditeException(String message) {
        super(message);
}
}
