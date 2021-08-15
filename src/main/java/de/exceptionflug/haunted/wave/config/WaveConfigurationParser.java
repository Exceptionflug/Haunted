package de.exceptionflug.haunted.wave.config;

import com.google.common.io.Files;
import de.exceptionflug.projectvenom.game.GameContext;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class WaveConfigurationParser {

    private final GameContext context;
    private final File file;
    private final List<String> lines;
    private final int wave;
    private int lineIndex;

    public WaveConfigurationParser(GameContext context, File file) throws IOException, ParseException {
        this.context = context;
        this.file = file;
        lines = Files.readLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalStateException("This file is empty: "+file.getAbsolutePath());
        } else {
            String line0 = lines.get(0);
            if (!line0.startsWith("WAVE")) {
                throw new ParseException("Expected token WAVE at first line", file.getName(), 1);
            }
            String[] split = line0.split(" ");
            if (split.length != 2) {
                throw new ParseException("WAVE requires exactly one argument", file.getName(), 1);
            }
            try {
                this.wave = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new ParseException("Argument 0 of WAVE instruction is required to be an integer", file.getName(), 1);
            }
        }
    }

    public Instruction nextInstruction() throws ParseException {
        if (lineIndex == lines.size() - 1) {
            return null;
        }
        lineIndex ++;
        String statement = lines.get(lineIndex).trim();
        if (statement.equals("}")) {
            throw new BlockEndSignal();
        }
        if (statement.isEmpty() || statement.startsWith("//")) {
            return nextInstruction();
        }
        String[] split = statement.split(" ");
        Instruction.InstructionType type;
        try {
            type = Instruction.InstructionType.valueOf(split[0]);
        } catch (IllegalArgumentException exception) {
            throw new ParseException("Unexpected token '"+split[0]+"'", file.getName(), lineIndex + 1);
        }
        if (split.length-1 != type.arguments().length) {
            throw new ParseException("Unexpected argument count for instruction "+type.name()+": Expected exactly "+type.arguments().length+" arguments",
                    file.getName(), lineIndex + 1);
        }
        if (split.length > 1) {
            String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);
            Object[] arguments = parseArguments(type, args);
            return new Instruction(type, arguments);
        }
        return new Instruction(type);
    }

    private Object[] parseArguments(Instruction.InstructionType type, String[] args) throws ParseException {
        Object[] out = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            String argument = args[i];
            if (argument.equals("{")) {
                out[i] = readBlock();
            } else {
                out[i] = type.arguments()[i].parseArgument(this, i, argument);
            }
        }
        return out;
    }

    private CodeBlock readBlock() throws ParseException {
        CodeBlock block = new CodeBlock();
        while (!Thread.interrupted()) {
            try {
                block.instructions.add(nextInstruction());
            } catch (BlockEndSignal endSignal) {
                break;
            }
        }
        return block;
    }

    @Getter
    @Accessors(fluent = true)
    static class CodeBlock {

        private final List<Instruction> instructions = new ArrayList<>();

    }

    static class BlockEndSignal extends Error {

    }

}
