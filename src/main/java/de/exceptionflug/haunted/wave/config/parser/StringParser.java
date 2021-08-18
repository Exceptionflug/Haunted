package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;

/**
 * Date: 16.08.2021
 *
 * @author Exceptionflug
 */
public class StringParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        if (!argument.startsWith("\"") || !argument.endsWith("\"")) {
            throw new ParseException("Argument "+index+": String must begin and end with quotation marks",
                    parser.file().getName(), parser.lineIndex() + 1);
        }
        return argument.substring(1, argument.length() - 1);
    }

}
