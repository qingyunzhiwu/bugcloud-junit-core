package bugcloud.junit.core.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.alibaba.fastjson.JSONObject;

import bugcloud.junit.core.annotation.BugCloudTest;
import bugcloud.junit.core.vo.RequestApiQuestionVo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BugCloudRunListener extends RunListener {
	private static final Log logger = LogFactory.getLog(BugCloudRunListener.class);
	private static final String PUSH_QUESTION_URL = "http://www.bug-cloud.com:8080/api/questions";
//	private static final String PUSH_QUESTION_URL = "http://127.0.0.1:8080/api/questions";
	private static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");

	private Class<?> testClass = null;

	public BugCloudRunListener(Class<?> clazz) {
		this.testClass = clazz;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void testRunFinished(Result result) throws Exception {
		BugCloudTest bugCloudTest = this.testClass.getAnnotation(BugCloudTest.class);
		if (bugCloudTest == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\nTest Class:" + this.testClass.getName() + " 开始测试=========================================");
		sb.append("\n run time:" + result.getRunTime());
		sb.append("\n run count:" + result.getRunCount());
		sb.append("\n ignore count:" + result.getIgnoreCount());
		sb.append("\n failure count:" + result.getFailureCount());
		sb.append("\n---------------------------------------------------");
		sb.append("\n appKey:" + bugCloudTest.appKey());
		sb.append("\n appSecret:" + bugCloudTest.appSecret());
		sb.append("\n pusher:" + bugCloudTest.pusher());
		sb.append("\n handler:" + bugCloudTest.handler());
		sb.append("\n---------------------------------------------------");
		for (Failure failure : result.getFailures()) {
			sb.append("\n 测试失败结果： " + failure.toString());
		}

		// 如果不推送，就直接返回。
		if (bugCloudTest.isPush() == false) {
			sb.append(" Test Class:" + this.testClass + " 测试完成=========================================");
			return;
		}

		OkHttpClient client = new OkHttpClient();
		JSONObject json = new JSONObject();
		json.put("appKey", bugCloudTest.appKey());
		json.put("appSecret", bugCloudTest.appSecret());
		json.put("pusher", bugCloudTest.pusher());
		json.put("handler", bugCloudTest.handler());
		List<RequestApiQuestionVo> questions = new ArrayList<>();
		for (Failure fail : result.getFailures()) {
			RequestApiQuestionVo raq = new RequestApiQuestionVo();
			raq.setSuccess(false);
			raq.setTitle(fail.getDescription().getClassName() + "." + fail.getDescription().getMethodName());
			raq.setContent(fail.getMessage());
			questions.add(raq);
		}
		json.put("questions", questions);
		RequestBody formBody = RequestBody.create(FORM_CONTENT_TYPE, String.valueOf(json));
		Request request = new Request.Builder().url(PUSH_QUESTION_URL).header("content-type", "application/json")
				.post(formBody).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.code() == 200) {
				sb.append("\n 提交问题接口完成,返回body:" + response.body().string());
			} else {
				sb.append("\n 提交问题接口失败\n state code:" + response.code() + "\n body:" + response.body().string());
			}
		} catch (Exception e) {
			sb.append("\n 提交问题接口失败\n local error:" + e.getMessage());
			if (bugCloudTest.isAssertPushException()) {
				throw e;
			}
		} finally {
			sb.append("\n Test Class:" + this.testClass.getName() + " 测试完成=========================================");
			logger.info(sb.toString());
		}

		super.testRunFinished(result);
	}
}
