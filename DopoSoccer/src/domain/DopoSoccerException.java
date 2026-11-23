package domain;

/**
 * Represents DopoSoccerException
 */
public class DopoSoccerException extends Exception{

    public static final String NOT_ENOUGH_POINTS = "No tienes suficientes puntos para la mejora seleccionada.";
    public static final String SLOWER_OR_EQUALS_TO_ZERO = "El tiempo o goles deben ser mayores a cero.";
    public static final String MAX_LEVEL_REACHED = "Esta estadística ya alcanzó su nivel máximo.";
    public static final String SAVE_ERROR = "No se pudo guardar la partida en el archivo seleccionado.";
    public static final String OPEN_ERROR = "No se pudo abrir la partida. Verifica que el archivo sea válido.";

    /**
     * Creates a new DopoSoccerException
     */
    public DopoSoccerException(String message){
        super(message);
    }

}