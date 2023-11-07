package com.daofen.admin.service.user;

import com.daofen.admin.AdminApplication;
import com.daofen.admin.basic.PageVO;
import com.daofen.admin.service.user.model.LinkOrgVO;
import com.daofen.admin.service.user.model.UserPO;
import com.daofen.admin.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    public void getList() {
        PageVO<UserPO> page = new PageVO<>();
        page.setSize(10);
        page.setStartIndex(1);
        userService.getList(page);
        System.out.println("page = " + JSONUtil.toString(page));
    }

    @Test
    public void linkOrg() {
        LinkOrgVO linkOrgVO = new LinkOrgVO();
        linkOrgVO.setOrgIds(Lists.newArrayList(1, 2, 3, 4));
        linkOrgVO.setUserId(1);
        userService.linkOrg(linkOrgVO);
    }
}
