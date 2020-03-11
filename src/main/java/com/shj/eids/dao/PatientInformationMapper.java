package com.shj.eids.dao;

import com.shj.eids.domain.PatientInformation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PatientInformation
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-05 12:59
 **/
@Repository
@Mapper
public interface PatientInformationMapper {
    public void addPatientInformation(PatientInformationMapper information);
    public void deletePatientInformationById(Integer id);
    public void updatePatientInformation(PatientInformation information);
    public PatientInformation getPatientInformationByIdNumber(String idNumber);
    /*
     * @Title: getPatientInformation
     * @Description:
     * @param args:为Map类型，key可以的取值为：
     *              status: 患者的状态（轻微、危重、死亡、康复）
     *              locationProvin： 患者所在的省或直辖市
     *              locationCity: 患者所在的市（直辖市不填）
     *              startTime,endTime:患者确诊所在的时间区间
     *              epidemicId: 患者对应的疫情事件ID
     *              start, length: 分页用的参数
     * @return java.util.List<com.shj.eids.domain.PatientInformation>: 返回的PatientInformation中未封epidemicEvent属性
     * @Author: ShangJin
     * @Date: 2020/3/10
     */
    public List<PatientInformation> getPatientInformation(Map<String, Object> args);
    /*
     * @Title: getCount
     * @Description:
     * @param args:为Map类型，key可以的取值为：
     *              status（List<String>）: 患者的状态（轻微、危重、死亡、康复）
     *              locationProvince： 患者所在的省或直辖市
     *              locationCity: 患者所在的市（直辖市不填）
     *              startTime,endTime:患者确诊所在的时间区间
     *              epidemicId: 患者对应的疫情事件ID
     *              start, length: 分页用的参数
     * @return java.lang.Integer: 统计的符合条件的数目
     * @Author: ShangJin
     * @Date: 2020/3/10
     */
    public Integer getCount(Map<String, Object> args);
}
