package com.extendedclip.papi.expansion.javascript.evaluator;

import com.koushikdutta.quack.QuackContext;

import java.util.Map;

public final class QuickJsScriptEvaluator implements ScriptEvaluator {
    @Override
    public Object execute(Map<String, Object> additionalBindings, String script) {
        try (QuackContext context = QuackContext.create()) {
            return context.evaluate(script);
        }
    }
}
