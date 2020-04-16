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
package cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.dbcp;

import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.druid.DruidSlf4jConfig;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.druid.DruidStatConfig;
import cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.druid.DruidWallConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static cn.humingfeng.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConsts.*;
import static com.alibaba.druid.pool.DruidAbstractDataSource.*;

/**
 * Dbcp2参数配置
 *
 * @author HuMingfeng
 * @since 1.2.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Slf4j
public class Dbcp2Config {

    private Integer initialSize;
    private Integer minIdle;
    private Integer maxIdle;
    private Integer maxTotal;
    private Long maxWaitMillis;
    private Boolean defaultAutoCommit;
    private Long removeAbandonedTimeout;
    private Boolean removeAbandonedOnBorrow;
    private Boolean removeAbandonedOnMaintenance;
    private Boolean testWhileIdle;
    private Long timeBetweenEvictionRunsMillis;
    private Integer numTestsPerEvictionRun;
    private Long minEvictableIdleTimeMillis;

}
