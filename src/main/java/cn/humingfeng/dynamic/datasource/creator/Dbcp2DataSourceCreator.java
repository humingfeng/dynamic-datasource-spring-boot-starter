/**
 * Copyright © 2019 organization humingfeng
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

import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.dbcp2.Dbcp2Config;
import cn.humingfeng.dynamic.datasource.toolkit.ConfigMergeCreator;
import lombok.SneakyThrows;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import static cn.humingfeng.dynamic.datasource.support.DdConstants.DBCP2_DATASOURCE;

/**
 * DBCP数据源创建器
 *
 * @author HuMingfeng
 * @since 2021/5/18
 */
public class Dbcp2DataSourceCreator extends cn.humingfeng.dynamic.datasource.creator.AbstractDataSourceCreator implements cn.humingfeng.dynamic.datasource.creator.DataSourceCreator, InitializingBean {

    private static final ConfigMergeCreator<Dbcp2Config, BasicDataSource> MERGE_CREATOR = new ConfigMergeCreator<>("Dbcp2", Dbcp2Config.class, BasicDataSource.class);

    private Dbcp2Config gConfig;

    @Override
    @SneakyThrows
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        BasicDataSource dataSource = MERGE_CREATOR.create(gConfig, dataSourceProperty.getDbcp2());
        dataSource.setUsername(dataSourceProperty.getUsername());
        dataSource.setPassword(dataSourceProperty.getPassword());
        dataSource.setUrl(dataSourceProperty.getUrl());
        String driverClassName = dataSourceProperty.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            dataSource.setDriverClassName(driverClassName);
        }
        if (Boolean.FALSE.equals(dataSourceProperty.getLazy())) {
            dataSource.start();
        }
        return dataSource;
    }

    @Override
    public boolean support(DataSourceProperty dataSourceProperty) {
        Class<? extends DataSource> type = dataSourceProperty.getType();
        return type == null || DBCP2_DATASOURCE.equals(type.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gConfig = properties.getDbcp2();
    }
}
