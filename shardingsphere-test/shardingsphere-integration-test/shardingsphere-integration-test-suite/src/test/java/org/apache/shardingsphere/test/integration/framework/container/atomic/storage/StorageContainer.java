/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.integration.framework.container.atomic.storage;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.test.integration.env.DataSourceEnvironment;
import org.apache.shardingsphere.test.integration.env.EnvironmentPath;
import org.apache.shardingsphere.test.integration.env.database.DatabaseEnvironmentManager;
import org.apache.shardingsphere.test.integration.framework.container.atomic.AtomicContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap.Builder;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Storage container.
 */
public abstract class StorageContainer extends AtomicContainer {
    
    private Map<String, DataSource> actualDataSourceMap;
    
    @Getter
    private final DatabaseType databaseType;
    
    @Getter
    private final String scenario;
    
    public StorageContainer(final DatabaseType databaseType, final String dockerImageName, final boolean isFakedContainer, final String scenario) {
        super(databaseType.getName().toLowerCase(), dockerImageName, isFakedContainer);
        this.databaseType = databaseType;
        this.scenario = scenario;
    }
    
    @Override
    protected void configure() {
        withClasspathResourceMapping(EnvironmentPath.getInitSQLResourcePath(databaseType, scenario), "/docker-entrypoint-initdb.d/", BindMode.READ_ONLY);
    }
    
    /**
     * Get actual data source map.
     *
     * @return actual data source map
     */
    @SneakyThrows({IOException.class, JAXBException.class})
    public synchronized Map<String, DataSource> getActualDataSourceMap() {
        if (null == actualDataSourceMap) {
            Collection<String> dataSourceNames = DatabaseEnvironmentManager.getDatabaseNames(scenario);
            Builder<String, DataSource> builder = ImmutableMap.builder();
            dataSourceNames.forEach(each -> builder.put(each, createDataSource(each)));
            actualDataSourceMap = builder.build();
        }
        return actualDataSourceMap;
    }
    
    private DataSource createDataSource(final String dataSourceName) {
        HikariDataSource result = new HikariDataSource();
        result.setDriverClassName(DataSourceEnvironment.getDriverClassName(databaseType));
        result.setJdbcUrl(DataSourceEnvironment.getURL(databaseType, isFakedContainer() ? null : getHost(), getPort(), dataSourceName));
        result.setUsername(getUsername());
        result.setPassword(getPassword());
        result.setMaximumPoolSize(4);
        result.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        getConnectionInitSQL().ifPresent(result::setConnectionInitSql);
        return result;
    }
    
    protected abstract String getUsername();
    
    protected abstract String getPassword();
    
    protected Optional<String> getConnectionInitSQL() {
        return Optional.empty();
    }
    
    protected abstract int getPort();
}
