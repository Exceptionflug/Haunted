package de.exceptionflug.haunted.wave.config;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Object parseArgument(WaveConfigurationParser parser, int index, String argument, Structure type) throws ParseException {
        ParseException parseException = new ParseException("Unable to parse argument "+index, parser.file().getName(), parser.lineIndex() + 1);
        for (Structure structure : structures) {
            try {
                if (structure == Structure.DYNAMIC) {
                    return type.parser().parse(parser, index, argument);
                }
                return structure.parser().parse(parser, index, argument);
            } catch (Exception e) {
                parseException.addSuppressed(new ParseException("Parsing as "+structure.name()+" failed", e, parser.file().getName(), parser.lineIndex() + 1));
            }
        }
        throw parseException;
    }

    public boolean allowedStructure(Structure structure) {
        for (int i = 0; i < structures().length; i++) {
            if (structures[i] == structure) {
                return true;
            }
        }
        return false;
    }

    public Structure[] structures() {
        return structures;
    }
}
