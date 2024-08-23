package com.spaceavocado.kernel.operand.reference;

import java.util.HashMap;

import com.spaceavocado.kernel.FlattenContext;
import com.spaceavocado.kernel.IEvaluable;
import java.util.regex.Pattern;
import com.spaceavocado.kernel.utils.Triplet;

public class Reference implements IEvaluable {
    private String address;
    private String path;
    private DataType dataType;
    private ISerializeOptions serializeOptions;
    private ISimplifyOptions simplifyOptions;

    private final static String NESTED_REFERENCE_RX = "\\{([^\\{\\}]+)\\}";
    private final static String DATA_TYPE_RX = "^.+\\.\\(([A-Z][a-z]+)\\)$";
    private final static String DATA_TYPE_TRIM_RX = ".\\(([A-Z][a-z]+)\\)$";
    private final static String FLOAT_TRIM_RX = "\\.\\d+";
    private final static String FLOAT_RX = "^\\d+\\.\\d+$";
    private final static String INT_RX = "^0$|^[1-9]\\d*$";

    public Reference(String address) {
        this(address, null, null);
    }
    public Reference(String address, ISerializeOptions serializeOptions) {
        this(address, serializeOptions, null);
    }
    public Reference(String address, ISimplifyOptions simplifyOptions) {
        this(address, null, simplifyOptions);
    }
    public Reference(
        String address,
        ISerializeOptions serializeOptions,
        ISimplifyOptions simplifyOptions
    ) {
        var dataType = getDataType(address);
        if (dataType == DataType.Unsupported) {
            throw new IllegalArgumentException(String.format("unsupported type casting, %s", address));
        }

        this.address = address;
        this.path = trimDataType(address);
        this.dataType = dataType;
        this.serializeOptions = serializeOptions != null ? serializeOptions : new DefaultSerializeOptions();
        this.simplifyOptions = simplifyOptions != null ? simplifyOptions : new DefaultSimplifyOptions();
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        if (context == null) {
            return null;
        }

        context = FlattenContext.create(context);
        var lookout = evaluate(context, path, dataType);

        return lookout.c;
    }

    @Override
    public Object serialize() {
        return serializeOptions.To(
            dataType != DataType.Undefined
                ? String.format("%s.(%s)", path, dataType)
                : path
        );
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        if (context == null) {
            return this;
        }

        context = FlattenContext.create(context);
        var lookout = evaluate(context, path, dataType);
        var found = lookout.a;
        var resolvedPath = lookout.b;
        var value = lookout.c;

        if (found && !isIgnoredPath(resolvedPath, simplifyOptions.ignoredPaths(), simplifyOptions.ignoredPathsRx())) {
            return value;
        }

        return this;
    }

    public static Triplet<Boolean, String, Object> evaluate (HashMap<String, Object> context, String path, DataType dataType) {
        context = FlattenContext.create(context);
        var lookout = contextLookup(context, path);

        var found = lookout.a;
        var resolvedPath = lookout.b;
        var value = lookout.c;

        if (found && value != null) {
            switch (dataType) {
                case Number:
                    value = toNumber(value);
                    break;
                case Integer:
                    value = toInteger(value);
                    break;
                case Float:
                    value = toFloat(value);
                    break;
                case Boolean:
                    value = toBoolean(value);
                    break;
                case String:
                    value = toString(value);
                    break;
                default:
                    break;
            }
        }

        return new Triplet<Boolean, String, Object>(found, resolvedPath, value);
    }

    public static Triplet<Boolean, String, Object> contextLookup(HashMap<String, Object> flattenContext, String path) {
        if (flattenContext == null) {
            return new Triplet<Boolean, String, Object>(false, path, null);
        }

        var pattern = Pattern.compile(NESTED_REFERENCE_RX);
        var re = pattern.matcher(path);

        while (re.find()) {
            var lookup = contextLookup(flattenContext, re.group(1));
            if (!lookup.a) {
                return new Triplet<Boolean, String, Object>(false, path, null);
            }

            path = path.substring(0, re.start()) + lookup.c + path.substring(re.end());
            re = pattern.matcher(path);
        }

        if (flattenContext.containsKey(path)) {
            return new Triplet<Boolean, String, Object>(true, path, flattenContext.get(path));
        }

        return new Triplet<Boolean, String, Object>(false, path, null);
    }
    
