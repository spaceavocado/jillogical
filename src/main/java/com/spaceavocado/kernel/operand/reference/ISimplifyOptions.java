package com.spaceavocado.kernel.operand.reference;

import java.util.regex.Pattern;

public interface ISimplifyOptions {
    String[] ignoredPaths();
    Pattern[] ignoredPathsRx();
}

