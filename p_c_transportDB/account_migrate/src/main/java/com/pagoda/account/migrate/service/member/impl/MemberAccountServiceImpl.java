package com.pagoda.account.migrate.service.member.impl;

import com.pagoda.account.migrate.dataobject.member.MemberAccount;
import com.pagoda.account.migrate.repository.member.MemberAccountRepository;
import com.pagoda.account.migrate.service.member.IMemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wfg
 * @version 1.0.0 2018-09-07
 */
@Slf4j
@Service
public class MemberAccountServiceImpl implements IMemberAccountService {


    @Autowired
    private MemberAccountRepository memberAccountRepository;


    public Page<MemberAccount> getMemberAccountById() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<MemberAccount> pageableRecord = memberAccountRepository.findAll(pageable);

        return pageableRecord;
    }

    @Override
    public List<MemberAccount> getMemberAccountList() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<MemberAccount> pageableRecord = memberAccountRepository.findAll(pageable);

        return pageableRecord.getContent();
    }

    @Override
    public List<MemberAccount> getMemberAccountList(int taskNum) {
        Pageable pageable = PageRequest.of(taskNum, 100);
        Page<MemberAccount> pageableRecord = memberAccountRepository.findAll(pageable);

        return pageableRecord.getContent();
    }

    @Override
    public Map<String, Object> findMinAndMaxAndCountModifyTime(Integer minMainAccountBalance) {

        return memberAccountRepository.findMinAndMaxAndCountModifyTime(minMainAccountBalance);
    }

    @Override
    public Map<String, Object> findMinAndMaxAndCountMemberId(Integer minMainAccountBalance) {

        return memberAccountRepository.findMinAndMaxAndCountMemberId(minMainAccountBalance);
    }

    @Override
    public List<MemberAccount> findMemberAccountValidPageList(String startUpdateTimeStr, String endUpdateTimeStr, int minMainAccountBalance, Pageable pageable){

        List<MemberAccount> memberAccountValidPartList = memberAccountRepository.findMemberAccountValidPageList(startUpdateTimeStr, endUpdateTimeStr, minMainAccountBalance, pageable);

        return memberAccountValidPartList;
    }

    @Override
    public List<MemberAccount> findMemberAccountValidPageList(int minMainAccountBalance, List<Long> memberIdList){

        List<MemberAccount> memberAccountValidPartList = memberAccountRepository.findMemberAccountValidPageList(minMainAccountBalance, memberIdList);

        return memberAccountValidPartList;
    }

    @Override
    public List<MemberAccount> findMemberAccountValidPageList(String startUpdateTimeStr, String endUpdateTimeStr, int minMainAccountBalance, List<Long> memberIdList, Pageable pageable){

        List<MemberAccount> memberAccountValidPartList = memberAccountRepository.findMemberAccountValidPageList(startUpdateTimeStr, endUpdateTimeStr, minMainAccountBalance, memberIdList, pageable);

        return memberAccountValidPartList;
    }

    @Override
    public Long countMemberAccountValidPartList(String startUpdateTimeStr, String endUpdateTimeStr, int minMainAccountBalance){

        return memberAccountRepository.countMemberAccountValidPartList(startUpdateTimeStr, endUpdateTimeStr, minMainAccountBalance);

    }

    @Override
    public List<MemberAccount> findMemberAccountValidPartList(Integer startMemberId, Integer endMemberId, int minMainAccountBalance){

        List<MemberAccount> memberAccountValidPartList = memberAccountRepository.findMemberAccountValidPartList(startMemberId, endMemberId, minMainAccountBalance);

        return memberAccountValidPartList;
    }


}
