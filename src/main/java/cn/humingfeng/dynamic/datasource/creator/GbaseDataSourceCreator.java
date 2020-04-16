/**
 * Copyright © 2020 organization humingfeng
 * <pre>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <pre/>
 */
package cn.humingfeng.dynamic.datasource.creator;

import cn.humingfeng.dynamic.datasource.exception.ErrorCreateDataSourceException;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.dbcp.Dbcp2Config;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 基础Gbase数据源创建器
 *
 * @author HuMingfeng
 * @since 2020/2/28
 */
@Data
@Slf4j
public class GbaseDataSourceCreator {

    private static final Log LOG = LogFactory.getLog(GbaseDataSourceCreator.class);

    private Dbcp2Config dbcp2Config;

    public GbaseDataSourceCreator(Dbcp2Config dbcp2Config) {
        this.dbcp2Config = dbcp2Config;
    }

    /**
     * 创建基础数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        try {
            Properties properties = new Properties();
            properties.setProperty("url",dataSourceProperty.getUrl());
            properties.setProperty("username",dataSourceProperty.getUsername());
            properties.setProperty("password",dataSourceProperty.getPassword());
            properties.setProperty("driverClassName",dataSourceProperty.getDriverClassName());

            properties.setProperty("initialSize","5");
            properties.setProperty("minIdle","5");
            properties.setProperty("maxIdle","20");
            properties.setProperty("maxTotal","100");
            properties.setProperty("maxWaitMillis","100000");
            properties.setProperty("defaultAutoCommit","true");
            properties.setProperty("removeAbandonedTimeout","600");
            properties.setProperty("removeAbandonedOnBorrow","true");
            properties.setProperty("removeAbandonedOnMaintenance","true");
            properties.setProperty("testWhileIdle","true");
            properties.setProperty("timeBetweenEvictionRunsMillis","60000");
            properties.setProperty("numTestsPerEvictionRun","5");
            properties.setProperty("minEvictableIdleTimeMillis","300000");

            if (dbcp2Config.getInitialSize() != null) {
                properties.setProperty("initialSize",String.valueOf(dbcp2Config.getInitialSize()));
            }
            if (dbcp2Config.getMinIdle() != null) {
                properties.setProperty("minIdle",String.valueOf(dbcp2Config.getMinIdle()));
            }
            if (dbcp2Config.getMaxIdle() != null) {
                properties.setProperty("maxIdle",String.valueOf(dbcp2Config.getMaxIdle()));
            }
            if (dbcp2Config.getMaxTotal() != null) {
                properties.setProperty("maxTotal",String.valueOf(dbcp2Config.getMaxTotal()));
            }
            if (dbcp2Config.getMaxWaitMillis() != null) {
                properties.setProperty("maxWaitMillis",String.valueOf(dbcp2Config.getMaxWaitMillis()));
            }
            if (dbcp2Config.getDefaultAutoCommit() != null && !dbcp2Config.getDefaultAutoCommit()) {
                properties.setProperty("defaultAutoCommit","false");
            }
            if (dbcp2Config.getRemoveAbandonedTimeout() != null) {
                properties.setProperty("removeAbandonedTimeout",String.valueOf(dbcp2Config.getRemoveAbandonedTimeout()));
            }
            if (dbcp2Config.getRemoveAbandonedOnBorrow() != null && !dbcp2Config.getRemoveAbandonedOnBorrow()) {
                properties.setProperty("removeAbandonedOnBorrow","false");
            }
            if (dbcp2Config.getRemoveAbandonedOnMaintenance() != null && !dbcp2Config.getRemoveAbandonedOnMaintenance()) {
                properties.setProperty("removeAbandonedOnMaintenance","false");
            }
            if (dbcp2Config.getTestWhileIdle() != null && !dbcp2Config.getTestWhileIdle()) {
                properties.setProperty("testWhileIdle","false");
            }
            if (dbcp2Config.getTimeBetweenEvictionRunsMillis() != null) {
                properties.setProperty("timeBetweenEvictionRunsMillis",String.valueOf(dbcp2Config.getTimeBetweenEvictionRunsMillis()));
            }
            if (dbcp2Config.getNumTestsPerEvictionRun() != null) {
                properties.setProperty("numTestsPerEvictionRun",String.valueOf(dbcp2Config.getNumTestsPerEvictionRun()));
            }
            if (dbcp2Config.getMinEvictableIdleTimeMillis() != null) {
                properties.setProperty("minEvictableIdleTimeMillis",String.valueOf(dbcp2Config.getMinEvictableIdleTimeMillis()));
            }

            BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(properties);

            LOG.info("dynamic-datasource create gbase database named " + dataSourceProperty.getPollName() + " succeed");
            return dataSource;
        } catch (Exception e) {
            LOG.error("dynamic-datasource create basic database named " + dataSourceProperty.getPollName() + " error");
            throw new ErrorCreateDataSourceException(
                    "dynamic-datasource create basic database named " + dataSourceProperty.getPollName() + " error");
        }
    }
}
