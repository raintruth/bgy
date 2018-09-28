package com.pagoda.account.migrate.service.account.impl;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.pagoda.account.common.util.BeanUtils;
import com.pagoda.account.migrate.dataobject.account.User;
import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.producerconsumer.base.constant.ProdConsWorkerConstants;
import com.pagoda.account.migrate.repository.account.UserBatchRepository;
import com.pagoda.account.migrate.repository.account.UserRepository;
import com.pagoda.account.migrate.service.account.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author Lixh
 * @Date: 2018/9/4 15:34
 * @Description:
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBatchRepository userBatchRepository;

    @Override
    public User findUserByUserId(Long userId) {
        return userRepository.findUserByUserId(userId);
    }


    /**
     用户ID, 自维护序列 	user_id	int(10)				memberId
     会员号, 唯一	member_id	varchar(32)				memberNum
     手机号	phone_no	int(11)				phoneNum	varchar(32)
     邮箱, 唯一	email	char(32)				memberEmail	varchar(64)
     真实姓名	real_name	varchar(32)				memberName
     身份证号	identity_card_no	char(44)				identityCardNum
     消费密码	consume_password	char(32)				consumePasswd	varchar(32)
     用户状态: N-正常, L-锁定, D-禁用, C-注销	status	char(1)				status
     所属品牌: P-百果园, G-果多美	brand_type	char(1)				belongBrand
     创建时间	create_time	datetime				registerTime	char(14)
     更新时间	update_time	datetime				modifyTime	char(14)

     * @return
     */
    @Override
    public int saveUserByMemberList(List<Member> memberList){
        List<User> userList = new ArrayList(memberList.size());

        for(Member member : memberList){
            User user = BeanUtils.fastClone(User.class);
            String randomStr15 = RandomUtil.randomNumbers(15);
            //TODO 用户ID, 自维护序列
            user.setUserId(member.getMemberId().longValue()+1);
            //String randomStr9 = RandomUtil.randomNumbers(9);
            //user.setUserId(Long.parseLong(randomStr9));

            user.setMemberId(Long.parseLong(member.getMemberId().toString()));
            if(Validator.isMobile(member.getPhoneNum()) ){
                user.setPhoneNo(member.getPhoneNum());
            }else {
                //user.setPhoneNo("无效手机号" + member.getPhoneNum());
                user.setPhoneNo("");
            }

            String memberEmail = member.getMemberEmail();
            user.setEmail(StrUtil.isEmpty(memberEmail) ? "" : memberEmail);
            user.setRealName(StrUtil.isEmpty(member.getMemberName()) ? "" : member.getMemberName());
            user.setIdentityCardNo(randomStr15);
            //TODO 密码加密的规则
            user.setConsumePassword(StrUtil.isEmpty(member.getConsumePasswd()) ? "" : member.getConsumePasswd());
            //TODO 用户状态: N-正常, L-锁定, D-禁用, C-注销
            user.setStatus("N");
            //TODO 所属品牌: P-百果园, G-果多美
            user.setBrandType("P");

            user.setCreateTime(DateUtil.parse(member.getRegisterTime(), DatePattern.PURE_DATETIME_PATTERN));
            user.setUpdateTime(DateUtil.parse(member.getModifyTime(), DatePattern.PURE_DATETIME_PATTERN));

            userList.add(user);
        }

        //userBatchRepository.insertBatch(userList);//userBatchRepository.batchInsert(userList);
        userBatchRepository.batchInsertIfExistUpdate(userList);
        System.out.println("保存了【"+memberList.get(0).getMemberId() + ","+ memberList.get(memberList.size()-1).getMemberId() + "】");

        return userList.size();
    }

    @Override
    public List<User> findUserPartList(Integer startMemberId, Integer endMemberId) {
        return userRepository.findUserPartList(startMemberId, endMemberId);

    }

    @Override
    public List<User> findUserPartList(String startUpdateTimeStr, String endUpdateTimeStr) {
        return userRepository.findUserPartList(startUpdateTimeStr, endUpdateTimeStr);
    }

    @Override
    public Long countUserPartList(String startUpdateTimeStr, String endUpdateTimeStr) {
        return userRepository.countUserPartList(startUpdateTimeStr, endUpdateTimeStr);
    }

    @Override
    public List<User> findUserPartListByMemberIds(String memberIdList) {
        return userRepository.findUserPartListByMemberIds(memberIdList);
    }

    @Override
    public Map<String, Object> findMemberIdInfoMapByPage(Pageable pageable) {
        List<User> userPageList = userRepository.findUserPageList(pageable);

        HashMap<String, Object> storageInfoMap = new HashMap<>(2);

        HashMap<String, Long> memberIdLinkUserIdMap = new HashMap<>(userPageList.size());
        ArrayList<Long> memberIdList = new ArrayList<>();
        for (User user : userPageList) {
            memberIdList.add(Long.parseLong(user.getMemberId().toString()));
            memberIdLinkUserIdMap.put(user.getMemberId().toString(), user.getUserId().longValue());
        }

        //String memberIdList = JdbcSqlUtil.getmemberIdList(memberIdList, "memberId", true);
        storageInfoMap.put(ProdConsWorkerConstants.UserAccount.GOODS_MAP_MEMBER_ID_LIST_KEY, memberIdList);

        storageInfoMap.put(ProdConsWorkerConstants.UserAccount.GOODS_MAP_MEMBER_ID_LINK_USER_ID_MAP_KEY, memberIdLinkUserIdMap);

        return storageInfoMap;
    }


}
