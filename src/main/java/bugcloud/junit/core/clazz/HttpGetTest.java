package bugcloud.junit.core.clazz;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpGetTest extends TestCase {
//	public void testGet(Method method) {
//		List<String> urls = new ArrayList<>();
//		String headUrl = null;
//		Annotation classAnnotation = method.getDeclaringClass().getAnnotation(RequestMapping.class);
//		if (classAnnotation != null) {
//			headUrl = ((RequestMapping) classAnnotation).value()[0];
//		}
//		Annotation annotation = method.getAnnotation(GetMapping.class);
//		if (annotation != null) {
//			String[] getMappingValues = ((GetMapping) annotation).value();
//			if (getMappingValues != null) {
//				for (String shortUrl : getMappingValues) {
//					urls.clear();
//					urls.add(headUrl);
//					urls.add(shortUrl);
//					try {
//						HttpGetTest.testGet("http://127.0.0.1:8080/"+HttpUtils.getUrl(urls), method);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}
	
	public static void testGet(String url,Map<String,String> params) throws IOException {
		StringBuffer sb = new StringBuffer();
		if(params!=null) {
			for(Map.Entry<String, String> item : params.entrySet()) {
				if(sb.length()==0) {
					sb.append("?");
				}else {
					sb.append("&");
				}
				sb.append(item.getKey()+"="+item.getValue());
			}
		}
		sb.insert(0, url);
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(sb.toString()).build();
		final Call call = client.newCall(request);
		call.enqueue(new Callback() {
	        @Override
	        public void onFailure(Call call, IOException e) {
	        }
	 
	        @Override
	        public void onResponse(Call call, okhttp3.Response response) throws IOException {
	        }
	    });
	}

}
