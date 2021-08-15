package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;
import org.bukkit.Location;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class LocationParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        if (!argument.startsWith("[")) {
            throw new ParseException("Argument "+index+": Must start with open brackets", parser.file().getName(), parser.lineIndex() + 1);
        }
        if (!argument.endsWith("]")) {
            throw new ParseException("Argument "+index+": Must end with closed brackets", parser.file().getName(), parser.lineIndex() + 1);
        }
        argument = argument.substring(1, argument.length() - 1);
        String[] split = argument.split(",");
        if (split.length < 3) {
            throw new ParseException("Argument "+index+": At least 3 with comma seperated coordinates expected", parser.file().getName(), parser.lineIndex() + 1);
        }
        double x;
        double y;
        double z;
        float yaw = 0;
        float pitch = 0;
        try {
            x = Double.parseDouble(split[0]);
        } catch (NumberFormatException e) {
            throw new ParseException("Argument "+index+": X coordinate must be a double", parser.file().getName(), parser.lineIndex() + 1);
        }
        try {
            y = Double.parseDouble(split[1]);
        } catch (NumberFormatException e) {
            throw new ParseException("Argument "+index+": Y coordinate must be a double", parser.file().getName(), parser.lineIndex() + 1);
        }
        try {
            z = Double.parseDouble(split[2]);
        } catch (NumberFormatException e) {
            throw new ParseException("Argument "+index+": Z coordinate must be a double", parser.file().getName(), parser.lineIndex() + 1);
        }
        if (split.length == 5) {
            try {
                yaw = Float.parseFloat(split[3]);
            } catch (NumberFormatException e) {
                throw new ParseException("Argument "+index+": yaw must be a float", parser.file().getName(), parser.lineIndex() + 1);
            }
            try {
                pitch = Float.parseFloat(split[4]);
            } catch (NumberFormatException e) {
                throw new ParseException("Argument "+index+": pitch must be a float", parser.file().getName(), parser.lineIndex() + 1);
            }
        }
        return new Location(parser.context().currentMap().spectatorSpawn().getWorld(), x, y, z, yaw, pitch);
    }

}
