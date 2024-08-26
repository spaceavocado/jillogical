package com.spaceavocado.jillogical.kernel.operand.reference;

import java.util.regex.Pattern;

public class DefaultSimplifyOptions implements ISimplifyOptions {

    @Override
    public String[] ignoredPaths() {
        return new String[] {};
    }

    @Override
    public Pattern[] ignoredPathsRx() {
        return new Pattern[] {};
    }
}
