package top.putop.face_show.utils;

import java.io.File;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * BootStrap-FileInput 后台文件上传工具类
 * 
 * @author ffj
 *
 */
public class FileUploadUtil {

	// 保存目录
	// private final static String FILE_UPLOAD_PATH = "F:/img/picFile";
	private final static String FILE_UPLOAD_PATH = "/usr/local/faceimgs/";

	/**
	 * 上传文件通用接口
	 * 
	 * @param request
	 *            请求体
	 * @param dstFileName
	 *            html上传组件中(input中name属性)，上传文件体名称，通过此名称获取所有上传的文件map
	 */
	public static String uploadFile(HttpServletRequest request, String dstFileName) {

		String reportAddr = ""; // 保存到服务器目录的文件全路径

		// 判断保存文件的路径是否存在
		File fileUploadPath = new File(FILE_UPLOAD_PATH);
		if (!fileUploadPath.exists()) {
			fileUploadPath.mkdir();
		}
		// 解析图片
		if (ServletFileUpload.isMultipartContent(request)) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> fileList = multipartRequest.getFiles(dstFileName);
			// 这里图片单一上传就是一次只有一条数据 无视for
			for (MultipartFile item : fileList) {
				String fileName = ""; // 当前上传文件全名称
				String fileType = ""; // 当前上传文件类型
				String saveFileName = ""; // 保存到服务器目录的文件名称

				try {
					fileName = item.getOriginalFilename();
					fileType = item.getContentType();
					saveFileName = Instant.now().toEpochMilli() + "_" + fileName;
					reportAddr = fileUploadPath + "/" + saveFileName;
					reportAddr = reportAddr.replace("/", File.separator).replace("\\", File.separator);

					File savedFile = new File(fileUploadPath, saveFileName);
					// 保存图片
					item.transferTo(savedFile);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return reportAddr;
	}

}
