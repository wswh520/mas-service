//package com.wh.mas.controller.sms.send;
//
//import com.alibaba.fastjson.JSON;
//import com.wh.mas.model.Submit;
//import com.wh.mas.model.Tmpsubmit;
//import com.wh.mas.service.SysMessageService;
//import com.wh.mas.service.SysMessageTelephoneService;
//import com.wh.mas.util.MD5Util;
//import com.wh.mas.util.TransSubmit;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.UnsupportedEncodingException;
//
//@RestController
//@RequestMapping("/sms")
//@Slf4j
//public class tmpsubmitController {
//
//    @Autowired
//    private SysMessageService sysMessageService;
//    @Autowired
//    private SysMessageTelephoneService sysMessageTelephoneService;
//
//    private static String sign = "wHwvfNuH5";//签名编码。在云MAS平台『管理』→『接口管理』→『短信接入用户管理』获取。
//    private static String secretKey = "Hd@202001";//用户密码
//    private static String ecName = "东华工程科技股份有限公司";//企业名称
//    private static String apId = "hdkjdx";//接口账号用户名
//
//    @RequestMapping(value = "tmpsubmit",produces="text/html;charset=UTF-8;")
//    public void doDeal(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//
//        String mobiles = request.getParameter("mobiles");
//        String content = request.getParameter("content");//短信内容
//
//        String addSerial = "";
//        if(StringUtils.isNotBlank(mobiles) && StringUtils.isNotBlank(content)) {
//            TransSubmit ts = new TransSubmit();
//            String params = getParams(mobiles,content,addSerial);
//            // 保存发送信息
//            Integer id = sysMessageService.insertSysMessage(content,apId,addSerial);
//
//            String result = ts.doPost(params,"http://112.35.1.155:1992/sms/norsubmit");
////            String result = "{\"msgGroup\":\"\",\"rspcod\":\"InvalidUsrOrPwd\",\"success\":false}";;
//            // 保存反馈信息
//            sysMessageTelephoneService.saveSysMessageTelePhone(mobiles,id,result);
//
//            log.info("回调结果result================="+result);
//            response.getWriter().print(result);
//        }else{
//            log.info("手机号码或短信内容为空");
//            response.getWriter().print("手机号码或短信内容为空");
//        }
//    }
//
//
//    /**
//     * 模拟放款接口请求参数
//     * @throws Exception
//     *
//     */
//    private String getParams(String mobiles,String content,String addSerial) throws UnsupportedEncodingException {
//
//        Tmpsubmit submit = new Tmpsubmit();
//        String[] paramss = {};
//        submit.setApId(apId);
//        submit.setEcName(ecName);
//        submit.setSecretKey(secretKey);
//        submit.setParams(JSON.toJSONString(paramss));
//        submit.setMobiles(mobiles);
//        submit.setAddSerial("");
//        submit.setSign(sign);
//        submit.setTemplateId();
//
//
//        submit.setContent(content);
//        submit.setAddSerial(addSerial);
//
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(StringUtils.trimToEmpty(ecName)).append(StringUtils.trimToEmpty(apId));
//        buffer.append(StringUtils.trimToEmpty(secretKey)).append(StringUtils.trimToEmpty(mobiles));
//        buffer.append(StringUtils.trimToEmpty(content)).append(StringUtils.trimToEmpty(sign));
//        buffer.append(StringUtils.trimToEmpty(addSerial));
//
//        submit.setMac(MD5Util.MD5(buffer.toString()).toLowerCase());
//
//        String param = JSON.toJSONString(submit);
//        log.info("Base64加密前字符串:{}",param);
//        String encode = Base64.encodeBase64String(param.getBytes("UTF-8"));
//        log.info("Base64加密后字符串:{}",encode);
//
//        return encode;
//    }
//
//}
