package top.putop.face_show.model;

import java.util.Map;

/**
 * 照片扫描出来人物特征信息
 * 
 * @author ffj
 *
 */
public class ManMessage {

	/**
	 * 性别 Male:男性,Female:女性
	 */
	private String gender;

	public double getSmileValue() {
		return smileValue;
	}

	public void setSmileValue(double smileValue) {
		this.smileValue = smileValue;
	}

	/**
	 * 年龄
	 */
	private int age;
	/**
	 * 笑容概率 值为一个 [0,100] 的浮点数，小数点后3位有效数字。数值越大表示笑程度高。
	 */
	private double smileValue;

	/**
	 * 情绪识别结果 anger：愤怒 disgust：厌恶 fear：恐惧 happiness：高兴 neutral：平静 sadness：伤心
	 * surprise：惊讶
	 */
	private Map<String, Double> emotion;
	/**
	 * 种族 Asian:亚洲人,White:白人,Black:黑人
	 */
	private String ethnicity;
	/**
	 * 男性眼中的魅力值
	 */
	private double beauty_male_score;
	/**
	 * 女性眼中的魅力值
	 */
	private double beauty_female_score;
	/**
	 * 健康
	 */
	private double health;
	/**
	 * 色斑
	 */
	private double stain;
	/**
	 * 青春痘
	 */
	private double acne;
	/**
	 * 黑眼圈
	 */
	private double dark_circle;

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getStain() {
		return stain;
	}

	public void setStain(double stain) {
		this.stain = stain;
	}

	public double getAcne() {
		return acne;
	}

	public void setAcne(double acne) {
		this.acne = acne;
	}

	public double getDark_circle() {
		return dark_circle;
	}

	public void setDark_circle(double dark_circle) {
		this.dark_circle = dark_circle;
	}

	public double getBeauty_male_score() {
		return beauty_male_score;
	}

	public void setBeauty_male_score(double beauty_male_score) {
		this.beauty_male_score = beauty_male_score;
	}

	public double getBeauty_female_score() {
		return beauty_female_score;
	}

	public void setBeauty_female_score(double beauty_female_score) {
		this.beauty_female_score = beauty_female_score;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public Map<String, Double> getEmotion() {
		return emotion;
	}

	public void setEmotion(Map<String, Double> emotion) {
		this.emotion = emotion;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
