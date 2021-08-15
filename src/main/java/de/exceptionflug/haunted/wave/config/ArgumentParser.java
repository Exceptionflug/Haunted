package de.exceptionflug.haunted.wave.config;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public interface ArgumentParser {

    Object parse(WaveConfigurationParser parser, int index, String argument) throws ParseException;

}
