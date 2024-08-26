package com.spaceavocado.jillogical.kernel.operand.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Collection implements IEvaluable {
    private ArrayList<IEvaluable> items;
    private char escapeCharacter;
    private HashSet<String> escapedOperators;
    
    public Collection(IEvaluable[] items) {
        this(items, '\\', null);
    }
    public Collection(IEvaluable[] items, char escapeCharacter) {
        this(items, escapeCharacter, null);
    }
    public Collection(IEvaluable[] items, HashSet<String> escapedOperators) {
        this(items, '\\', escapedOperators);
    }
    public Collection(IEvaluable[] items, char escapeCharacter, HashSet<String> escapedOperators)
    {
        if (items.length < 1)
        {
            throw new IllegalArgumentException("collection operand must have at least 1 item");
        }

        this.items = new ArrayList<>(Arrays.asList(items));
        this.escapeCharacter = escapeCharacter;
        this.escapedOperators = escapedOperators != null ? escapedOperators : new HashSet<String>();
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);

        return items.stream()
            .map(item -> item.evaluate(flattenContext))
            .toArray();
    }

    @Override
    public Object serialize() {
        var head = items.get(0).serialize();
        if (shouldBeEscaped(head, escapedOperators)) {
            head = escapeOperator((String)head, escapeCharacter);
        }

        return Stream.concat(
            Arrays.stream(new Object[] { head }),
            items.subList(1, items.size()).stream().map(item -> item.serialize())
        ).toArray();
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);

        var res = new ArrayList<Object>();
        for (IEvaluable item : items) {
            var val = item.simplify(flattenContext);
            if (val instanceof IEvaluable) {
                return this;
            }

            res.add(val);
        }

        return res.toArray();
    }

    public static boolean shouldBeEscaped(Object subject, HashSet<String> escapedOperators)
    {
        if (subject == null) {
            return false;
        }

        return subject instanceof String && escapedOperators.contains(subject);
    }

    public static String escapeOperator(String op, char escapeOperator) {
        return String.format("%s%s", escapeOperator, op);
    }
    
    @Override
    public String toString() {
        return String.format(
            "[%s]",
            items.stream().map(Object::toString).collect(Collectors.joining(", "))
        );
    }
}
