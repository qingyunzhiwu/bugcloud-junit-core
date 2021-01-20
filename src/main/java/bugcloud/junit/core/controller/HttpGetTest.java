package bugcloud.junit.core.controller;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import bugcloud.junit.core.util.UrlUtils;
import junit.framework.TestCase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpGetTest extends TestCase {
	public void testGet2() {
		
		System.out.println();
	}
	public void testGet(Method method) {
		List<String> urls = new ArrayList<>();
		String headUrl = null;
		Annotation classAnnotation = method.getDeclaringClass().getAnnotation(RequestMapping.class);
		if (classAnnotation != null) {
			headUrl = ((RequestMapping) classAnnotation).value()[0];
		}
		Annotation annotation = method.getAnnotation(GetMapping.class);
		if (annotation != null) {
			String[] getMappingValues = ((GetMapping) annotation).value();
			if (getMappingValues != null) {
				for (String shortUrl : getMappingValues) {
					urls.clear();
					urls.add(headUrl);
					urls.add(shortUrl);
					try {
						HttpGetTest.testGet("http://127.0.0.1:8080/"+UrlUtils.getUrl(urls), method);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static void testGet(String url, Method method) throws IOException {
		System.out.println((url));
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();

		try (Response response = client.newCall(request).execute()) {
			response.body().string();
		}
	}
}
