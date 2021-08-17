package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;

/**
 * Date: 16.08.2021
 *
 * @author Exceptionflug
 */
public class StatementParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        if (!argument.startsWith("(") || !argument.endsWith(")")) {
            throw new ParseException("Argument "+index+": A statement must begin with open parentheses and must end with closing ones",
                    parser.file().getName(), parser.lineIndex() + 1);
        }
        argument = argument.substring(1, argument.length() - 1);
        return parser.nextStatement(argument);
    }

}
