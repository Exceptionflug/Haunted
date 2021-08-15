package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class JavaClassParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        try {
            return Class.forName(argument);
        } catch (ClassNotFoundException e) {
            throw new ParseException("Argument "+index+": Class "+argument+" not found", parser.file().getName(), parser.lineIndex() + 1);
        }
    }

}
