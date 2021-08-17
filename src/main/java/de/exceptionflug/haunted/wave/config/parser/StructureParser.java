package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.Structure;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;

/**
 * Date: 16.08.2021
 *
 * @author Exceptionflug
 */
public class StructureParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        try {
            return Structure.valueOf(argument);
        } catch (IllegalArgumentException e) {
            throw new ParseException("Argument "+index+": No such structure "+argument, parser.file().getName(), parser.lineIndex() + 1);
        }
    }

}
