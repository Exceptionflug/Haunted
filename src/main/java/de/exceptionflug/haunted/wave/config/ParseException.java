package de.exceptionflug.haunted.wave.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class ParseException extends Exception {

    private final String fileName;
    private final int line;

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
    public StackTraceElement[] getStackTrace() {
        List<StackTraceElement> stackTraceElements = new ArrayList<>();
        stackTraceElements.add(new StackTraceElement(fileName.substring(0, fileName.lastIndexOf(".")), "init", fileName, line));
        stackTraceElements.addAll(Arrays.asList(super.getStackTrace()));
        return stackTraceElements.toArray(new StackTraceElement[0]);
    }

}
