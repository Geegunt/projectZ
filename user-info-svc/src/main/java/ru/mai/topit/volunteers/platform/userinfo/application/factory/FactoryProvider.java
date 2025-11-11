package ru.mai.topit.volunteers.platform.userinfo.application.factory;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FactoryProvider {
    private final Map<Class<?>, ModelFactory<?, ?>> registry = new HashMap<>();

    public FactoryProvider(List<ModelFactory<?, ?>> factories) {
        for (ModelFactory<?, ?> factory : factories) {
            registry.put(factory.supportsSource(), factory);
        }
    }

    @SuppressWarnings("unchecked")
    public <S, T> ModelFactory<S, T> getFactory(Class<S> sourceType) {
        ModelFactory<?, ?> factory = registry.get(sourceType);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for source type: " + sourceType.getName());
        }
        return (ModelFactory<S, T>) factory;
    }
}


