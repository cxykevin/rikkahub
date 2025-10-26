package me.rerere.rikkahub.data.ai

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import me.rerere.rikkahub.utils.JsonInstant
import me.rerere.rikkahub.utils.jsonPrimitiveOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AIRequestInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val host = request.url.host

        if (host == "api.siliconflow.cn") {
            request = processSiliconCloudRequest(request)
        }

        return chain.proceed(request)
    }

    // 处理硅基流动的请求
    private fun processSiliconCloudRequest(request: Request): Request {
        val authHeader = request.header("Authorization")
        val path = request.url.encodedPath

        // 如果没有设置api token, 填入免费api key
        if ((authHeader?.trim() == "Bearer" || authHeader?.trim() == "Bearer sk-") && path in listOf(
                "/v1/chat/completions",
                "/v1/models"
            )
        ) {
            // 移除了 Firebase 相关的代码，这里直接返回原请求
            // 如果需要实现其他逻辑，可以在这里添加
        }

        return request
    }
}

private fun Request.readBodyAsJson(): JsonElement? {
    val contentType = body?.contentType()
    if (contentType?.type == "application" && contentType.subtype == "json") {
        val buffer = okio.Buffer()
        buffer.use {
            body?.writeTo(it)
            return JsonInstant.parseToJsonElement(buffer.readUtf8())
        }
    }
    return null
}
