package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class IntegerParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new ParseException("Argument "+index+" must be an integer", parser.file().getName(), parser.lineIndex() + 1);
        }
    }

}
