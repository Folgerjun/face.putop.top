package top.putop.face_show.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import top.putop.face_show.model.ManMessage;
import top.putop.face_show.utils.FaceUtil;
import top.putop.face_show.utils.FileUploadUtil;

@Controller
public class FaceController {

	/**
	 * 进入初始页面
	 * 
	 * @return
	 */
	@RequestMapping("/")
	public String index() {

		return "face";
	}

	/**
	 * 图片保存并输出信息
	 */
	@PostMapping("/sendPic")
	@ResponseBody
	public String uploadReport(HttpServletRequest request, HttpServletResponse response) {

		// 调用通用接口上传文件
		String picPath = FileUploadUtil.uploadFile(request, "picFile");

		String faceMess = FaceUtil.getMess(picPath);

		List<ManMessage> messages = FaceUtil.getAttrMes(faceMess);

		return JSONObject.toJSONString(messages);
	}

	public static void main(String[] args) {

	}

}
