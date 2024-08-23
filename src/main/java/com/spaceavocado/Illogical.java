package com.spaceavocado;

import java.util.HashMap;
import java.util.Map;

import com.spaceavocado.kernel.FlattenContext;
import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.operand.reference.ISerializeOptions;
import com.spaceavocado.kernel.operand.reference.ISimplifyOptions;
import com.spaceavocado.kernel.parser.Operator;
import com.spaceavocado.kernel.parser.Parser;

public class Illogical {
    private Parser parser;

    public Illogical() {
        this(null, null, null, Parser.DEFAULT_ESCAPE_CHARACTER);
    }
    public Illogical(Map<Operator, String> operatorMapping) {
        this(operatorMapping, null, null, Parser.DEFAULT_ESCAPE_CHARACTER);
    }
    public Illogical(ISerializeOptions serializeOptions) {
        this(null, serializeOptions, null, Parser.DEFAULT_ESCAPE_CHARACTER);
    }
    public Illogical(ISimplifyOptions simplifyOptions) {
        this(null, null, simplifyOptions, Parser.DEFAULT_ESCAPE_CHARACTER);
    }
    public Illogical(char escapeCharacter) {
        this(null, null, null, escapeCharacter);
    }
    public Illogical(
        Map<Operator, String> operatorMapping,
        ISerializeOptions serializeOptions,
        ISimplifyOptions simplifyOptions,
        char escapeCharacter
    ) {
        this.parser = new Parser(operatorMapping, serializeOptions, simplifyOptions, escapeCharacter);
    }

    public IEvaluable parse(Object expression) {
        return parser.parse(expression);
    }

    public Object evaluate(Object expression, HashMap<String, Object> context) {
        return parser.parse(expression).evaluate(FlattenContext.create(context));
    }

    public Object simplify(Object expression, HashMap<String, Object> context) {
        return parser.parse(expression).simplify(FlattenContext.create(context));
    }

    public String statement(Object expression) {
        return parser.parse(expression).toString();
    }
}
