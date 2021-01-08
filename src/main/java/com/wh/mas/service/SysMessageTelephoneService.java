package com.wh.mas.service;

import com.wh.mas.dao.SysMessageTelephoneMapper;
import com.wh.mas.model.Result;
import com.wh.mas.model.SysMessageTelephone;
import com.wh.mas.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SysMessageTelephoneService {
    @Autowired
    private SysMessageTelephoneMapper sysMessageTelephoneMapper;

    public void saveSysMessageTelePhone(String telephone,Integer messageid,String errormsg){
        JsonUtil jsonUtil = new JsonUtil();
        // 解析返回json信息{"msgGroup":"0927152506001000833076","rspcod":"success","success":true}
        // 发送状态：0未提交，1提交成功，2提交失败，3发送成功，4发送失败
        Integer re = 0;
        Result result = jsonUtil.getResult(errormsg);
        if(result == null){
            re = 2;
        }else if(result.getSuccess()){
            re = 3;
        }else{
            re = 4;
        }
        sysMessageTelephoneMapper.saveSysMessageTelephone(telephone,re,messageid,errormsg);
    }

    public static void main(String[] args) {

    }


}
