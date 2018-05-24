package top.putop.face_show.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLException;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import top.putop.face_show.model.ManMessage;

/**
 * face++ 解析工具类
 * 
 * @author ffj
 *
 */
public class FaceUtil {

	private final static int CONNECT_TIME_OUT = 30000;
	private final static int READ_OUT_TIME = 50000;
	private static String boundaryString = getBoundary();

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		getMess("F:/img/picFile/aa.jpg");
	}

	/**
	 * 获取json字符串的脸部识别信息
	 */
	public static String getMess(String picPath) {

		File file = new File(picPath);
		byte[] buff = getBytesFromFile(file);
		String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, byte[]> byteMap = new HashMap<>();
		map.put("api_key", "your api_key");
		map.put("api_secret", "your api_secret");
		map.put("return_landmark", "1");
		map.put("return_attributes",
				"gender,age,smiling,headpose,facequality,blur,eyestatus,emotion,ethnicity,beauty,mouthstatus,eyegaze,skinstatus");
		byteMap.put("image_file", buff);
		try {
			byte[] bacd = post(url, map, byteMap);
			String str = new String(bacd);
			// System.out.println(str);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap)
			throws Exception {
		HttpURLConnection conne;
		URL url1 = new URL(url);
		conne = (HttpURLConnection) url1.openConnection();
		conne.setDoOutput(true);
		conne.setUseCaches(false);
		conne.setRequestMethod("POST");
		conne.setConnectTimeout(CONNECT_TIME_OUT);
		conne.setReadTimeout(READ_OUT_TIME);
		conne.setRequestProperty("accept", "*/*");
		conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
		conne.setRequestProperty("connection", "Keep-Alive");
		conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
		DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry) iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			obos.writeBytes("--" + boundaryString + "\r\n");
			obos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
			obos.writeBytes("\r\n");
			obos.writeBytes(value + "\r\n");
		}
		if (fileMap != null && fileMap.size() > 0) {
			Iterator fileIter = fileMap.entrySet().iterator();
			while (fileIter.hasNext()) {
				Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
				obos.writeBytes("--" + boundaryString + "\r\n");
				obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey() + "\"; filename=\""
						+ encode(" ") + "\"\r\n");
				obos.writeBytes("\r\n");
				obos.write(fileEntry.getValue());
				obos.writeBytes("\r\n");
			}
		}
		obos.writeBytes("--" + boundaryString + "--" + "\r\n");
		obos.writeBytes("\r\n");
		obos.flush();
		obos.close();
		InputStream ins = null;
		int code = conne.getResponseCode();
		try {
			if (code == 200) {
				ins = conne.getInputStream();
			} else {
				ins = conne.getErrorStream();
			}
		} catch (SSLException e) {
			e.printStackTrace();
			return new byte[0];
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[4096];
		int len;
		while ((len = ins.read(buff)) != -1) {
			baos.write(buff, 0, len);
		}
		byte[] bytes = baos.toByteArray();
		ins.close();
		return bytes;
	}

	private static String getBoundary() {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 32; ++i) {
			sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(
					random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
		}
		return sb.toString();
	}

	private static String encode(String value) throws Exception {
		return URLEncoder.encode(value, "UTF-8");
	}

	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * json字符串解析出相关信息封装对象输出
	 * 
	 * @param faceMess
	 * @return
	 */
	public static List<ManMessage> getAttrMes(String faceMess) {
		// String jsonS =
		// "{\"image_id\":\"jJ9buQ38Io2JsCluZlV9qQ==\",\"request_id\":\"1527062355,937d066f-76a5-45e9-8413-36496e290abb\",\"time_used\":298,\"faces\":[{\"landmark\":{\"mouth_upper_lip_left_contour2\":{\"y\":551,\"x\":368},\"mouth_upper_lip_top\":{\"y\":530,\"x\":411},\"mouth_upper_lip_left_contour1\":{\"y\":532,\"x\":395},\"left_eye_upper_left_quarter\":{\"y\":375,\"x\":273},\"left_eyebrow_lower_middle\":{\"y\":345,\"x\":277},\"mouth_upper_lip_left_contour3\":{\"y\":554,\"x\":379},\"right_eye_top\":{\"y\":320,\"x\":449},\"left_eye_bottom\":{\"y\":385,\"x\":293},\"right_eyebrow_lower_left_quarter\":{\"y\":313,\"x\":410},\"right_eye_pupil\":{\"y\":330,\"x\":451},\"mouth_lower_lip_right_contour1\":{\"y\":533,\"x\":452},\"mouth_lower_lip_right_contour3\":{\"y\":550,\"x\":447},\"mouth_lower_lip_right_contour2\":{\"y\":541,\"x\":469},\"contour_chin\":{\"y\":687,\"x\":444},\"contour_left9\":{\"y\":682,\"x\":399},\"left_eye_lower_right_quarter\":{\"y\":378,\"x\":308},\"mouth_lower_lip_top\":{\"y\":541,\"x\":416},\"right_eyebrow_upper_middle\":{\"y\":282,\"x\":438},\"left_eyebrow_left_corner\":{\"y\":360,\"x\":233},\"right_eye_bottom\":{\"y\":341,\"x\":455},\"contour_left7\":{\"y\":630,\"x\":332},\"contour_left6\":{\"y\":596,\"x\":306},\"contour_left5\":{\"y\":559,\"x\":284},\"contour_left4\":{\"y\":521,\"x\":265},\"contour_left3\":{\"y\":482,\"x\":250},\"contour_left2\":{\"y\":442,\"x\":238},\"contour_left1\":{\"y\":401,\"x\":229},\"left_eye_lower_left_quarter\":{\"y\":388,\"x\":278},\"contour_right1\":{\"y\":304,\"x\":582},\"contour_right3\":{\"y\":401,\"x\":606},\"contour_right2\":{\"y\":352,\"x\":596},\"mouth_left_corner\":{\"y\":576,\"x\":348},\"contour_right4\":{\"y\":452,\"x\":612},\"contour_right7\":{\"y\":596,\"x\":572},\"right_eyebrow_left_corner\":{\"y\":321,\"x\":378},\"nose_right\":{\"y\":441,\"x\":440},\"nose_tip\":{\"y\":441,\"x\":371},\"contour_right5\":{\"y\":503,\"x\":610},\"nose_contour_lower_middle\":{\"y\":477,\"x\":389},\"left_eyebrow_lower_left_quarter\":{\"y\":354,\"x\":255},\"mouth_lower_lip_left_contour3\":{\"y\":566,\"x\":397},\"right_eye_right_corner\":{\"y\":326,\"x\":485},\"right_eye_lower_right_quarter\":{\"y\":335,\"x\":472},\"mouth_upper_lip_right_contour2\":{\"y\":521,\"x\":458},\"right_eyebrow_lower_right_quarter\":{\"y\":299,\"x\":478},\"left_eye_left_corner\":{\"y\":389,\"x\":264},\"mouth_right_corner\":{\"y\":530,\"x\":489},\"mouth_upper_lip_right_contour3\":{\"y\":530,\"x\":451},\"right_eye_lower_left_quarter\":{\"y\":342,\"x\":436},\"left_eyebrow_right_corner\":{\"y\":331,\"x\":323},\"left_eyebrow_lower_right_quarter\":{\"y\":338,\"x\":300},\"right_eye_center\":{\"y\":332,\"x\":452},\"nose_left\":{\"y\":470,\"x\":339},\"mouth_lower_lip_left_contour1\":{\"y\":557,\"x\":381},\"left_eye_upper_right_quarter\":{\"y\":365,\"x\":305},\"right_eyebrow_lower_middle\":{\"y\":303,\"x\":443},\"left_eye_top\":{\"y\":366,\"x\":288},\"left_eye_center\":{\"y\":378,\"x\":291},\"contour_left8\":{\"y\":660,\"x\":363},\"contour_right9\":{\"y\":669,\"x\":497},\"right_eye_left_corner\":{\"y\":341,\"x\":419},\"mouth_lower_lip_bottom\":{\"y\":557,\"x\":422},\"left_eyebrow_upper_left_quarter\":{\"y\":341,\"x\":249},\"left_eye_pupil\":{\"y\":375,\"x\":291},\"right_eyebrow_upper_left_quarter\":{\"y\":294,\"x\":402},\"contour_right8\":{\"y\":636,\"x\":540},\"right_eyebrow_right_corner\":{\"y\":296,\"x\":514},\"right_eye_upper_left_quarter\":{\"y\":327,\"x\":432},\"left_eyebrow_upper_middle\":{\"y\":331,\"x\":272},\"right_eyebrow_upper_right_quarter\":{\"y\":281,\"x\":477},\"nose_contour_left1\":{\"y\":370,\"x\":339},\"nose_contour_left2\":{\"y\":435,\"x\":341},\"mouth_upper_lip_right_contour1\":{\"y\":522,\"x\":425},\"nose_contour_right1\":{\"y\":354,\"x\":389},\"nose_contour_right2\":{\"y\":414,\"x\":416},\"mouth_lower_lip_left_contour2\":{\"y\":573,\"x\":372},\"contour_right6\":{\"y\":551,\"x\":596},\"nose_contour_right3\":{\"y\":463,\"x\":417},\"nose_contour_left3\":{\"y\":478,\"x\":362},\"left_eye_right_corner\":{\"y\":370,\"x\":320},\"left_eyebrow_upper_right_quarter\":{\"y\":326,\"x\":298},\"right_eye_upper_right_quarter\":{\"y\":320,\"x\":468},\"mouth_upper_lip_bottom\":{\"y\":540,\"x\":415}},\"attributes\":{\"emotion\":{\"sadness\":0.001,\"neutral\":99.558,\"disgust\":0.414,\"anger\":0.004,\"surprise\":0.001,\"fear\":0.001,\"happiness\":0.021},\"beauty\":{\"female_score\":66.938,\"male_score\":61.422},\"gender\":{\"value\":\"Male\"},\"age\":{\"value\":59},\"mouthstatus\":{\"close\":100,\"surgical_mask_or_respirator\":0,\"open\":0,\"other_occlusion\":0},\"glass\":{\"value\":\"None\"},\"skinstatus\":{\"dark_circle\":46.792,\"stain\":0.005,\"acne\":0.129,\"health\":11.102},\"headpose\":{\"yaw_angle\":10.712633,\"pitch_angle\":3.0783901,\"roll_angle\":-21.947752},\"blur\":{\"blurness\":{\"threshold\":50,\"value\":0.007},\"motionblur\":{\"threshold\":50,\"value\":0.007},\"gaussianblur\":{\"threshold\":50,\"value\":0.007}},\"smile\":{\"threshold\":50,\"value\":0.003},\"eyestatus\":{\"left_eye_status\":{\"normal_glass_eye_open\":1.143,\"no_glass_eye_close\":11.49,\"occlusion\":2.109,\"no_glass_eye_open\":18.639,\"normal_glass_eye_close\":0.752,\"dark_glasses\":65.867},\"right_eye_status\":{\"normal_glass_eye_open\":0,\"no_glass_eye_close\":0.048,\"occlusion\":0,\"no_glass_eye_open\":99.952,\"normal_glass_eye_close\":0,\"dark_glasses\":0}},\"facequality\":{\"threshold\":70.1,\"value\":78.937},\"ethnicity\":{\"value\":\"BLACK\"},\"eyegaze\":{\"right_eye_gaze\":{\"position_x_coordinate\":0.445,\"vector_z_component\":0.927,\"vector_x_component\":-0.133,\"vector_y_component\":0.35,\"position_y_coordinate\":0.522},\"left_eye_gaze\":{\"position_x_coordinate\":0.493,\"vector_z_component\":0.955,\"vector_x_component\":0.282,\"vector_y_component\":0.089,\"position_y_coordinate\":0.513}}},\"face_rectangle\":{\"width\":391,\"top\":288,\"left\":246,\"height\":391},\"face_token\":\"a725974e4fc7733bdcc9f61b8834d542\"}]}";

		List<ManMessage> messages = new ArrayList<>();

		JSONObject object = JSONObject.parseObject(faceMess);
		JSONArray faces = object.getJSONArray("faces");
		int size = faces.size();

		if (size != 0) {
			// 循环获取信息
			for (int i = 0; i < size; i++) {
				ManMessage message = new ManMessage();

				JSONObject attributes = faces.getJSONObject(i).getJSONObject("attributes");

				if (attributes == null) {
					continue;
				}
				// 性别
				String gender = attributes.getJSONObject("gender").getString("value");
				if (StringUtils.isEmpty(gender)) {
					message.setGender("未知");
				}
				if ("male".equals(gender.toLowerCase())) {
					message.setGender("男性");
				} else if ("female".equals(gender.toLowerCase())) {
					message.setGender("女性");
				}
				// 年龄
				int age = attributes.getJSONObject("age").getIntValue("value");
				message.setAge(age);
				// 微笑概率
				double smileValue = attributes.getJSONObject("smile").getDoubleValue("value");
				message.setSmileValue(smileValue);
				Map<String, Double> emotionM = new HashMap<>();
				JSONObject emotion = attributes.getJSONObject("emotion");
				// 愤怒
				double anger = emotion.getDoubleValue("anger");
				emotionM.put("anger", anger);
				// 厌恶
				double disgust = emotion.getDoubleValue("disgust");
				emotionM.put("disgust", disgust);
				// 恐惧
				double fear = emotion.getDoubleValue("fear");
				emotionM.put("fear", fear);
				// 高兴
				double happiness = emotion.getDoubleValue("happiness");
				emotionM.put("happiness", happiness);
				// 平静
				double neutral = emotion.getDoubleValue("neutral");
				emotionM.put("neutral", neutral);
				// 伤心
				double sadness = emotion.getDoubleValue("sadness");
				emotionM.put("sadness", sadness);
				// 惊讶
				double surprise = emotion.getDoubleValue("surprise");
				emotionM.put("surprise", surprise);
				if (emotionM != null && !emotionM.isEmpty()) {
					message.setEmotion(emotionM);
				}
				// 种族
				String ethnicity = attributes.getJSONObject("ethnicity").getString("value").toLowerCase();
				if (StringUtils.isEmpty(ethnicity)) {
					message.setEthnicity("未知");
				}
				if ("asian".equals(ethnicity)) {
					message.setEthnicity("亚洲人");
				} else if ("white".equals(ethnicity)) {
					message.setEthnicity("白人");
				} else if ("black".equals(ethnicity)) {
					message.setEthnicity("黑人");
				}
				// 男性眼中的魅力值
				double beauty_male_score = attributes.getJSONObject("beauty").getDoubleValue("male_score");
				message.setBeauty_male_score(beauty_male_score);
				// 女性眼中的魅力值
				double beauty_female_score = attributes.getJSONObject("beauty").getDoubleValue("female_score");
				message.setBeauty_female_score(beauty_female_score);
				JSONObject skinstatus = attributes.getJSONObject("skinstatus");
				// 健康
				double health = skinstatus.getDoubleValue("health");
				message.setHealth(health);
				// 色斑
				double stain = skinstatus.getDoubleValue("stain");
				message.setStain(stain);
				// 青春痘
				double acne = skinstatus.getDoubleValue("acne");
				message.setAcne(acne);
				// 黑眼圈
				double dark_circle = skinstatus.getDoubleValue("dark_circle");
				message.setDark_circle(dark_circle);

				messages.add(message);

			}
		} else {
			return null;
		}
		return messages;
	}

}
