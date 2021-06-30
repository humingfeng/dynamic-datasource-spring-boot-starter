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
package cn.humingfeng.dynamic.datasource.toolkit;

import cn.humingfeng.dynamic.datasource.ds.ItemDataSource;
import com.p6spy.engine.spy.P6DataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.experimental.UtilityClass;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Just a simple db util
 *
 * @author HuMingfeng
 * @since 3.4.0
 */
@UtilityClass
public class DatabasebUtils {

    private static boolean seataExist;
    private static boolean p6spyExist;

    static {
        try {
            Class.forName("io.seata.rm.datasource.DataSourceProxy");
            seataExist = true;
        } catch (ClassNotFoundException ignore) {
        }
        try {
            Class.forName("com.p6spy.engine.spy.P6DataSource");
            p6spyExist = true;
        } catch (ClassNotFoundException ignore) {
        }
    }

    /**
     * close db
     *
     * @param dataSource db
     */
    public void closeDataSource(DataSource dataSource) throws Exception {
        if (dataSource instanceof ItemDataSource) {
            ((ItemDataSource) dataSource).close();
        } else {
            if (seataExist && dataSource instanceof DataSourceProxy) {
                DataSourceProxy dataSourceProxy = (DataSourceProxy) dataSource;
                dataSource = dataSourceProxy.getTargetDataSource();
            }
            if (p6spyExist && dataSource instanceof P6DataSource) {
                Field realDataSourceField = P6DataSource.class.getDeclaredField("realDataSource");
                realDataSourceField.setAccessible(true);
                dataSource = (DataSource) realDataSourceField.get(dataSource);
            }
            Class<? extends DataSource> clazz = dataSource.getClass();
            Method closeMethod = clazz.getDeclaredMethod("close");
            closeMethod.invoke(dataSource);
        }
    }
}
