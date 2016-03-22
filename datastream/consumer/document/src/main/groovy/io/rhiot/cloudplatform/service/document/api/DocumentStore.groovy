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
package io.rhiot.cloudplatform.service.document.api

/**
 * Document stores can be used to perform CRUD operations on messages formatted as documents i.e. JSON-like
 * structures.
 */
interface DocumentStore {

    String save(String collection, Map<String, Object> pojo)

    /**
     * Returns the number of the documents in the given collection.
     *
     * @param collection to query against.
     * @return number of documents. Returns 0 for non-existing collections as well.
     */
    long count(String collection)

    Map<String, Object> findOne(String collection, String id)

    List<Map<String, Object>> findMany(String collection, List<String> ids)

    List<Map<String,Object>> findByQuery(String collection, Map<String, Object> queryBuilder)

    long countByQuery(CountByQueryOperation countByQueryOperation)

    void remove(String collection, String id)

}
