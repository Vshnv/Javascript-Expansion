package com.extendedclip.papi.expansion.javascript.evaluator;


import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.util.Map;

public final class NashornScriptEvaluator implements ScriptEvaluator {
    private final Map<String, Object> bindings;
    private final NashornScriptEngineFactory factory;
    private final ScriptEngine engine;

    public NashornScriptEvaluator(final Map<String, Object> bindings, final NashornScriptEngineFactory factory, ScriptEngine engine) {
        this.bindings = bindings;
        this.factory = factory;
        this.engine = engine;
    }

    @Override
    public Object execute(final Map<String, Object> additionalBindings, final String script) throws EvaluatorException {
        final long start = System.currentTimeMillis();
        final long startNano = System.nanoTime();
        try  {
            for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                bind(engine, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Object> entry : additionalBindings.entrySet()) {
                bind(engine, entry.getKey(), entry.getValue());
            }
            return engine.eval(script);
        } catch (final Exception exception) {
            throw new EvaluatorException("Failed to evaluate requested script.", exception);
        } finally {
            final long end = System.currentTimeMillis();
            final long endNano = System.nanoTime();

            final long timeTaken = end - start;
            System.out.printf("Evaluated in %d %s\n", timeTaken == 0 ? (endNano - startNano) : timeTaken, timeTaken == 0 ? "ns" : "ms");
        }
    }

    private void bind(final ScriptEngine engine, final String key, final Object value) {
        engine.put(key, value);
    }
}
