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

package org.apache.shardingsphere.test.integration.engine.dcl;

import org.apache.shardingsphere.test.integration.engine.SingleITCase;
import org.apache.shardingsphere.test.integration.env.EnvironmentPath;
import org.apache.shardingsphere.test.integration.env.authority.AuthorityEnvironmentManager;
import org.apache.shardingsphere.test.integration.framework.param.model.AssertionParameterizedArray;
import org.junit.Before;

public abstract class BaseDCLIT extends SingleITCase {
    
    private AuthorityEnvironmentManager authorityEnvironmentManager;
    
    public BaseDCLIT(final AssertionParameterizedArray parameterizedArray) {
        super(parameterizedArray);
    }
    
    @Before
    public final void init() throws Exception {
        authorityEnvironmentManager = new AuthorityEnvironmentManager(EnvironmentPath.getAuthorityFile(getScenario()), getActualDataSourceMap(), getDatabaseType());
        authorityEnvironmentManager.initialize();
    }
    
    @Override
    public final void tearDown() throws Exception {
        authorityEnvironmentManager.clean();
        super.tearDown();
    }
}
