package pe.utp.proyectofinal.exception;

public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message) {
        this("business_error", message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
