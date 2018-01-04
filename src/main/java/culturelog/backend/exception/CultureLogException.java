package culturelog.backend.exception;

/**
 * @author Jan Venstermans
 */
public class CultureLogException extends Exception {

    private final CultureLogExceptionKey key;
    private final Object[] objects;

    public CultureLogException(CultureLogExceptionKey key) {
        this(key, null);
    }

    public CultureLogException(CultureLogExceptionKey key, Object[] objects) {
        super(key.getKey());
        this.key = key;
        this.objects = objects;
    }

    public CultureLogExceptionKey getKey() {
        return key;
    }

    public Object[] getObjects() {
        return objects;
    }
}