package com.shj.eids.utils;

import com.baidu.aip.face.AipFace;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import java.awt.geom.FlatteningPathIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @ClassName: AipFaceUtils
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 10:32
 **/
public class AipFaceUtils {
    private static AipFace client;
    private static Date registerTime;
    private static final String APP_ID = "18922324";
    private static final String API_KEY = "NOCU6jMGz9PdCGs0R6GGec4l";
    private static final String SECRET_KEY = "GZoGrp2P7QcwGx8sg0WAI4ZWP1PrX1GF";
    public static final String GROUP_ID = "PatientFaceRepository";
    public static final double OCCLUSION_LEFT_EYE = 0.3;
    public static final double OCCLUSION_RIGHT_EYE = 0.3;
    public static final double OCCLUSION_NOSE = 0.4;
    public static final double OCCLUSION_MOUTH = 0.4;
    public static final double OCCLUSION_LEFT_CHECK = 0.4;
    public static final double OCCLUSION_RIGHT_CHECK = 0.4;
    public static final double OCCLUSION_CHIN_CONTOUR = 0.4;
    public static final double BLUR = 0.4;
    public static final double ILLUMINATION = 40;
    public static final double[] values = {OCCLUSION_LEFT_EYE, OCCLUSION_RIGHT_EYE, OCCLUSION_NOSE, OCCLUSION_MOUTH,
            OCCLUSION_LEFT_CHECK, OCCLUSION_RIGHT_CHECK, OCCLUSION_CHIN_CONTOUR, BLUR, ILLUMINATION};
    public static final String[] msgs = {"左眼遮挡", "右眼遮挡", "鼻子遮挡", "嘴巴遮挡", "左脸遮挡", "右脸遮挡", "下巴遮挡", "照片模糊", "照片过暗", "检测非真实人脸"};
    public static final String []keys = {"left_eye", "right_eye", "nose","mouth", "left_cheek","right_cheek","chin_contour" , "blur", "illumination", "face_probability"};
    private static Logger logger = LoggerFactory.getLogger(AipFaceUtils.class);
    static {
        client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(6000);
        registerTime = new Date();
    }
    public static AipFace getClient(){
        Date now = new Date();
        Long diff = now.getTime() - registerTime.getTime();
        int days = (int) (diff / (1000 * 60 * 60 * 24));
        if(days >= 30){
            client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(6000);
            registerTime = new Date();
        }
        return client;
    }

    public static String check(JSONObject json){
        //检查识别是否成功
        if(json.has("error_msg") && !"SUCCESS".equals(json.getString("error_msg"))){
            return json.getString("error_msg");
        }

        JSONObject res = json.getJSONObject("result").getJSONArray("face_list").getJSONObject(0);
        //检查是否是卡通人脸
        String faceType = res.getJSONObject("face_type").getString("type");
        if("cartoon".equals(faceType)){
            return "卡通人脸";
        }

        //检查五官是否遮挡、模糊度、光照
        Double probability = res.getDouble("face_probability");
        JSONObject quality = res.getJSONObject("quality");
        JSONObject occlusion = quality.getJSONObject("occlusion");

        for(int i = 0; i < 7; i++){
            double t = occlusion.getDouble(keys[i]);
            if(t > values[i]){
                return msgs[i];
            }
        }
        double t;
        t = quality.getDouble("blur");
        if(t > values[7]){
            return msgs[7];
        }
        t = quality.getDouble("illumination");
        if(t < values[8]){
            return msgs[8];
        }

        if(probability <= 0.8){
            return msgs[9];
        }
        return null;
    }

    /*
     * @Title: registerFace
     * @Description: 获取人脸照片的输入流，完成注册人脸功能，返回一个注册结果
     * @param img: 人脸照片输入流
     * @param patientId: 要注册的患者ID信息
     * @return org.json.JSONObject：注册结果
     * @Author: ShangJin
     * @Date: 2020/4/1
     */
    public static JSONObject registerFace(InputStream img, String patientId, String userGroupId) throws IOException {
        String imageCode = Base64Util.encode(img);
        AipFace client = AipFaceUtils.getClient();
        HashMap<String, String> options = new HashMap<>();
        options.put("action_type ", "REPLACE");
        logger.info("人脸注册：" + options.toString());
        return client.addUser(imageCode, "BASE64", userGroupId, patientId.toString(),options);
    }

    /*
     * @Title: detectFace
     * @Description: 获取人脸照片输入流，检查人脸照片是否合格
     * @param img: 人脸照片输入流
     * @return org.json.JSONObject：检测结果
     * @Author: ShangJin
     * @Date: 2020/4/1
     */
    public static JSONObject detectFace(InputStream img) throws IOException {
        String imageCode = Base64Util.encode(img);
        AipFace client = AipFaceUtils.getClient();
        HashMap<String, String> options = new HashMap<>();
        options.put("face_field", "quality,face_type");//质量检测
        return client.detect(imageCode, "BASE64", options);
    }

    /*
     * 新建人脸库
     */
    public static void addGroup(String groupId) throws Exception {
        HashMap<String, String> options = new HashMap<String, String>();
        // 创建用户组
        JSONObject res = client.groupAdd(groupId, options);

        //发生错误
        if(res.getInt("error_code") != 0){
            throw new Exception(res.getString("error_msg"));
        }
        logger.info("新建人脸库：" + groupId);
    }

    /*
     * 删除人脸库
     */
    public static void deleteGroup(String groupId) throws Exception {
        HashMap<String, String> options = new HashMap<String, String>();
        // 删除用户组
        JSONObject res = client.groupDelete(groupId, options);
        //发生错误
        if(res.getInt("error_code") != 0){
            throw new Exception(res.getString("error_msg"));
        }
        logger.info("删除人脸库：" + groupId);
    }

    public static List<String> faceSearch(InputStream file, List<String> groupList, @Nullable List<Double> score) throws Exception {
        StringBuilder groupIdList = new StringBuilder();
        for(int i = 0; i < groupList.size(); i++){
            if(i == 0){
                groupIdList.append(groupList.get(i));
            }else{
                groupIdList.append(",").append(groupList.get(i));
            }
        }

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("match_threshold", "85");
        options.put("liveness_control", "LOW");
        options.put("max_user_num", String.valueOf(groupList.size()));

        String image = Base64Util.encode(file);
        String imageType = "BASE64";
        // 人脸搜索
        JSONObject res = client.search(image, imageType, groupIdList.toString(), options);
        //检查是否出错
        if(res.has("error_msg") && !"SUCCESS".equals(res.getString("error_msg"))){
            throw new Exception(res.getString("error_msg"));
        }
        JSONArray userList = res.getJSONObject("result").getJSONArray("user_list");
        List<String> resIds = new ArrayList<>();
        if(score == null){
            for(int i = 0; i < userList.length(); i++){
                resIds.add(userList.getJSONObject(i).getString("user_id"));
            }
        }else{
            for(int i = 0; i < userList.length(); i++){
                JSONObject t = userList.getJSONObject(i);
                resIds.add(t.getString("user_id"));
                score.add(t.getDouble("score"));
            }
        }
        return resIds;
    }

}