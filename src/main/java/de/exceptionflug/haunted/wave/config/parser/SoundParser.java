package de.exceptionflug.haunted.wave.config.parser;

import de.exceptionflug.haunted.wave.config.ArgumentParser;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;
import org.bukkit.Sound;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class SoundParser implements ArgumentParser {

    @Override
    public Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException {
        try {
            return Sound.valueOf(argument);
        } catch (IllegalArgumentException e) {
            throw new ParseException("Argument "+index+": No such sound "+argument, parser.file().getName(), parser.lineIndex() + 1);
        }
    }

}
