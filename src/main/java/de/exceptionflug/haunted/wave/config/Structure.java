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
    BOOLEAN(new BooleanParser()),
    JAVA_CLASS(new JavaClassParser()),
    LOCATION(new LocationParser()),
    SOUND(new SoundParser()),
    STATEMENT(new StatementParser()),
    STRUCTURE(new StructureParser()),
    STRING(new StringParser()),
    SPECIFIER((parser1, index, argument) -> argument),
    BLOCK(null),
    DYNAMIC(null);

    private final ArgumentParser parser;

    Structure(ArgumentParser parser) {
        this.parser = parser;
    }

    @Override
    public String toString() {
        return name();
    }
}
