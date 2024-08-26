# (java)illogical

A micro conditional engine used to parse the logical and comparison expressions, evaluate an expression in data context, and provide access to a text form of the given expression.

> Revision: Aug 26, 2024.

Other implementations:
- [TS/JS](https://github.com/spaceavocado/illogical)
- [GO](https://github.com/spaceavocado/goillogical)
- [Python](https://github.com/spaceavocado/pyillogical)
- [C#](https://github.com/spaceavocado/cillogical)

## About

This project has been developed to provide C// implementation of [spaceavocado/illogical](https://github.com/spaceavocado/illogical).


**Table of Content**

---

- [(java)illogical](#javaillogical)
  - [About](#about)
- [Basic Usage](#basic-usage)
  - [Evaluate](#evaluate)
  - [Statement](#statement)
  - [Parse](#parse)
  - [IEvaluable](#ievaluable)
    - [Simplify](#simplify)
    - [Serialize](#serialize)
- [Working with Expressions](#working-with-expressions)
  - [Evaluation Data Context](#evaluation-data-context)
    - [Accessing Array Element:](#accessing-array-element)
    - [Accessing Array Element via Reference:](#accessing-array-element-via-reference)
    - [Nested Referencing](#nested-referencing)
    - [Composite Reference Key](#composite-reference-key)
    - [Data Type Casting](#data-type-casting)
  - [Operand Types](#operand-types)
    - [Value](#value)
    - [Reference](#reference)
    - [Collection](#collection)
  - [Comparison Expressions](#comparison-expressions)
    - [Equal](#equal)
    - [Not Equal](#not-equal)
    - [Greater Than](#greater-than)
    - [Greater Than or Equal](#greater-than-or-equal)
    - [Less Than](#less-than)
    - [Less Than or Equal](#less-than-or-equal)
    - [In](#in)
    - [Not In](#not-in)
    - [Prefix](#prefix)
    - [Suffix](#suffix)
    - [Overlap](#overlap)
    - [None](#none)
    - [Present](#present)
  - [Logical Expressions](#logical-expressions)
    - [And](#and)
    - [Or](#or)
    - [Nor](#nor)
    - [Xor](#xor)
    - [Not](#not)
- [Engine Options](#engine-options)
  - [Reference Serialize Options](#reference-serialize-options)
    - [From](#from)
    - [To](#to)
  - [Collection Serialize Options](#collection-serialize-options)
    - [Escape Character](#escape-character)
  - [Simplify Options](#simplify-options)
    - [Ignored Paths](#ignored-paths)
    - [Ignored Paths RegEx](#ignored-paths-regex)
  - [Operator Mapping](#operator-mapping)
- [Contributing](#contributing)
- [License](#license)

---


# Basic Usage

```java
import com.spaceavocado.Illogical;

// Create a new instance of the engine
var illogical = new Illogical();

// Evaluate an expression
illogical.evaluate(new Object[] { "==", 1, 1 }, null);
```

> For advanced usage, please [Engine Options](#engine-options).

## Evaluate

Evaluate comparison or logical expression:

`illogical.Evaluate(`[Comparison Expression](#comparison-expressions) or [Logical Expression](#logical-expressions), [Evaluation Data Context](#evaluation-data-context)`)` => `bool`

**Example**

```java
var context = new HashMap<String, Object>();
context.put("name", "peter");

// Comparison expression
illogical.evaluate(new Object[]{"==", 5, 5}, context);
illogical.evaluate(new Object[]{"==", "circle", "circle"}, context);
illogical.evaluate(new Object[]{"==", true, true }, context);
illogical.evaluate(new Object[]{"==", "$name", "peter"}, context);
illogical.evaluate(new Object[]{"NULL", "$RefA"}, context);

// Logical expression
illogical.evaluate(new Object[] {
    "AND",
    new Object[] { "==", 5, 5 },
    new Object[] { "==", 10, 10 }
}, context);

illogical.evaluate(new Object[] {
    "AND",
    new Object[] { "==", "circle", "circle" },
    new Object[] { "==", 10, 10 }
}, context);

illogical.evaluate(new Object[] {
    "OR",
    new Object[] { "==", "$name", "peter" },
    new Object[] { "==", 5, 10 }
}, context);
```

## Statement

Get expression string representation:

`illogical.Statement(`[Comparison Expression](#comparison-expressions) or [Logical Expression](#logical-expressions)`)` => `str`

**Example**

```java
// Comparison expression

illogical.statement(new Object[] { "==", 5, 5 }); // (5 == 5)
illogical.statement(new Object[] { "==", "circle", "circle" }); // ("circle" == "circle")
illogical.statement(new Object[] { "==", true, true }); // (True == True)
illogical.statement(new Object[] { "==", "$name", "peter" }); // ({name} == "peter")
illogical.statement(new Object[] { "NONE", "$RefA" }); // ({RefA} <is none>)

// Logical expression

illogical.statement(new Object[] {
    "AND",
    new Object[] { "==", 5, 5 },
    new Object[] { "==", 10, 10 }
}); // ((5 == 5) AND (10 == 10))

illogical.statement(new Object[] {
    "AND",
    new Object[] { "==", "circle", "circle" },
    new Object[] { "==", 10, 10 }
}); // (("circle" == "circle") AND (10 == 10))

illogical.statement(new Object[] {
    "OR",
    new Object[] { "==", "$name", "peter" },
    new Object[] { "==", 5, 10 }
}); // (({name} == "peter") OR (5 == 10))
```

## Parse

Parse the expression into a **IEvaluable** object, i.e. it returns the parsed self-evaluable condition expression.

`illogical.parse(`[Comparison Expression](#comparison-expressions) or [Logical Expression](#logical-expressions)`)` => `IEvaluable`

## IEvaluable

- `evaluable.Evaluate(context)` please see [Evaluation Data Context](#evaluation-data-context).
- `evaluable.Simplify(context)` please see [Simplify](#simplify).
- `evaluable.Serialize()` please see [Serialize](#serialize).
- `$"{evaluable}" | evaluable.ToString()` please see [Statement](#statement).

**Example**

```java
var evaluable = illogical.parse(new Object[] { "==", "$name", "peter" });

var context = new HashMap<String, Object>();
context.put("name", "peter");

evaluable.evaluate(context); // true

System.out.println(evaluable); // ({name} == "peter")
```

### Simplify

Simplifies an expression with a given context. This is useful when you already have some of
the properties of context and wants to try to evaluate the expression.

**Example**

```java
var evaluable = illogical.parse(new Object[] {
  "AND",
  new Object[] { "==", "$a", 10 },
  new Object[] { "==", "$b", 20 }
});

var context = new HashMap<String, Object>();
context.put("a", 10);
evaluable.simplify(context); // ({b} == 20)

var context = new HashMap<String, Object>();
context.put("a", 20);
evaluable.simplify(context); // false
```

Values not found in the context will cause the parent operand not to be evaluated and returned
as part of the simplified expression.

In some situations we might want to evaluate the expression even if referred value is not
present. You can provide a list of keys that will be strictly evaluated even if they are not
present in the context.

**Example**

```java
import com.spaceavocado.jillogical.kernel.operand.reference.ISimplifyOptions;
import java.util.regex.Pattern;

var simplifyOptions = new ISimplifyOptions() {
    @Override
    public String[] ignoredPaths() {
        return new String[] {};
    }

    @Override
    public Pattern[] ignoredPathsRx() {
        return new Pattern[] { Pattern.compile("^ignored") };
    }
};

var illogical = new Illogical(simplifyOptions);

var evaluable = illogical.parse(new Object[] {
    "AND",
    new Object[] { "==", "$a", 10 },
    new Object[] { "==", "$ignored", 20 }
});

var context = new HashMap<String, Object>();
context.put("a", 10);

evaluable.simplify(new Dictionary<string, object?> { { "a", 10 } }); // false
// $ignored" will be evaluated to null.
```

Alternatively we might want to do the opposite and strictly evaluate the expression for all referred
values not present in the context except for a specified list of optional keys.

**Example**

```java
import com.spaceavocado.jillogical.kernel.operand.reference.ISimplifyOptions;
import java.util.regex.Pattern;

var simplifyOptions = new ISimplifyOptions() {
    @Override
    public String[] ignoredPaths() {
        return new String[] { "ignored" };
    }

    @Override
    public Pattern[] ignoredPathsRx() {
        return new Pattern[] { };
    }
};

var illogical = new Illogical(simplifyOptions);

var evaluable = illogical.parse(new Object[] {
    "OR",
    new Object[] { "==", "$a", 10 },
    new Object[] { "==", "$b", 20 },
    new Object[] { "==", "$c", 20 }
});

var context = new HashMap<String, Object>();
context.put("a", 10);

evaluable.simplify(context); // ({a} == 10)
// except for "$b" everything not in context will be evaluated to null.
```

### Serialize

Serializes an expression into the raw expression form, reverse the parse operation.

**Example**

```java
evaluable = illogical.parse(new Object[] {
  "AND",
  new Object[] { "==", "$a", 10 },
  new Object[] { "==", 10, 20}
});

evaluable.serialize()
// new Object[] { "AND", new Object[] { "==", "$a", 10 }, new Object[] { "==", 10, 20 } }
```

# Working with Expressions

## Evaluation Data Context

The evaluation data context is used to provide the expression with variable references, i.e. this allows for the dynamic expressions. The data context is object with properties used as the references keys, and its values as reference values.

> Valid reference values: Dictionary, string, char, int, float, decimal, double, array of (bool, string, char, int, float).

To reference the nested reference, please use "." delimiter, e.g.:
`$address.city`

### Accessing Array Element:

`$options[1]`

### Accessing Array Element via Reference:

`$options[{index}]`

- The **index** reference is resolved within the data context as an array index.

### Nested Referencing

`$address.{segment}`

- The **segment** reference is resolved within the data context as a property key.

### Composite Reference Key

`$shape{shapeType}`

- The **shapeType** reference is resolved within the data context, and inserted into the outer reference key.
- E.g. **shapeType** is resolved as "**B**" and would compose the **$shapeB** outer reference.
- This resolution could be n-nested.

### Data Type Casting

`$payment.amount.(Type)`

Cast the given data context into the desired data type before being used as an operand in the evaluation.

> Note: If the conversion is invalid, then a warning message is being logged.

Supported data type conversions:

- .(String): cast a given reference to String.
- .(Number): cast a given reference to Number.
- .(Integer): cast a given reference to Integer.
- .(Float): cast a given reference to Float.
- .(Boolean): cast a given reference to Boolean.

**Example**

```java
// Data context
var context = new HashMap<String, Object>();
context.put("name", "peter");
context.put("country", "canada");
context.put("age", 21);
context.put("options", new Object[]{ 1, 2, 3 });

var address = new HashMap<String, Object>();
address.put("city", "Toronto");
address.put("country", "Canada");
context.put("address", address);

context.put("index", 2);
context.put("segment", "city");
context.put("shapeA", "box");
context.put("shapeB", "circle");
context.put("shapeType", "B");

// Evaluate an expression in the given data context

illogical.evaluate(new Object[] { ">", "$age", 20 }, context); // true
illogical.evaluate(new Object[] { "==", "$address.city", "Toronto" }, context); // true

// Accessing Array Element
illogical.evaluate(new Object[] { "==", "$options[1]", 2 }, context); // true

// Accessing Array Element via Reference
illogical.evaluate(new Object[] { "==", "$options[{index}]", 3 }, context); // true

// Nested Referencing
illogical.evaluate(new Object[] { "==", "$address.{segment}", "Toronto" }, context); // true

// Composite Reference Key
illogical.evaluate(new Object[] { "==", "$shape{shapeType}", "circle" }, context); // true

// Data Type Casting
illogical.evaluate(new Object[] { "==", "$age.(String)", "21" }, context); // true
```

## Operand Types

The [Comparison Expression](#comparison-expression) expect operands to be one of the below:

### Value

Simple value types: string, char, int, float, decimal, double, bool, null.

**Example**

```java
var val1 = 5;
var var2 = "cirle";
var var3 = true;

illogical.parse(new Object[] {
    "AND",
    new Object[] { "==", val1, var2 },
    new Object[] { "==", var3, var3 }
});
```

### Reference

The reference operand value is resolved from the [Evaluation Data Context](#evaluation-data-context), where the the operands name is used as key in the context.

The reference operand must be prefixed with `$` symbol, e.g.: `$name`. This might be customized via [Reference Predicate Parser Option](#reference-predicate).

**Example**

| Expression                    | Data Context      |
| ----------------------------- | ----------------- |
| `["==", "$age", 21]`          | `{age: 21}`       |
| `["==", "circle", "$shape"] ` | `{shape: "circle"}` |
| `["==", "$visible", true]`    | `{visible: true}` |

### Collection

The operand could be an array mixed from [Value](#value) and [Reference](#reference).

**Example**

| Expression                               | Data Context                        |
| ---------------------------------------- | ----------------------------------- |
| `["IN", [1, 2], 1]`                      | `null`                                |
| `["IN", "circle", ["$shapeA", "$shapeB"] ` | `{shapeA: "circle", shapeB: "box"}` |
| `["IN", ["$number", 5], 5]`                | `{number: 3}`                       |

## Comparison Expressions

### Equal

Expression format: `["==", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: string, char, int, float, decimal, double, bool, null.

```json
["==", 5, 5]
```

```java
illogical.evaluate(new Object[] { "==", 5, 5 }, context); // true
```

### Not Equal

Expression format: `["!=", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: string, char, int, float, decimal, double, bool, null.

```json
["!=", "circle", "square"]
```

```java
illogical.evaluate(new Object[] { "!=", "circle", "square" }, context); // true
```

### Greater Than

Expression format: `[">", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: int, float.

```json
[">", 10, 5]
```

```java
illogical.evaluate(new Object[] { ">", 10, 5 }, context); // true
```

### Greater Than or Equal

Expression format: `[">=", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: int, float.

```json
[">=", 5, 5]
```

```java
illogical.evaluate(new Object[] { ">=", 5, 5 }, context); // true
```

### Less Than

Expression format: `["<", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: int, float.

```json
["<", 5, 10]
```

```java
illogical.evaluate(new Object[] { "<", 5, 10 }, context); // true
```

### Less Than or Equal

Expression format: `["<=", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: int, float.

```json
["<=", 5, 5]
```

```java
illogical.evaluate(new Object[] { "<=", 5, 5 }, context); // true
```

### In

Expression format: `["IN", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: string, char, int, float, decimal, double, bool, null and an array of (string, char, int, float, decimal, double, bool, null).

```json
["IN", 5, [1, 2, 3, 4, 5]]
["IN", ["circle", "square", "triangle"], "square"]
```

```java
illogical.evaluate(new Object[] {
    "IN", 5, new Object[] { 1, 2, 3, 4, 5 }
}, context); // true

illogical.evaluate(new Object[] {
    "IN", new Object[] { "circle", "square", "triangle" }, "square" },
context); // true
```

### Not In

Expression format: `["NOT IN", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: string, char, int, float, decimal, double, bool, null and array of (string, char, int, float, decimal, double, bool, null).

```json
["IN", 10, [1, 2, 3, 4, 5]]
["IN", ["circle", "square", "triangle"], "oval"]
```

```java
illogical.evaluate(new Object[] {
    "NOT IN", 10, new Object[] { 1, 2, 3, 4, 5 }
}, context); // true

illogical.evaluate(new Object[] {
    "NOT IN", new Object[] { "circle", "square", "triangle" }, "oval" },
context); // true
```

### Prefix

Expression format: `["PREFIX", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: string.

- Left operand is the PREFIX term.
- Right operand is the tested word.

```json
["PREFIX", "hemi", "hemisphere"]
```

```java
illogical.evaluate(new Object[] { "PREFIX", "hemi", "hemisphere" }, context) // true
illogical.evaluate(new Object[] { "PREFIX", "hemi", "sphere" }, context) // false
```

### Suffix

Expression format: `["SUFFIX", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: string.

- Left operand is the tested word.
- Right operand is the SUFFIX term.

```json
["SUFFIX", "establishment", "ment"]
```

```java
illogical.evaluate(new Object[] { "SUFFIX", "establishment", "ment" }, context) // true
illogical.evaluate(new Object[] { "SUFFIX", "establish", "ment" }, context) // false
```

### Overlap

Expression format: `["OVERLAP", `[Left Operand](#operand-types), [Right Operand](#operand-types)`]`.

> Valid operand types: list; set; tuple of (string, char, int, float, decimal, double, bool, null).

```json
["OVERLAP", [1, 2], [1, 2, 3, 4, 5]]
["OVERLAP", ["circle", "square", "triangle"], ["square"]]
```

```java
illogical.evaluate(new Object[] {
    "OVERLAP", new Object[] { 1, 2, 6 }, new Object[] { 1, 2, 3, 4, 5 }
}, context); // true

illogical.evaluate(new Object[] {
    "OVERLAP", new Object[] {"circle", "square", "triangle" }, new Object[] {"square", "oval" }
}, context); // true
```

### None

Expression format: `["NONE", `[Reference Operand](#reference)`]`.

```json
["NONE", "$RefA"]
```

```java
illogical.evaluate(new Object[] { "NONE", "RefA" }, null); // true

var context = new HashMap<String, Object>();
context.put("RefA", 10);
illogical.evaluate(new Object[] { "NONE", "RefA" }, context); // false
```

### Present

Evaluates as FALSE when the operand is UNDEFINED or NULL.

Expression format: `["PRESENT", `[Reference Operand](#reference)`]`.

```json
["PRESENT", "$RefA"]
```

```java
illogical.evaluate(new Object[] { "PRESENT", "RefA" }, null); // false

var context = new HashMap<String, Object>();
context.put("RefA", 10);
illogical.evaluate(new Object[] { "PRESENT", "RefA" }, context); // true

var context = new HashMap<String, Object>();
context.put("RefA", false);
illogical.evaluate(new Object[] { "PRESENT", "RefA" }, context); // true

var context = new HashMap<String, Object>();
context.put("RefA", "val");
illogical.evaluate(new Object[] { "PRESENT", "RefA" }, context); // true
```

## Logical Expressions

### And

The logical AND operator returns the bool value TRUE if both operands are TRUE and returns FALSE otherwise.

Expression format: `["AND", Left Operand 1, Right Operand 2, ... , Right Operand N]`.

> Valid operand types: [Comparison Expression](#comparison-expressions) or [Nested Logical Expression](#logical-expressions).

```json
["AND", ["==", 5, 5], ["==", 10, 10]]
```

```java
illogical.evaluate(new object[] {
    "AND",
    new Object[] { "==", 5, 5 },
    new Object[] { "==", 10, 10 }
}, context); // true
```

### Or

The logical OR operator returns the bool value TRUE if either or both operands is TRUE and returns FALSE otherwise.

Expression format: `["OR", Left Operand 1, Right Operand 2, ... , Right Operand N]`.

> Valid operand types: [Comparison Expression](#comparison-expressions) or [Nested Logical Expression](#logical-expressions).

```json
["OR", ["==", 5, 5], ["==", 10, 5]]
```

```java
illogical.evaluate(new Object[] {
    "OR",
    new Object[] { "==", 5, 5 },
    new Object[] { "==", 10, 5 }
}, context); // true
```

### Nor

The logical NOR operator returns the bool value TRUE if both operands are FALSE and returns FALSE otherwise.

Expression format: `["NOR", Left Operand 1, Right Operand 2, ... , Right Operand N]`

> Valid operand types: [Comparison Expression](#comparison-expressions) or [Nested Logical Expression](#logical-expressions).

```json
["NOR", ["==", 5, 1], ["==", 10, 5]]
```

```java
illogical.evaluate(new Object[] {
    "NOR",
    new Object[] { "==", 5, 1 },
    new Object[] { "==", 10, 5 }
}, context); // true
```

### Xor

The logical NOR operator returns the bool value TRUE if both operands are FALSE and returns FALSE otherwise.

Expression format: `["XOR", Left Operand 1, Right Operand 2, ... , Right Operand N]`

> Valid operand types: [Comparison Expression](#comparison-expressions) or [Nested Logical Expression](#logical-expressions).

```json
["XOR", ["==", 5, 5], ["==", 10, 5]]
```

```java
illogical.evaluate(new Object[] {
    "XOR",
    new Object[] { "==", 5, 5 },
    new Object[] { "==", 10, 5 }
}, context); // true
```

```json
["XOR", ["==", 5, 5], ["==", 10, 10]]
```

```java
illogical.evaluate(new Object[] {
    "XOR",
    new Object[] { "==", 5, 5 },
    new Object[] { "==", 10, 10 }
}, context); // false
```

### Not

The logical NOT operator returns the bool value TRUE if the operand is FALSE, TRUE otherwise.

Expression format: `["NOT", Operand]`

> Valid operand types: [Comparison Expression](#comparison-expressions) or [Nested Logical Expression](#logical-expressions).

```json
["NOT", ["==", 5, 5]]
```

```java
illogical.evaluate(new Object[] { "NOT", new Object[] { "==", 5, 5 } }, context); // true
```

# Engine Options

## Reference Serialize Options

**Usage**

```java
import com.spaceavocado.jillogical.kernel.operand.reference.ISerializeOptions;

var serializeOptions = new ISerializeOptions() {
    @Override
    public String from(String operand) {
        return operand.length() > 2 && operand.startsWith("__")
            ? operand.substring(2)
            : null;
    }

    @Override
    public String to(String operand) {
        return String.format("__%s", operand);
    }
};

var illogical = new Illogical(serializeOptions);
```

### From

A function used to determine if the operand is a reference type, if so, return a raw value operand value

**Return value:**

- `string` = reference type
- `null` = value type

**Default reference predicate:**

> The `$` symbol at the begging of the operand is used to predicate the reference type., E.g. `$State`, `$Country`.

### To

A function used to transform the operand into the reference annotation stripped form. I.e. remove any annotation used to detect the reference type. E.g. "$Reference" => "Reference".

> **Default reference transform:**
> It removes the `$` symbol at the begging of the operand name.

## Collection Serialize Options

**Usage**

```java
var illogical = new Illogical('*');
```

### Escape Character

Charter used to escape fist value within a collection, if the value contains operator value.

**Example**
- `["==", 1, 1]` // interpreted as EQ expression
- `["\==", 1, 1]` // interpreted as a collection

> **Default escape character:**
> `\`

## Simplify Options

Options applied while an expression is being simplified.

**Usage**

```java
import com.spaceavocado.jillogical.kernel.operand.reference.ISimplifyOptions;
import java.util.regex.Pattern;

var simplifyOptions = new ISimplifyOptions() {
    @Override
    public String[] ignoredPaths() {
        return new String[] { "ignored" };
    }

    @Override
    public Pattern[] ignoredPathsRx() {
        return new Pattern[] { Pattern.compile("^ignored") };
    }
};

var illogical = new Illogical(simplifyOptions);
```

### Ignored Paths

Reference paths which should be ignored while simplification is applied. Must be an exact match.

### Ignored Paths RegEx

Reference paths which should be ignored while simplification is applied. Matching regular expression patterns.

## Operator Mapping

Mapping of the operators. The key is unique operator key, and the value is the key used to represent the given operator in the raw expression.

**Usage**

```java
import com.spaceavocado.jillogical.kernel.parser.Parser;

var operatorMapping = new HashMap<Operator, String>(Parser.DEFAULT_OPERATOR_MAPPING);
operatorMapping.put(Operator.EQ, "IS");

var illogical = new Illogical(operatorMapping);
```

**Default operator mapping:**

```java
var DEFAULT_OPERATOR_MAPPING = Map.ofEntries(
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
```

---

# Contributing

See [contributing.md](https://github.com/spaceavocado/pyillogical/blob/master/contributing.md).

# License

Illogical is released under the MIT license. See [license.md](https://github.com/spaceavocado/pyillogical/blob/master/license.md).