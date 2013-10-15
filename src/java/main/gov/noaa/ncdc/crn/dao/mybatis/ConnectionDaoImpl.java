package gov.noaa.ncdc.crn.dao.mybatis;

import gov.noaa.ncdc.crn.dao.ConnectionDao;
import gov.noaa.ncdc.crn.persistence.ConnectionMapper;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class ConnectionDaoImpl implements ConnectionDao {
    @Autowired
    private SqlSessionFactory sessionFactory;
    @Autowired
    private ConnectionMapper mapper;

    @Override
    public boolean testConnection() {
        return mapper.test() == 1;
    }

    @Override
    public void flushDataCache() throws DataAccessException {
        Configuration configuration = sessionFactory.getConfiguration();

        Collection<Cache> caches = configuration.getCaches();
        for (Cache cache : caches) {
            Lock w = cache.getReadWriteLock().writeLock();
            w.lock();
            try {
                cache.clear();
            } finally {
                w.unlock();
            }
        }
    }

}
