package com.shj.eids.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.PatientInformationExcelModel;
import com.shj.eids.service.PatientInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

/**
 * @ClassName: PatientInformationExcelListener
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-31 18:38
 **/
public class PatientInformationExcelListener extends AnalysisEventListener<PatientInformationExcelModel> {
    private ArrayList<PatientInformationExcelModel> list = new ArrayList<>();
    private Admin admin;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final int BATCH_COUNT = 10;
    private PatientInformationService service;

    public PatientInformationExcelListener(Admin admin, PatientInformationService service){
        this.service = service;
        this.admin = admin;
    }
    @Override
    public void invoke(PatientInformationExcelModel data, AnalysisContext context) {
        data.setReportingTime(new Date());
        list.add(data);
        logger.info("管理员ID:" + admin.getId() + ",导入患者数据:" + data.toString());
        if(list.size() >= BATCH_COUNT){
            service.addPatientInformationByExcelModel(list);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if(list.size() > 0){
            service.addPatientInformationByExcelModel(list);
        }
    }
}