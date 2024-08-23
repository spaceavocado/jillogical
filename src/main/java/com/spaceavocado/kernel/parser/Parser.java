package com.spaceavocado.kernel.parser;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Arrays;

import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.Primitive;
import com.spaceavocado.kernel.expression.comparison.Eq;
import com.spaceavocado.kernel.expression.comparison.Ge;
import com.spaceavocado.kernel.expression.comparison.Gt;
import com.spaceavocado.kernel.expression.comparison.In;
import com.spaceavocado.kernel.expression.comparison.Le;
import com.spaceavocado.kernel.expression.comparison.Lt;
import com.spaceavocado.kernel.expression.comparison.Ne;
import com.spaceavocado.kernel.expression.comparison.NotIn;
import com.spaceavocado.kernel.expression.comparison.Null;
import com.spaceavocado.kernel.expression.comparison.Overlap;
import com.spaceavocado.kernel.expression.comparison.Prefix;
import com.spaceavocado.kernel.expression.comparison.Present;
import com.spaceavocado.kernel.expression.comparison.Suffix;
import com.spaceavocado.kernel.expression.logical.And;
import com.spaceavocado.kernel.expression.logical.Nor;
import com.spaceavocado.kernel.expression.logical.Not;
import com.spaceavocado.kernel.expression.logical.Or;
import com.spaceavocado.kernel.expression.logical.Xor;
import com.spaceavocado.kernel.operand.reference.DefaultSerializeOptions;
import com.spaceavocado.kernel.operand.reference.ISerializeOptions;
import com.spaceavocado.kernel.operand.reference.ISimplifyOptions;
import com.spaceavocado.kernel.operand.reference.Reference;
import com.spaceavocado.kernel.operand.value.Value;
import com.spaceavocado.kernel.operand.collection.Collection;

  
public class Parser {
    public static Map<Operator, String> DEFAULT_OPERATOR_MAPPING = Map.ofEntries(
        // Logical
        entry(Operator.AND, "AND"),
        entry(Operator.OR, "OR"),
        entry(Operator.NOR, "NOR"),
        entry(Operator.XOR, "XOR"),
        entry(Operator.NOT, "NOT"),
        // Comparison
        entry(Operator.EQ, "=="),
        entry(Operator.NE, "!="),
        entry(Operator.GT, ">"),
        entry(Operator.GE, ">="),
        entry(Operator.LT, "<"),
        entry(Operator.LE, "<="),
        entry(Operator.NONE, "NONE"),
        entry(Operator.PRESENT, "PRESENT"),
        entry(Operator.IN, "IN"),
        entry(Operator.NOTIN, "NOT IN"),
        entry(Operator.OVERLAP, "OVERLAP"),
        entry(Operator.PREFIX, "PREFIX"),
        entry(Operator.SUFFIX, "SUFFIX")
    );

    public static char DEFAULT_ESCAPE_CHARACTER = '\\';

    private Map<Operator, String> operatorMapping;
    private Map<String, IExpression> operatorHandlerMapping;
    private ISerializeOptions serializeOptions;
    private ISimplifyOptions simplifyOptions;
    private char escapeCharacter;
    private HashSet<String> escapedOperators;

