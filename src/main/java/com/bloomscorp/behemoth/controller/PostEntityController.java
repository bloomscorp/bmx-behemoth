package com.bloomscorp.behemoth.controller;

import com.bloomscorp.behemoth.service.BehemothMiddleware;
import com.bloomscorp.behemoth.worker.BehemothControllerWorker;
import com.bloomscorp.nverse.NVerseHttpRequestWrapper;
import com.bloomscorp.nverse.sanitizer.NVerseSanitizer;
import com.bloomscorp.nverse.validator.NVerseValidator;
import com.bloomscorp.raintree.RainTreeResponse;
import com.bloomscorp.raintree.restful.RainEnhancedResponse;
import com.bloomscorp.raintree.restful.RainEntity;
import com.bloomscorp.raintree.restful.RainResponse;

import java.util.List;

public interface PostEntityController {

    <E, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntity(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthPostMessage,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker
    );

    <E, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntity(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthPostMessage,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        W worker
    );

    <
        E1,
        E2,
        W extends BehemothControllerWorker<RainEntity<E2>>
    > String postEntityCustomResponse(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthPostMessage,
        String successLogMessage,
        NVerseValidator<E1> validator,
        NVerseSanitizer<E1, E1> sanitizer,
        E1 entity,
        W worker
    );

    <E, W extends BehemothControllerWorker<R>, R> RainTreeResponse postEntityEnhancedResponse(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthPostMessage,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker,
        RainEnhancedResponse<E, R> enhancedResponse
    );

    <E, W extends BehemothControllerWorker<R>, R> RainTreeResponse postEntityEnhancedResponse(
        NVerseHttpRequestWrapper request,
        String methodName,
        int surveillanceCode,
        String unAuthPostMessage,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        W worker,
        RainEnhancedResponse<E, R> enhancedResponse
    );

    <E, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntityUnauthorized(
        NVerseHttpRequestWrapper request,
        String methodName,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker
    );

    <E, W extends BehemothControllerWorker<Integer>> RainTreeResponse postEntityUnauthorized(
        NVerseHttpRequestWrapper request,
        String methodName,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        W worker
    );

    <
        E1,
        E2,
        W extends BehemothControllerWorker<RainEntity<E2>>
    > String postEntityCustomResponseUnauthorized(
        NVerseHttpRequestWrapper request,
        String methodName,
        String successLogMessage,
        NVerseValidator<E1> validator,
        NVerseSanitizer<E1, E1> sanitizer,
        E1 entity,
        W worker
    );

    <E, W extends BehemothControllerWorker<R>, R> RainTreeResponse postEntityEnhancedResponseUnauthorized(
        NVerseHttpRequestWrapper request,
        String methodName,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        List<BehemothMiddleware<?, ?>> middlewares,
        W worker,
        RainEnhancedResponse<E, R> enhancedResponse
    );

    <E, W extends BehemothControllerWorker<R>, R> RainTreeResponse postEntityEnhancedResponseUnauthorized(
        NVerseHttpRequestWrapper request,
        String methodName,
        String successLogMessage,
        NVerseValidator<E> validator,
        NVerseSanitizer<E, E> sanitizer,
        E entity,
        W worker,
        RainEnhancedResponse<E, R> enhancedResponse
    );
}
