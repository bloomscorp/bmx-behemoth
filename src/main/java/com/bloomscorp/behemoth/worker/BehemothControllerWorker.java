package com.bloomscorp.behemoth.worker;

@FunctionalInterface
public interface BehemothControllerWorker<R> {
    R work();
}
