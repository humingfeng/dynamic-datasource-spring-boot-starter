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

import cn.humingfeng.dynamic.datasource.exception.ErrorCreateDataSourceException;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * 基础数据源创建器
 *
 * @author HuMingfeng
 * @since 2020/1/21
 */
@Data
@Slf4j
public class BasicDataSourceCreator implements DataSourceCreator {

    private static Method createMethod;
    private static Method typeMethod;
    private static Method urlMethod;
    private static Method usernameMethod;
    private static Method passwordMethod;
    private static Method driverClassNameMethod;
    private static Method buildMethod;

    static {
        //to support springboot 1.5 and 2.x
        Class<?> builderClass = null;
        try {
            builderClass = Class.forName("org.springframework.boot.jdbc.DataSourceBuilder");
        } catch (Exception ignored) {
        }
        if (builderClass == null) {
            try {
                builderClass = Class.forName("org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder");
            } catch (Exception e) {
                log.warn("not in springBoot ENV,could not create BasicDataSourceCreator");
            }
        }
        if (builderClass != null) {
            try {
                createMethod = builderClass.getDeclaredMethod("create");
                typeMethod = builderClass.getDeclaredMethod("type", Class.class);
                urlMethod = builderClass.getDeclaredMethod("url", String.class);
                usernameMethod = builderClass.getDeclaredMethod("username", String.class);
                passwordMethod = builderClass.getDeclaredMethod("password", String.class);
                driverClassNameMethod = builderClass.getDeclaredMethod("driverClassName", String.class);
                buildMethod = builderClass.getDeclaredMethod("build");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建基础数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    @Override
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        try {
            Object o1 = createMethod.invoke(null);
            Object o2 = typeMethod.invoke(o1, dataSourceProperty.getType());
            Object o3 = urlMethod.invoke(o2, dataSourceProperty.getUrl());
            Object o4 = usernameMethod.invoke(o3, dataSourceProperty.getUsername());
            Object o5 = passwordMethod.invoke(o4, dataSourceProperty.getPassword());
            Object o6 = driverClassNameMethod.invoke(o5, dataSourceProperty.getDriverClassName());
            DataSource dataSource = (DataSource) buildMethod.invoke(o6);
            log.info("dynamic-datasource create basic database named " + dataSourceProperty.getPoolName() + " succeed");
            return dataSource;
        } catch (Exception e) {
            log.error("dynamic-datasource create basic database named " + dataSourceProperty.getPoolName() + " error");
            throw new ErrorCreateDataSourceException(
                    "dynamic-datasource create basic database named " + dataSourceProperty.getPoolName() + " error");
        }
    }

    @Override
    public boolean support(DataSourceProperty dataSourceProperty) {
        return true;
    }
}
