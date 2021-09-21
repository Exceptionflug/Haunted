package de.exceptionflug.haunted.wave.config;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class ParseException extends Exception {

    private final String fileName;
    private final int line;
    private boolean b0;

    public ParseException(String message, Exception cause, String fileName, int line) {
        super(message, cause);
        this.fileName = fileName;
        this.line = line;
    }

    public ParseException(String message, String fileName, int line) {
        super(message);
        this.fileName = fileName;
        this.line = line;
    }

    @Override
    public String getMessage() {
        if (b0) {
            return super.getMessage();
        }
        StringBuilder builder = new StringBuilder(super.getMessage() + " (" + fileName + ":" + line + ")\n");
        for (Throwable cause : getSuppressed()) {
            if (cause instanceof ParseException e) {
                ((ParseException) cause).b0 = true;
                builder.append("-> ").append(e.getMessage()).append("\n");
            } else {
                builder.append("-> ").append(cause).append("\n");
            }
            if (cause.getCause() != null) {
                examineCause(cause.getCause(), builder);
            }
        }
        builder.append("\n");
        builder.append("For more details, see the following stacktrace:");
        return builder.toString();
    }

    private void examineCause(Throwable cause, StringBuilder builder) {
        if (cause instanceof ParseException e) {
            e.b0 = true;
            builder.append("  - ").append(e.getMessage()).append("\n");
        } else {
            builder.append("  - ").append(cause).append("\n");
        }
        if (cause.getCause() != null) {
            examineCause(cause.getCause(), builder);
        }
    }

}
