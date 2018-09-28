package com.pagoda.account.migrate.repository.base;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author  wfg
 * Version  1.1.0
 * @since   2018教师节 下午2:28:58
 */
@Slf4j
public abstract  class AbstractBatchDao<T> implements BatchDao<T> {

    @Qualifier("primaryJdbcTemplate")
    @Autowired
    protected JdbcTemplate primaryJdbcTemplate;

    @Qualifier("secondaryJdbcTemplate")
    @Autowired
    protected JdbcTemplate secondaryJdbcTemplate;

    @PersistenceContext
    protected EntityManager em;

    /**
     * // saveAll效率低下 要先查是否有数据再插入
     * @param list
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void batchInsert(List list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                em.persist(list.get(i));
                if (i % 100 == 0) {//一次一百条插入
                    em.flush();
                    em.clear();
                }
            }
            //log.info("save to DB success,list is {}",list.toString());
            log.info("save to DB success,list is {}",list.size());
        } catch (Exception e) {
            log.error("batch insert data failuer.");
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void batchUpdate(List list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                em.merge(list.get(i));
                if (i % 100 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            log.info("update data success,list is {}",list.toString());
        } catch (Exception e) {
            log.error("batch update data failuer.");
            e.printStackTrace();
        }
    }


    /**
     * Hibernate使用游标分页的一个通用查询分页方法
     * 描述
     * @param queryString
     * @param parameters
     * @param page
     * @return
     */
 /*    @SuppressWarnings("unchecked")
   public List findPageByQuery(final String queryString,
                               final Object[] parameters, final Page page) {

        Session session = (Session) em.getDelegate();

        Query query = session.createQuery(queryString);

        // 判断有无条件参数的情况
        if(parameters != null) {
            for(int i = 0; i < parameters.length; i++) {
                query.setParameter(i, parameters[i]);
            }
        }

        // 使用游标来得到总条数
        ScrollableResults sr = query.scroll();
        sr.last();
        int totalCount = sr.getRowNumber();

        // 索引加1
        int totalRec = totalCount + 1;
        page.setTotalCount(totalRec);

        int startIndex = (page.getPageNum() - 1) * page.getNumPerPage();

        query.setFirstResult(startIndex);
        query.setMaxResults(page.getNumPerPage());

        List reList = query.list();

        return reList;

*/
}
