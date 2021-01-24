package bugcloud.junit.core.clazz;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpGetTest extends TestCase {
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
