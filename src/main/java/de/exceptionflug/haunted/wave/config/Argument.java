package de.exceptionflug.haunted.wave.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class Argument {

    private final Structure[] structures;

    public Argument(Structure... structures) {
        this.structures = structures;
    }

    public Object parseArgument(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        ParseException parseException = new ParseException("Unable to parse argument "+index, parser.file().getName(), parser.lineIndex() + 1);
        for (Structure structure : structures) {
            try {
                return structure.parser().parse(parser, index, argument);
            } catch (Exception e) {
                parseException.addSuppressed(new ParseException("Parsing as "+structure.name()+" failed", e, parser.file().getName(), parser.lineIndex() + 1));
            }
        }
        throw parseException;
    }

}
