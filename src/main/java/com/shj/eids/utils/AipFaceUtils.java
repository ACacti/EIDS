package com.shj.eids.utils;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

}