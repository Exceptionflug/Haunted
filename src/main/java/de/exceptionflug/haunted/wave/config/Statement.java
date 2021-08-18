package de.exceptionflug.haunted.wave.config;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.units.qual.A;

import java.util.Arrays;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class Statement {

    private final InstructionType type;
    private final Object[] arguments;

    public Statement(InstructionType type, Object... arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return type.name() + " " + Joiner.on(" ").join(arguments);
    }

    @Getter
    @Accessors(fluent = true)
    enum InstructionType {

        SPAWN(null, new Argument(Structure.JAVA_CLASS), new Argument(Structure.LOCATION, Structure.SPECIFIER)),
        TELEPORT(null, new Argument(Structure.LOCATION)),
        WAIT(null, new Argument(Structure.INTEGER, Structure.SPECIFIER, Structure.STATEMENT)),
        LOOP(null, new Argument(Structure.INTEGER, Structure.STATEMENT), new Argument(Structure.BLOCK)),
        CINEMATIC(null, new Argument(Structure.INTEGER, Structure.STATEMENT), new Argument(Structure.BLOCK)),
        VERTEX(null, new Argument(Structure.LOCATION)),
        TARGET(null, new Argument(Structure.LOCATION)),
        LOAD_INT(Structure.INTEGER, new Argument(Structure.SPECIFIER)),
        SOUND(null, new Argument(Structure.SOUND), new Argument(Structure.DOUBLE), new Argument(Structure.DOUBLE)),
        DECLARE(null, new Argument(Structure.STRUCTURE), new Argument(Structure.SPECIFIER)),
        DEFINE(null, new Argument(Structure.SPECIFIER), new Argument(Structure.DYNAMIC, Structure.STATEMENT)),
        IF(null, new Argument(Structure.BOOLEAN, Structure.STATEMENT), new Argument(Structure.BLOCK)),
        ECHO(null, new Argument(Structure.values())),

        // Returning things
        RANDOM_INT(Structure.INTEGER, new Argument(Structure.INTEGER, Structure.STATEMENT), new Argument(Structure.INTEGER, Structure.STATEMENT)),
        RANDOM_DOUBLE(Structure.DOUBLE, new Argument(Structure.DOUBLE, Structure.STATEMENT), new Argument(Structure.DOUBLE, Structure.STATEMENT)),
        COMPARE(Structure.BOOLEAN, new Argument(Structure.values()), new Argument(Structure.values())),
        CONCAT(Structure.STRING, new Argument(Structure.values()), new Argument(Structure.values())),

        // Maths
        ADD(null, new Argument(Structure.SPECIFIER), new Argument(Structure.DOUBLE, Structure.INTEGER, Structure.STATEMENT)),
        SUBTRACT(null, new Argument(Structure.SPECIFIER), new Argument(Structure.DOUBLE, Structure.INTEGER, Structure.STATEMENT)),
        MULTIPLY(null, new Argument(Structure.SPECIFIER), new Argument(Structure.DOUBLE, Structure.INTEGER, Structure.STATEMENT)),
        DIVIDE(null, new Argument(Structure.SPECIFIER), new Argument(Structure.DOUBLE, Structure.INTEGER, Structure.STATEMENT)),
        MODULO(null, new Argument(Structure.SPECIFIER), new Argument(Structure.DOUBLE, Structure.INTEGER, Structure.STATEMENT)),

        // Logic
        NEGATE(Structure.BOOLEAN, new Argument(Structure.BOOLEAN, Structure.STATEMENT)),
        AND(Structure.BOOLEAN, new Argument(Structure.BOOLEAN, Structure.STATEMENT), new Argument(Structure.BOOLEAN, Structure.STATEMENT)),
        OR(Structure.BOOLEAN, new Argument(Structure.BOOLEAN, Structure.STATEMENT), new Argument(Structure.BOOLEAN, Structure.STATEMENT)),

        END(null);

        private final Structure returnType;
        private final Argument[] arguments;

        InstructionType(Structure returnType, Argument... arguments) {
            this.returnType = returnType;
            this.arguments = arguments;
        }

        public Structure returnType() {
            return returnType;
        }
    }

}
