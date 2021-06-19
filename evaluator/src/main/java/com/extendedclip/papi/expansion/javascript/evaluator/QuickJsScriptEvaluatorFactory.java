package com.extendedclip.papi.expansion.javascript.evaluator;

import java.util.Map;

public final class QuickJsScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    @Override
    public ScriptEvaluator create(Map<String, Object> bindings) {
        return new QuickJsScriptEvaluator();
    }

    @Override
    public void cleanBinaries() {
        System.out.println("{DEBUG} Cleaned binaries");
    }
}
