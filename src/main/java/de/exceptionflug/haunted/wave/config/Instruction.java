package de.exceptionflug.haunted.wave.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.units.qual.A;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class Instruction {

    private final InstructionType type;
    private final Object[] arguments;

    public Instruction(InstructionType type, Object... arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    @Getter
    @Accessors(fluent = true)
    enum InstructionType {

        SPAWN(new Argument(Structure.JAVA_CLASS), new Argument(Structure.LOCATION, Structure.SPECIFIER)),
        TELEPORT(new Argument(Structure.LOCATION)),
        WAIT(new Argument(Structure.INTEGER, Structure.SPECIFIER)),
        LOOP(new Argument(Structure.INTEGER), new Argument(Structure.BLOCK)),
        CINEMATIC(new Argument(Structure.INTEGER), new Argument(Structure.BLOCK)),
        VERTEX(new Argument(Structure.LOCATION)),
        SOUND(new Argument(Structure.SOUND), new Argument(Structure.DOUBLE), new Argument(Structure.DOUBLE)),
        END();

        private final Argument[] arguments;

        InstructionType(Argument... arguments) {
            this.arguments = arguments;
        }
    }

}
