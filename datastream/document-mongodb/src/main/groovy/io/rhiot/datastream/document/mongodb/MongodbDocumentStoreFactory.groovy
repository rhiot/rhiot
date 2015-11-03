/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.datastream.document.mongodb

import com.mongodb.Mongo
import io.rhiot.steroids.Bean
import io.rhiot.steroids.Steroids

import static io.rhiot.utils.Properties.stringProperty

class MongodbDocumentStoreFactory {

    @Bean
    MongodbDocumentStore mongodbDocumentStore() {
        new MongodbDocumentStore(
                Steroids.bean(Mongo.class).get(),
                stringProperty('cloudlet.document.driver.mongodb.db', 'cloudlet_document')
        )
    }

}
