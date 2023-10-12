package com.crm.controller.org;

import com.crm.common.CrmConstant;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.esign.model.OrgESignPO;
import com.crm.service.org.OrgService;
import com.crm.service.org.ShopService;
import com.crm.service.org.TeamService;
import com.crm.service.org.model.OrgPO;
import com.crm.service.org.model.OrgRegisterPO;
import com.crm.service.org.model.ShopPO;
import com.crm.service.org.model.TeamPO;
import com.crm.util.JudgeUtil;
import com.crm.util.QRCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.OutputStream;

@RestController
public class OrgController {

    private static final Logger LOG = LoggerFactory.getLogger(OrgController.class);

    @Autowired
    private OrgService orgService;


    @Autowired
    CacheConfigService cacheConfigService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/org/get")
    @ResponseBody
    public ResultVO getOrg(HttpServletRequest request){
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        OrgPO po = orgService.getOrgById(employee.getOrgId());
        po.setLoginMobile(employee.getPhone());
        po.setLoginName(employee.getName());
        po.setRoleName(employee.getRole().getName());
        return ResultVO.success("获取机构授权信息成功",po);
    }


    /**
     *
     * @return
     */
    @RequestMapping("/org/config/get")
    @ResponseBody
    public ResultVO getOrgConfig(){
        OrgPO orgPO = orgService.getOrgById(LoginUtil.getLoginEmployee().getOrgId());
        if(null != orgPO && null == orgPO.getOrgESign()){
            orgPO.setOrgESign(new OrgESignPO());
        }
        return ResultVO.success("获取机构配置信息成功",orgPO);
    }

    /**
     * 修改机构配置信息
     * @param orgPO
     * @return
     */
    @RequestMapping("/org/config/update")
    @ResponseBody
    public ResultVO updateOrg(@RequestBody() OrgPO orgPO){
        if(null == orgPO || !JudgeUtil.isNumber(orgPO.getId()) || orgPO.getId() <= 0){
            return ResultVO.success("修改机构配置信息失败:参数错误,非法请求",null);
        }
        orgService.updateOrg(orgPO);
        return ResultVO.success("修改机构配置信息成功",null);
    }


    @RequestMapping("/org/qrcode")
    @ResponseBody
    public ResultVO getQRCode(){
        OrgPO orgPO = orgService.getOrgById(LoginUtil.getLoginEmployee().getOrgId());
        if(null == orgPO || StringUtils.isBlank(orgPO.getQrcode())){
            String dir = cacheConfigService.getCacheConfigValue(CrmConstant.Config.Upload.UPLOAD,CrmConstant.Config.Upload.SOURCE_DIC);
            if(StringUtils.isBlank(dir)){
                return ResultVO.fail("配置目录未设置,请联系管理员",null);
            }
            dir = dir.endsWith("/")?dir:dir+"/";
            String fileName = "qrcode"+"_"+orgPO.getId()+".png";
            String path = dir+fileName;

            String value = cacheConfigService.getCacheConfigValue("org_qrcode","redirect_url");
            if(StringUtils.isBlank(value)){
                return ResultVO.fail("跳转未设置,请联系管理员",null);
            }
            String url = String.format(value,orgPO.getId(),orgPO.getName());
            OutputStream os = null;
            try {
                os = new FileOutputStream(path);
                os.write(QRCodeUtil.createQRCode(200,200,url));
            }catch (Exception e){
                LOG.error("机构[{}]生成二维码异常:",orgPO.getId(),e.getMessage(),e);
                return ResultVO.fail("机构生成二维码异常",null);
            }finally {
                if(null != os){
                    try {
                        os.flush();
                        os.close();
                    }catch (Exception e1){}
                }
            }
            String domain = cacheConfigService.getCacheConfigValue(CrmConstant.Config.Upload.UPLOAD,CrmConstant.Config.Upload.DOMAIN2);
            orgPO.setQrcode(domain+fileName);
            orgService.updateOrg(orgPO);
        }
        return ResultVO.success("生成机构二维码成功",orgPO.getQrcode());
    }


    /**
     * 上门登记
     * @param orgId 机构id
     * @param mobile 客户手机号码
     * @return ResponseBody
     *     public ResultVO
     */
    @RequestMapping("/org/dengji")
    @ResponseBody
    public ResultVO dengji(@RequestParam("orgId") Long orgId,@RequestParam("mobile") String mobile) {
        orgService.addRegister(new OrgRegisterPO(orgId,mobile));
        return ResultVO.success("登记成功,为保障您的权益,请勿私下和客户经理交易",null);
    }

    @Autowired
    ShopService shopService;

    @Autowired
    TeamService teamService;
    /**
     * 获取当前登陆对象状态
     * @return
     */
    @RequestMapping("/switch/get")
    @ResponseBody
    public ResultVO getSwitch(){
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();

        if(stringRedisTemplate.opsForHash().hasKey(CrmConstant.Switch.KEY,employeeBO.getOrgId().toString())){
            Object value = stringRedisTemplate.opsForHash().get(CrmConstant.Switch.KEY,employeeBO.getOrgId().toString());
            if(null == value){
                stringRedisTemplate.opsForHash().put(CrmConstant.Switch.KEY,employeeBO.getOrgId().toString(),CrmConstant.Switch.OFF);
                return ResultVO.success("获取开关状态成功",CrmConstant.Switch.OFF);
            }
            return ResultVO.success("获取开关状态成功",Byte.valueOf(value.toString()));
        }
        stringRedisTemplate.opsForHash().put(CrmConstant.Switch.KEY,employeeBO.getOrgId().toString(),CrmConstant.Switch.OFF);
        return ResultVO.success("获取开关状态成功",CrmConstant.Switch.OFF);
    }

    @RequestMapping("/switch/state")
    @ResponseBody
    public ResultVO switchState(@RequestParam("state")Byte state){
        if(null == state || !JudgeUtil.in(state,CrmConstant.Switch.NO,CrmConstant.Switch.OFF)){
            return ResultVO.fail("非法请求",null);
        }
        stringRedisTemplate.opsForHash().put(CrmConstant.Switch.KEY,LoginUtil.getLoginEmployee().getOrgId().toString(),state.byteValue());
        if(CrmConstant.Switch.NO == state) // 如果开启休息模式,则清理员工登陆状态
            LoginUtil.updateLoginRedis(LoginUtil.getLoginEmployee().getOrgId(),null);
        return ResultVO.success("操作开关成功",null);
    }




}
