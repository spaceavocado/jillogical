package com.spaceavocado;

import java.util.regex.Pattern;

import com.spaceavocado.kernel.operand.reference.ISimplifyOptions;

public class SampleSimplifyOptions implements ISimplifyOptions
{
    public String[] _ignoredPaths;
    public Pattern[] _ignoredPathsRx;

    public SampleSimplifyOptions(String[] ignoredPaths, Pattern[] ignoredPathsRx)
    {
        this._ignoredPaths = ignoredPaths;
        this._ignoredPathsRx = ignoredPathsRx;
    }

    @Override
    public String[] ignoredPaths() {
        return this._ignoredPaths;
    }

    @Override
    public Pattern[] ignoredPathsRx() {
        return this._ignoredPathsRx;
    }
}