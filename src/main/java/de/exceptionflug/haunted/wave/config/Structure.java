package de.exceptionflug.haunted.wave.config;

import de.exceptionflug.haunted.wave.config.parser.*;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public enum Structure {

    INTEGER(new IntegerParser()),
    DOUBLE(new DoubleParser()),
    BLOCK(null),
    JAVA_CLASS(new JavaClassParser()),
    LOCATION(new LocationParser()),
    SOUND(new SoundParser()),
    SPECIFIER((parser1, index, argument) -> argument);

    private final ArgumentParser parser;

    Structure(ArgumentParser parser) {
        this.parser = parser;
    }

}
