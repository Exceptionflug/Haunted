package de.exceptionflug.haunted.wave.config;

import com.google.common.io.Files;
import de.exceptionflug.projectvenom.game.GameContext;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private final Map<String, Structure> declaredVariables = new HashMap<>();
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

    public Statement nextStatement() throws ParseException {
        if (lineIndex == lines.size() - 1) {
            return null;
        }
        lineIndex ++;
        String statement = lines.get(lineIndex).trim();
        return nextStatement(statement);
    }

    public Statement nextStatement(String statement) throws ParseException {
        if (statement.equals("}")) {
            throw new BlockEndSignal();
        }
        if (statement.isEmpty() || statement.startsWith("//")) {
            return nextStatement();
        }
        String[] split = statement.split(" ");
        Statement.InstructionType type;
        try {
            type = Statement.InstructionType.valueOf(split[0]);
        } catch (IllegalArgumentException exception) {
            throw new ParseException("Unexpected token '"+split[0]+"'", file.getName(), lineIndex + 1);
        }
        if (split.length > 1) {
            String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);
            Object[] arguments = parseArguments(type, args);
            if (type == Statement.InstructionType.DECLARE) {
                if (declaredVariables.containsKey((String) arguments[1])) {
                    throw new ParseException("Variable with name "+arguments[1]+" already declared", file.getName(), lineIndex + 1);
                }
                declaredVariables.put((String) arguments[1], (Structure) arguments[0]);
            }
            if (arguments.length != type.arguments().length) {
                throw new ParseException("Unexpected argument count for instruction "+type.name()+": Expected exactly "+type.arguments().length+" arguments but got "+arguments.length,
                        file.getName(), lineIndex + 1);
            }
            return new Statement(type, arguments);
        }
        if (type.arguments().length != 0) {
            throw new ParseException("Unexpected argument count for instruction "+type.name()+": Expected exactly "+type.arguments().length+" arguments but got 0",
                    file.getName(), lineIndex + 1);
        }
        return new Statement(type);
    }

    private Object[] parseArguments(Statement.InstructionType type, String[] args) throws ParseException {
        List<Object> out = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean string = false;
        int typeArgCounter = 0;
        Structure structure = null;
        for (int i = 0; i < args.length; i++) {
            String argument = args[i];
            if (string) {
                builder.append(" ").append(argument);
                if (argument.endsWith("\"")) {
                    string = false;
                    out.add(type.arguments()[typeArgCounter].parseArgument(this, typeArgCounter, builder.toString(), Structure.STRING));
                    typeArgCounter ++;
                }
                continue;
            }
            if (argument.startsWith("$")) {
                String variable = argument.substring(1);
                if (!declaredVariables.containsKey(variable)) {
                    throw new ParseException("Unknown variable "+variable, file.getName(), lineIndex + 1);
                }
                if (!type.arguments()[typeArgCounter].allowedStructure(declaredVariables.get(variable))) {
                    throw new ParseException("Variable type mismatch: Expected " +
                            Arrays.toString(type.arguments()[i].structures()) + " but got "+declaredVariables.get(variable).name(), file.getName(), lineIndex + 1);
                }
                out.add(new Variable(variable));
                typeArgCounter ++;
            } else if (argument.equals("{")) {
                out.add(readBlock());
                typeArgCounter ++;
            } else if (argument.startsWith("(")) {
                Map.Entry<Integer, Statement> entry = parseStatement(args, i, type, typeArgCounter, structure);
                i = entry.getKey();
                out.add(entry.getValue());
            } else if (argument.startsWith("\"")) {
                string = true;
                builder = new StringBuilder().append(argument);
            } else {
                if (type == Statement.InstructionType.DEFINE) {
                    if (type.arguments()[typeArgCounter].allowedStructure(Structure.SPECIFIER)) {
                        structure = declaredVariables.get(argument);
                    }
                }
                out.add(type.arguments()[typeArgCounter].parseArgument(this, typeArgCounter, argument, structure));
                typeArgCounter ++;
            }
        }
        return out.toArray();
    }

    private Map.Entry<Integer, Statement> parseStatement(String[] args, int beginIndex, Statement.InstructionType type, int typeArgCounter, Structure structure) throws ParseException {
        StringBuilder builder = new StringBuilder();
        int level = 0;
        for (int i = beginIndex; i < args.length; i++) {
            String argument = args[i];
            builder.append(" ").append(argument);
            if (argument.endsWith(")")) {
                level --;
                if (level == 0) {
                    Statement parsedStatement = (Statement) type.arguments()[typeArgCounter].parseArgument(this, typeArgCounter, builder.toString().trim(), Structure.STATEMENT);
                    Structure returnType = parsedStatement.type().returnType();
                    if (returnType == null) {
                        throw new ParseException("Statements with no return type cannot be used inline", file.getName(), lineIndex + 1);
                    }
                    if (structure != null) {
                        if (structure != returnType) {
                            throw new ParseException("Statement return type mismatch: Expected " +
                                    structure.name() + " but got "+ returnType.name(), file.getName(), lineIndex + 1);
                        }
                    } else {
                        if (!type.arguments()[typeArgCounter].allowedStructure(returnType)) {
                            throw new ParseException("Statement return type mismatch: Expected " +
                                    Arrays.toString(type.arguments()[typeArgCounter].structures()) + " but got "+ returnType.name(), file.getName(), lineIndex + 1);
                        }
                    }
                    return new AbstractMap.SimpleEntry<>(i, parsedStatement);
                }
            } else if (argument.startsWith("(")) {
                level ++;
            }
        }
        throw new ParseException("Statement was never closed", file.getName(), lineIndex + 1);
    }

    private CodeBlock readBlock() throws ParseException {
        CodeBlock block = new CodeBlock();
        while (!Thread.interrupted()) {
            try {
                block.statements.add(nextStatement());
            } catch (BlockEndSignal endSignal) {
                break;
            }
        }
        return block;
    }

    @Getter
    @Accessors(fluent = true)
    static class CodeBlock {

        private final List<Statement> statements = new ArrayList<>();

        @Override
        public String toString() {
            return "CodeBlock{" +
                    "statements=" + statements +
                    '}';
        }
    }


    @Getter
    @Accessors(fluent = true)
    static class Variable {

        private final String name;

        Variable(String name) {
            this.name = name;
        }
    }

    static class BlockEndSignal extends Error {

    }

}