    public static DataType getDataType(String path) {
        var re = Pattern.compile(DATA_TYPE_RX).matcher(path);
        if (re.find()) {
            switch (re.group(1)) {
                case "Number":
                    return DataType.Number;
                case "Integer":
                    return DataType.Integer;
                case "Float":
                    return DataType.Float;
                case "String":
                    return DataType.String;
                case "Boolean":
                    return DataType.Boolean;
                default:
                    return DataType.Unsupported;
            }
        }

        return DataType.Undefined;
    }

    public static String trimDataType(String path) {
        return path.replaceAll(DATA_TYPE_TRIM_RX, "");
    }

    public static boolean isIgnoredPath(String path, String[] ignoredPaths, Pattern[] ignoredPathsRx)
    {
        if (ignoredPaths != null) {
            for (String ignoredPath : ignoredPaths) {
                if (ignoredPath.equals(path)) {
                    return true;
                }
            }
        }

        if (ignoredPathsRx != null) {
            for (Pattern ignoredPath : ignoredPathsRx) {
                if (ignoredPath.matcher(path).find()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Number toNumber(Object value)
    {
        if (value instanceof Number number) {
            return number;
        }
        if (value instanceof Boolean bit) {
            return bit ? 1 : 0;
        }
        if (value instanceof String text) {
            if(Pattern.compile(FLOAT_RX).matcher(text).matches()) {
                return Float.parseFloat(text);
            }
            if(Pattern.compile(INT_RX).matcher(text).matches()) {
                return Integer.parseInt(text);
            }
            throw new ClassCastException(String.format("invalid conversion from \"%s\" text to number", text));
        }

        throw new ClassCastException(String.format("invalid conversion from \"%s\" to number", value));
    }

    public static int toInteger(Object value)
    {
        if (value instanceof Integer number) {
            return number;
        }
        if (value instanceof Float number) {
            return (int)Math.floor(number);
        }
        if (value instanceof Double number) {
            return (int)Math.floor(number);
        }
        if (value instanceof Boolean bit) {
            return bit ? 1 : 0;
        }
        if (value instanceof String text) {
            if(Pattern.compile(FLOAT_RX).matcher(text).matches()) {
                return Integer.parseInt(text.replaceAll(FLOAT_TRIM_RX, ""));
            }
            if(Pattern.compile(INT_RX).matcher(text).matches()) {
                return Integer.parseInt(text);
            }
            throw new ClassCastException(String.format("invalid conversion from \"%s\" text to int", text));
        }

        throw new ClassCastException(String.format("invalid conversion from \"%s\" to int", value));
    }

    public static float toFloat(Object value)
    {
        if (value instanceof Integer number) {
            return number;
        }
        if (value instanceof Float number) {
            return number;
        }
        if (value instanceof Double number) {
            return number.floatValue();
        }
        if (value instanceof String text) {
            try {
                return Float.parseFloat(text);
            } catch (Exception e) {
                throw new ClassCastException(String.format("invalid conversion from \"%s\" text to float", text));
            }
        }

        throw new ClassCastException(String.format("invalid conversion from \"%s\" to float", value));
    }

    public static String toString(Object value) {
        var text = value.toString();
        return value instanceof Boolean ? text.toLowerCase() : text; 
    }

    public static boolean toBoolean(Object value)
    {
        if (value instanceof Boolean bit) {
            return bit;
        }
        if (value instanceof Integer number) {
            if (number == 1) return true;
            if (number == 0) return false;
            throw new ClassCastException(String.format("invalid conversion from \"%s\" int to boolean", value));
        }
        if (value instanceof String text) {
            text = text.trim().toLowerCase();
            if (text.equals("true") || text.equals("1")) return true;
            if (text.equals("false") || text.equals("0")) return false;
            throw new ClassCastException(String.format("invalid conversion from \"%s\" string to boolean", value));
        }

        throw new ClassCastException(String.format("invalid conversion from \"%s\" to boolean", value));
    }

    @Override
    public String toString() {
        return String.format("{%s}", address);
    }
}
