package com.wh.mas.controller.sms.send;

import com.alibaba.fastjson.JSON;
import com.wh.mas.dao.SysMessageMapper;
import com.wh.mas.model.SendRes;
import com.wh.mas.model.Submit;
import com.wh.mas.service.SysMessageLogService;
import com.wh.mas.service.SysMessageService;
import com.wh.mas.service.SysMessageTelephoneService;
import com.wh.mas.util.MD5Util;
import com.wh.mas.util.TransSubmit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;


/**
 * 发送普通短信
 */
@RestController
@RequestMapping("/sms")
@Slf4j
public class NorsubmitController extends HttpServlet {

    @Autowired
    private SysMessageService sysMessageService;
    @Autowired
    private SysMessageTelephoneService sysMessageTelephoneService;
    @Autowired
    private SysMessageLogService sysMessageLogService;

//    private static String sign = "wHwvfNuH5";//签名编码。在云MAS平台『管理』→『接口管理』→『短信接入用户管理』获取。
    private static String sign = "gecFQ4Ku3";//签名编码。在云MAS平台『管理』→『接口管理』→『短信接入用户管理』获取。
    private static String secretKey = "Hd@20210114";//用户密码
    private static String ecName = "东华工程科技股份有限公司";//企业名称
    private static String apId = "hdkjdx";//接口账号用户名

    @RequestMapping(value = "norsubmitSms",produces="text/html;charset=UTF-8;")
    public void norsubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String mobiles = request.getParameter("mobiles");//手机号码
        String content = request.getParameter("content");//短信内容
        String addSerial = request.getParameter("addSerial");//拓展码

        if(checkNorsubmit(mobiles,content,addSerial,response)) {
            mobiles = mobiles.replaceAll("，",",");
            TransSubmit ts = new TransSubmit();
            String params = getParams(mobiles,content,addSerial);
            // 保存发送信息
            Integer id = sysMessageService.insertSysMessage(content,apId,addSerial);
            String result = ts.doPost("http://112.35.1.155:1992/sms/norsubmit",params,"");
            // 保存反馈信息
            sysMessageTelephoneService.saveSysMessageTelePhone(mobiles,id,result);
            // 保存日志信息
            sysMessageLogService.insertSysMessageLog("发送普通短信同步反馈",result);

            log.info("回调结果result================="+result);
            response.getWriter().print(result);
        }
    }

    public boolean checkNorsubmit(String mobiles,String content,String addSerial, HttpServletResponse response) throws IOException {
        if(StringUtils.isNotBlank(mobiles) && StringUtils.isNotBlank(content)
                && StringUtils.isNotBlank(addSerial)) {
            Pattern pattern = Pattern.compile("[0-9]*");
            if(pattern.matcher(addSerial).matches()){// 是数字
                if(addSerial.length() == 5){
                    return true;
                }else{
                    response.getWriter().print("拓展码应是5位数字");
                    return false;
                }
            }else{
                response.getWriter().print("拓展码应是5位数字");
                return false;
            }
        }else{
            response.getWriter().print("参数为空");
            return false;
        }
    }

    /**
     * 生成随机数
     * @param s
     * @return
     */
    public String getAddSerial(String s) {
        while (s.length() < 20) {
            s += (int) (Math.random() * 10);
        }
        log.info("随机数addSerial：{}",s);
        return s;
    }


    /**
     * 模拟放款接口请求参数
     * @throws Exception
     *
     */
    private String getParams(String mobiles,String content,String addSerial) throws UnsupportedEncodingException {

        Submit submit = new Submit();
        submit.setEcName(ecName);
        submit.setApId(apId);
        submit.setMobiles(mobiles);
        submit.setSecretKey(secretKey);
        submit.setContent(content);
        submit.setSign(sign);
        submit.setAddSerial(addSerial);

        StringBuffer buffer = new StringBuffer();
        buffer.append(StringUtils.trimToEmpty(ecName)).append(StringUtils.trimToEmpty(apId));
        buffer.append(StringUtils.trimToEmpty(secretKey)).append(StringUtils.trimToEmpty(mobiles));
        buffer.append(StringUtils.trimToEmpty(content)).append(StringUtils.trimToEmpty(sign));
        buffer.append(StringUtils.trimToEmpty(addSerial));

        submit.setMac(MD5Util.MD5(buffer.toString()).toLowerCase());

        String param = JSON.toJSONString(submit);
        log.info("Base64加密前字符串:{}",param);
        String encode = Base64.encodeBase64String(param.getBytes("UTF-8"));
        log.info("Base64加密后字符串:{}",encode);

        return encode;
    }

}