    public Parser() {
        this(null, null, null, DEFAULT_ESCAPE_CHARACTER);
    }
    public Parser(Map<Operator, String> operatorMapping) {
        this(operatorMapping, null, null, DEFAULT_ESCAPE_CHARACTER);
    }
    public Parser(ISerializeOptions serializeOptions) {
        this(null, serializeOptions, null, DEFAULT_ESCAPE_CHARACTER);
    }
    public Parser(ISimplifyOptions simplifyOptions) {
        this(null, null, simplifyOptions, DEFAULT_ESCAPE_CHARACTER);
    }
    public Parser(char escapeCharacter) {
        this(null, null, null, escapeCharacter);
    }
    public Parser(
        Map<Operator, String> operatorMapping,
        ISerializeOptions serializeOptions,
        ISimplifyOptions simplifyOptions,
        char escapeCharacter
    ) {
        this.serializeOptions = serializeOptions != null ? serializeOptions : new DefaultSerializeOptions();
        this.simplifyOptions = simplifyOptions;
        this.escapeCharacter = escapeCharacter;
        this.operatorMapping = operatorMapping != null ? operatorMapping : DEFAULT_OPERATOR_MAPPING;

        this.operatorHandlerMapping = Map.ofEntries(
            // Logical
            entry(operatorSymbol(Operator.AND), multiaryHandler(new IMultiaryExpression() {
                @Override
                public IEvaluable create(IEvaluable... operands) {
                    return new And(operands, operatorSymbol(Operator.AND));
                }
            })),
            entry(operatorSymbol(Operator.OR), multiaryHandler(new IMultiaryExpression() {
                @Override
                public IEvaluable create(IEvaluable... operands) {
                    return new Or(operands, operatorSymbol(Operator.OR));
                }
            })),
            entry(operatorSymbol(Operator.NOR), multiaryHandler(new IMultiaryExpression() {
                @Override
                public IEvaluable create(IEvaluable... operands) {
                    return new Nor(operands, operatorSymbol(Operator.NOR), operatorSymbol(Operator.NOT));
                }
            })),
            entry(operatorSymbol(Operator.XOR), multiaryHandler(new IMultiaryExpression() {
                @Override
                public IEvaluable create(IEvaluable... operands) {
                    return new Xor(operands, operatorSymbol(Operator.XOR), operatorSymbol(Operator.NOT), operatorSymbol(Operator.NOR));
                }
            })),
            entry(operatorSymbol(Operator.NOT), unaryHandler(new IUnaryExpression() {
                @Override
                public IEvaluable create(IEvaluable operand) {
                    return new Not(operand, operatorSymbol(Operator.NOT));
                }
            })),
            // Comparison
            entry(operatorSymbol(Operator.EQ), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Eq(left, right, operatorSymbol(Operator.EQ));
                }
            })),
            entry(operatorSymbol(Operator.NE), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Ne(left, right, operatorSymbol(Operator.NE));
                }
            })),
            entry(operatorSymbol(Operator.GT), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Gt(left, right, operatorSymbol(Operator.GT));
                }
            })),
            entry(operatorSymbol(Operator.GE), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Ge(left, right, operatorSymbol(Operator.GE));
                }
            })),
            entry(operatorSymbol(Operator.LT), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Lt(left, right, operatorSymbol(Operator.LT));
                }
            })),
            entry(operatorSymbol(Operator.LE), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Le(left, right, operatorSymbol(Operator.LE));
                }
            })),
            entry(operatorSymbol(Operator.NONE), unaryHandler(new IUnaryExpression() {
                @Override
                public IEvaluable create(IEvaluable operand) {
                    return new Null(operand, operatorSymbol(Operator.NONE));
                }
            })),
            entry(operatorSymbol(Operator.PRESENT), unaryHandler(new IUnaryExpression() {
                @Override
                public IEvaluable create(IEvaluable operand) {
                    return new Present(operand, operatorSymbol(Operator.PRESENT));
                }
            })),
            entry(operatorSymbol(Operator.IN), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new In(left, right, operatorSymbol(Operator.IN));
                }
            })),
            entry(operatorSymbol(Operator.NOTIN), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new NotIn(left, right, operatorSymbol(Operator.NOTIN));
                }
            })),
            entry(operatorSymbol(Operator.OVERLAP), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Overlap(left, right, operatorSymbol(Operator.OVERLAP));
                }
            })),
            entry(operatorSymbol(Operator.PREFIX), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Prefix(left, right, operatorSymbol(Operator.PREFIX));
                }
            })),
            entry(operatorSymbol(Operator.SUFFIX), binaryHandler(new IBinaryExpression() {
                @Override
                public IEvaluable create(IEvaluable left, IEvaluable right) {
                    return new Suffix(left, right, operatorSymbol(Operator.SUFFIX));
                }
            }))
        );

        this.escapedOperators = new HashSet<String>(this.operatorMapping.values());
    }

    private String operatorSymbol (Operator operator) {
        return this.operatorMapping.containsKey(operator)
            ? this.operatorMapping.get(operator)
            : DEFAULT_OPERATOR_MAPPING.get(operator);
    };

    private IExpression unaryHandler(IUnaryExpression handler) {
        return (IEvaluable[] operands) -> handler.create(operands[0]);
    }
    
    private IExpression binaryHandler(IBinaryExpression handler) {
        return (IEvaluable[] operands) -> handler.create(operands[0], operands[1]);
    }

    private IExpression multiaryHandler(IMultiaryExpression handler) {
        return (IEvaluable[] operands) -> handler.create(operands);
    }

    public boolean isEscaped(String value) {
        return value.startsWith(String.valueOf(escapeCharacter));
    }

    public String toReferenceAddress(Object reference) {
        return reference instanceof String ? serializeOptions.from((String)reference) : null;
    }

    public IEvaluable parse(Object input) {
        if (input == null) {
            throw new UnexpectedExpressionInputException("input cannot be null");
        };

        if (!(input instanceof Object[])) {
            return createOperand(input);
        }

        var expression = (Object[])input;

        if (expression.length < 2) {
            return createOperand(expression);
        }

        if (expression[0] instanceof String && isEscaped((String)expression[0])) {
            return createOperand(Stream.concat(
                Arrays.stream(new Object[] { ((String)expression[0]).substring(1) }),
                Arrays.stream(Arrays.copyOfRange(expression, 1, expression.length))
            ).toArray());
        }

        try {
            return createExpression(expression);
        }
        catch (UnexpectedExpressionException e) {
            return createOperand(expression);
        }
    }

    public IEvaluable createOperand(Object value)
    {
        if (value instanceof Object[] collection) {
            if (collection.length == 0) {
                throw new UnexpectedOperandException("collection operand must have items");
            }

            for (Object o : new ArrayList<>(Arrays.asList(value)).stream().toArray()) {
                System.out.println(o.getClass());
            }

            return new Collection(
                new ArrayList<>(Arrays.asList(collection)).stream().map((operand) -> {
                    return parse(operand);
                }).toArray(size -> new IEvaluable[size]),
                escapeCharacter,
                escapedOperators
            );
        }

        var address = toReferenceAddress(value);
        if (address != null) {
            return new Reference(address, serializeOptions, simplifyOptions);
        }

        if (value != null && !Primitive.isPrimitive(value)) {
            throw new UnexpectedOperandException("value operand must be a primitive value, number, text, bool and/or null");
        }

        return new Value(value);
    }

    public IEvaluable createExpression(Object[] expression)
    {
        var operator = expression[0];
        var operands = new ArrayList<>(Arrays.asList(expression)).subList(1, expression.length);

        if (!(operator instanceof String)) {
            throw new UnexpectedExpressionException(String.format("expression must have a valid operator, got %s", operator));
        }

        if (!operatorHandlerMapping.containsKey(operator)) {
            throw new UnexpectedExpressionException(String.format("missing expression handler for %s", operator));
        }

        return operatorHandlerMapping.get(operator).create(operands.stream().map((operand) -> parse(operand)).toArray(size -> new IEvaluable[size]));
    }
}
