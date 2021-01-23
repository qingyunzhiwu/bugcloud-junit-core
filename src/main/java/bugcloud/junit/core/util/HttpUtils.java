package bugcloud.junit.core.util;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;

/**
 * Url资源拼接工具
 * 
 * @author yuzhantao
 *
 */
public class HttpUtils {
	public static String createPropertyValue(String parameterType) {
//		if (argType == Signature.class) {
//	        return signature;
//	    } else if (argType == Request.class) {
//	        return signature.request();
//	    } else if (argType == Response.class) {
//	        return signature.response();
//	    } else if (argType == Session.class || argType == HttpSession.class) {
//	        return signature.request().session();
//	    } else if (argType == FileItem.class) {
//	        return new ArrayList<>(signature.request().fileItems().values()).get(0);
//	    } else if (argType == ModelAndView.class) {
//	        return new ModelAndView();
//	    } else if (argType == Map.class) {
//	        return signature.request().parameters();
//	    } else if (argType == Optional.class) {
//	        ParameterizedType firstParam = (ParameterizedType) parameter.getParameterizedType();
//	        Type paramsOfFirstGeneric = firstParam.getActualTypeArguments()[0];
//	        Class<?> modelType = ReflectKit.form(paramsOfFirstGeneric.getTypeName());
//	        return Optional.ofNullable(parseModel(modelType, signature.request(), null));
//	    } else {
//	        return parseModel(argType, signature.request(), null);
//	    }
		if (String.class.getName().equals(parameterType)) {
			return createStringValue();
		}
		return null;
	}

	private static String createStringValue() {
		return UUID.randomUUID().toString();
	}

	public static void testGet(MockMvc mock, String url) throws Exception {
		String responseString = mock.perform(MockMvcRequestBuilders.get(url) // 请求的url,请求的方法是get
				.contentType(MediaType.APPLICATION_JSON) // 数据的格式
				.param("pcode", "root") // 添加参数
		).andExpect(MockMvcResultMatchers.status().isOk()) // 返回的状态是200
				.andDo(MockMvcResultHandlers.print()) // 打印出请求和相应的内容
				.andReturn().getResponse().getContentAsString(); // 将相应的数据转换为字符串
		System.out.println("--------返回的json = " + responseString);
	}

	/**
	 * 获取网址
	 * 
	 * @param cs
	 * @return
	 */
	public static String getUrl(List<String> cs) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cs.size(); i++) {
			String s = cs.get(i);
			if (StringUtils.hasLength(s) == false) {
				continue;
			}
			int len = s.indexOf("/");
			if (len != 0) {
				s = "/" + s;
			}
			if (i < cs.size() - 1) {
				len = s.lastIndexOf("/");
				if (len == s.length() - 1) {
					s = s.substring(0,s.length()-1);
				}
			}
			sb.append(s);
		}
		return sb.toString();
	}

//	public static void testGet(String url,Map<?,?> params) {
//		StringBuffer sb = new StringBuffer();
//		if(params!=null) {
//			for(Map.Entry<?,?> item : params.entrySet()) {
//				if(sb.length()==0) {
//					sb.append("?");
//				}else {
//					sb.append("&");
//				}
//				sb.append(item.getKey()+"="+item.getValue());
//			}
//		}
//		sb.insert(0, url);
//		OkHttpClient client = new OkHttpClient();
//		Request request = new Request.Builder().url(sb.toString()).build();
//		final Call call = client.newCall(request);
//		call.enqueue(new Callback() {
//	        @Override
//	        public void onFailure(Call call, IOException e) {
//	        }
//	 
//	        @Override
//	        public void onResponse(Call call, okhttp3.Response response) throws IOException {
//	        }
//	    });
//	}
}
