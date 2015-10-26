/*
 * *
 *  * Licensed to the Rhiot under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 *  
 */

package com.github.camellabs.iot.cloudlet.webcam.service;

import com.github.camellabs.iot.cloudlet.webcam.domain.Webcam;
import com.github.camellabs.iot.cloudlet.webcam.domain.WebcamImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component("webcamService")
public class DefaultWebcamService implements WebcamService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebcamService.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DefaultWebcamService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> devices() {
        return mongoTemplate.getDb().getCollection(Webcam.class.getSimpleName()).distinct("deviceId");
    }

    @Override
    public Webcam webcam(String id) {
        return mongoTemplate.findById(id, Webcam.class);
    }
    
    @Override
    public List<Webcam> webcams(String device) {

        Query query = new Query().addCriteria(where("deviceId").is(device));
        return mongoTemplate.find(query, Webcam.class);
    }

    @Override
    public WebcamImage latestImage(String deviceId, String webcamName) {
        Query query = new Query();
        query.limit(1);
        query.with(new Sort(Sort.Direction.DESC, "timestamp"));
        query.addCriteria(where("deleted").is(null)).addCriteria(where("deviceId").is(deviceId));
        return mongoTemplate.findOne(query, WebcamImage.class);
    }
    
    
}
