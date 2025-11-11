package ru.mai.topit.volunteers.platform.userinfo.application.factory;

public interface ModelFactory<S, T> {
    Class<S> supportsSource();
    T create(S source);
}


