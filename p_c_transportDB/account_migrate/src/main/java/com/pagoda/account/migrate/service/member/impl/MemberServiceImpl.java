package com.pagoda.account.migrate.service.member.impl;

import com.pagoda.account.migrate.dataobject.member.Member;
import com.pagoda.account.migrate.repository.member.MemberRepository;
import com.pagoda.account.migrate.service.member.IMemberService;
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
public class MemberServiceImpl implements IMemberService {


    @Autowired
    private MemberRepository memberRepository;


    public Page<Member> getMemberById() {
        Pageable pageable = PageRequest.of(0, 10000);
        Page<Member> pageableRecord = memberRepository.findAll(pageable);

        return pageableRecord;
    }

    @Override
    public Map<String, Object> findMinAndMaxAndCountModifyTime() {

        return memberRepository.findMinAndMaxAndCountModifyTime();
    }

    @Override
    public Map<String, Object> findMinAndMaxMemberId(Long startMemberId, Long totalCount) {
        return memberRepository.findMinAndMaxMemberId(startMemberId, totalCount);
    }

    @Override
    public Map<String, Object> findMinAndMaxAndCountMemberId() {
        return memberRepository.findMinAndMaxAndCountMemberId();
    }


    @Override
    public List<Member> findMemberPageList(String startUpdateTimeStr, String endUpdateTimeStr, Pageable pageable){
        log.info("findMemberPartList, startUpdateTimeStr={}, endUpdateTimeStr={}, pageable={}", startUpdateTimeStr, endUpdateTimeStr, pageable);
        List<Member> memberValidPartList = memberRepository.findMemberPageList(startUpdateTimeStr, endUpdateTimeStr, pageable);

        return memberValidPartList;
    }

    @Override
    public Long countMemberPartList(String startUpdateTimeStr, String endUpdateTimeStr) {
        return memberRepository.countMemberPartList(startUpdateTimeStr, endUpdateTimeStr);
    }

    @Override
    public List<Member> findMemberPartList(Integer startMemberId, Integer endMemberId){

        List<Member> memberValidPartList = memberRepository.findMemberPartList(startMemberId, endMemberId);

        return memberValidPartList;
    }

    @Override
    public List<Member> findMemberPageList(Long startMemberId, Long endMemberId, Pageable pageable){

        List<Member> memberPageList = memberRepository.findMemberPageList(startMemberId, endMemberId, pageable);

        return memberPageList;
    }

    @Override
    public List<Member> findMemberValidPartList(Integer startMemberId, Integer endMemberId, int minMainAccountBalance){

        List<Member> memberValidPartList = memberRepository.findMemberValidPartList(startMemberId, endMemberId, minMainAccountBalance);

        return memberValidPartList;
    }



}
