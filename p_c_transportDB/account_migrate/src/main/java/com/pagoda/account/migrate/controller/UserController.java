package com.pagoda.account.migrate.controller;


import com.pagoda.account.common.result.Result;
import com.pagoda.account.common.util.ResultUtil;
import com.pagoda.account.migrate.dataobject.account.User;
import com.pagoda.account.migrate.service.account.IUserAccountRecordService;
import com.pagoda.account.migrate.service.account.UserService;
import com.pagoda.account.migrate.service.member.IMemberWalletRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/userAccount")
public class UserController {

   @Autowired
    private UserService userService;
    @Autowired
    private IUserAccountRecordService iUserAccountRecordService;

    @Autowired
    private IMemberWalletRecordService iMemberWalletRecordService;

    @GetMapping("/findUserById")
    public Result findUserById(Integer id) {
        User userByUserId = userService.findUserByUserId(1L);
        iMemberWalletRecordService.getMemberWalletRecordById("CM",null,null,null);
        Result result = ResultUtil.success(userByUserId);
        return result;
    }

    /**
     * 校验用户数据-要测试请先进行数据的删除
     * @return
     */
    @GetMapping("/del")
    public Result delUserById() {
        iUserAccountRecordService.getUserAccountRecordDtoList();
        Result result = ResultUtil.success();
        return result;
    }

}
