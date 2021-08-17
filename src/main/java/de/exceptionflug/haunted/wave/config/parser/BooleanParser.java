package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;

/**
 * Date: 16.08.2021
 *
 * @author Exceptionflug
 */
public class BooleanParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        if (!argument.equals("true") && !argument.equals("false")) {
            throw new ParseException("Argument "+index+": Only true and false are allowed values", parser.file().getName(), parser.lineIndex() + 1);
        }
        return Boolean.parseBoolean(argument);
    }

}
