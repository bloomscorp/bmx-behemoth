package com.bloomscorp.behemoth.pojo;

import com.bloomscorp.behemoth.service.BehemothMiddleware;

public record BehemothMiddlewareResult(boolean success, BehemothMiddleware<?, ?> middleware) {
}
