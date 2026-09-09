package com.aifindr.gateway.config.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class McpSseStreamFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		if (!isMcpGet(request)) {
			chain.doFilter(request, response);
			return;
		}
		response.setHeader("X-Accel-Buffering", "no");
		response.setBufferSize(512);
		chain.doFilter(withoutGzip(request), new SseFlushingResponse(response));
	}

	private static boolean isMcpGet(HttpServletRequest request) {
		return "GET".equalsIgnoreCase(request.getMethod()) && "/mcp".equals(request.getRequestURI());
	}

	private static HttpServletRequest withoutGzip(HttpServletRequest request) {
		return new HttpServletRequestWrapper(request) {
			@Override
			public String getHeader(String name) {
				if ("Accept-Encoding".equalsIgnoreCase(name)) {
					return "identity";
				}
				return super.getHeader(name);
			}

			@Override
			public Enumeration<String> getHeaders(String name) {
				if ("Accept-Encoding".equalsIgnoreCase(name)) {
					return Collections.enumeration(List.of("identity"));
				}
				return super.getHeaders(name);
			}
		};
	}

	private static final class SseFlushingResponse extends HttpServletResponseWrapper {
		private boolean flushed;

		private SseFlushingResponse(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void setContentType(String type) {
			super.setContentType(type);
			flushIfSse(type);
		}

		@Override
		public void setHeader(String name, String value) {
			super.setHeader(name, value);
			if ("Content-Type".equalsIgnoreCase(name)) {
				flushIfSse(value);
			}
		}

		private void flushIfSse(String contentType) {
			if (flushed || contentType == null || !contentType.contains("text/event-stream")) {
				return;
			}
			flushed = true;
			try {
				flushBuffer();
			}
			catch (IOException ignored) {
				flushed = false;
			}
		}
	}
}
