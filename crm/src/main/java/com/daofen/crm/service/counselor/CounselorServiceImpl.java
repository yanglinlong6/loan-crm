package com.daofen.crm.service.counselor;


import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.CompanyService;
import com.daofen.crm.service.counselor.dao.CounselorMapper;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.CompanyCounselorPO;
import com.daofen.crm.service.counselor.model.RoleBO;
import com.daofen.crm.service.counselor.model.RolePO;
import com.daofen.crm.utils.CollectionUtil;
import com.daofen.crm.utils.JSONUtil;
import com.daofen.crm.utils.JudgeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CounselorServiceImpl implements CounselorService {

    @Autowired
    private  CounselorMapper counselorMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void getCounselorPage(PageVO<CompanyCounselorBO> pageVO) {
        if(null == pageVO.getParam()){
            CompanyCounselorBO account = new CompanyCounselorBO();
            pageVO.setParam(account);
        }
        pageVO.getParam().setCompanyId(LoginUtil.getLoginUser().getCompanyId());
        pageVO.setData(counselorMapper.selectCounselorPage(pageVO));
        pageVO.setTotalCount(counselorMapper.selectCounselorPageCount(pageVO));
    }

    @Override
    public CompanyCounselorBO getCounselorByMobile(String mobile) {
        if(StringUtils.isBlank(mobile) || mobile.length() != 11)
            return null;
        CompanyCounselorPO counselorPO = counselorMapper.selectCounselorMobile(mobile);
        if(null == counselorPO){
            return null;
        }
        CompanyCounselorBO bo = JSONUtil.toJavaBean(counselorPO.toString(),CompanyCounselorBO.class);
        bo.setRole(roleService.getRole(bo.getRoleId()));
        bo.setCompany(companyService.getCompanyById(bo.getCompanyId()));
        return bo;
    }

    @Override
    public List<CompanyCounselorPO> getCounselorByCompanyId(Long companyId) {
        return counselorMapper.selectCounselorByCompanyId(companyId);
    }

    @Override
    public List<CompanyCounselorPO> getCounselorByShopId(Long shopId) {
        if(null == shopId || shopId < 0)
            return null;
        return counselorMapper.selectCounselorByShopId(shopId);
    }

    @Override
    public List<CompanyCounselorPO> getTeamCounselorByTeamId(Long teamId) {
        return counselorMapper.selectCounselorByTeamId(teamId);
    }

    @Override
    public void addCounselor(CompanyCounselorBO counselor) {
        if(null == counselor || StringUtils.isBlank(counselor.getMobile())){
            return;
        }
        CompanyCounselorPO counselorPO = counselorMapper.selectCounselorMobile(counselor.getMobile());
        if(null != counselorPO)
            throw new CrmException(ResultVO.ResultCode.FAIL,"账户手机号码以存在,请联系贵司管理员");

        if(null == counselor.getRoleId()){
            //初始化角色，如果没有穿角色id进来，则表示是新增公司（只用于新增城市分公司用）
            RolePO admin = roleService.initCompanyRole(counselor);
            counselor.setRoleId(admin.getId());
            counselor.setRole(JSONUtil.toJavaBean(admin.toString(),RoleBO.class));
            counselor.setCreateBy(LoginUtil.getLoginUser().getId().toString());
            counselor.setCreateDate(new Date());
        }
        counselor.setStatus(CrmConstant.Counselor.Status.ONLINE);
        counselorMapper.insertCounselor(counselor);
    }


    @Override
    public void updateCounselor(CompanyCounselorBO counselor) {
        if(null == counselor || StringUtils.isBlank(counselor.getMobile())){
            return;
        }
        counselor.setUpdateBy(LoginUtil.getLoginUser().getId().toString());
        counselor.setCreateDate(new Date());
        counselorMapper.updateCounselor(counselor);

        // 更新缓存
        CompanyCounselorBO bo = getCounselorByMobile(counselor.getMobile());
        if(null == bo){
            return;
        }
        String token = LoginUtil.generateToken(bo,"*");
        Set<String> keys = stringRedisTemplate.keys(token);
        if(CollectionUtil.isEmpty(keys))
            return;
        for(String key : keys){
            stringRedisTemplate.opsForValue().set(key,bo.toString(),12*60*60, TimeUnit.SECONDS);
        }
//        String[] keyArray = keys.toArray(new String[0]);
//        String key = keyArray[0];
//        stringRedisTemplate.opsForValue().set(key,bo.toString(),12*60*60, TimeUnit.SECONDS);
    }

    @Override
    public void delCounselor(Long id) {
//        CompanyCounselorPO counselor = counselorMapper.selectCounselorById(id);
//        if(null == counselor)
//            return;
        CompanyCounselorPO counselor = counselorMapper.selectCounselorById(id);
        if(null == counselor)
            return;
        RoleBO role = roleService.getRole(counselor.getRoleId());
        if(null == role || JudgeUtil.in(role.getType().intValue(),0,1,2))
            throw new CrmException(ResultVO.ResultCode.FAIL,"管理员账号不能删除!");
        counselorMapper.deleteCounselor(id);
//        RoleBO role = roleService.getRole(counselor.getRoleId());
//        if(null == role)
//            return;
//        roleService.delRolePermission(role.getId());
    }

	@Override
	public CompanyCounselorPO selectAdmin(Long id) {
		return counselorMapper.selectAdmin(id);
	}
}
