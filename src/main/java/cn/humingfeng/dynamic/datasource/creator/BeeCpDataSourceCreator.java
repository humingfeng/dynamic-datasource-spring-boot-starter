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

import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.beecp.BeeCpConfig;
import cn.humingfeng.dynamic.datasource.toolkit.ConfigMergeCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static cn.humingfeng.dynamic.datasource.support.DdConstants.BEECP_DATASOURCE;

/**
 * BeeCp数据源创建器
 *
 * @author HuMingfeng
 * @since 2020/5/14
 */
@Slf4j
@Data
@AllArgsConstructor
public class BeeCpDataSourceCreator implements DataSourceCreator {

    private static final ConfigMergeCreator<BeeCpConfig, BeeDataSourceConfig> MERGE_CREATOR = new ConfigMergeCreator<>("BeeCp", BeeCpConfig.class, BeeDataSourceConfig.class);

    private static Boolean beeCpExists = false;
    private static Method copyToMethod = null;

    static {
        try {
            Class.forName(BEECP_DATASOURCE);
            beeCpExists = true;
            copyToMethod = BeeDataSourceConfig.class.getDeclaredMethod("copyTo", BeeDataSourceConfig.class);
            copyToMethod.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }
    }

    private BeeCpConfig gConfig;

    @Override
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        BeeDataSourceConfig config = MERGE_CREATOR.create(gConfig, dataSourceProperty.getBeecp());
        config.setUsername(dataSourceProperty.getUsername());
        config.setPassword(dataSourceProperty.getPassword());
        config.setJdbcUrl(dataSourceProperty.getUrl());
        config.setPoolName(dataSourceProperty.getPoolName());
        String driverClassName = dataSourceProperty.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }
        if (Boolean.FALSE.equals(dataSourceProperty.getLazy())) {
            return new BeeDataSource(config);
        }
        BeeDataSource beeDataSource = new BeeDataSource();
        try {
            copyToMethod.invoke(config, beeDataSource);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return beeDataSource;
    }

    @Override
    public boolean support(DataSourceProperty dataSourceProperty) {
        Class<? extends DataSource> type = dataSourceProperty.getType();
        return (type == null && beeCpExists) || (type != null && BEECP_DATASOURCE.equals(type.getName()));
    }
}
