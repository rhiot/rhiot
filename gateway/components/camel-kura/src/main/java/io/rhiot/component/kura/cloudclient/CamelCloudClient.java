package io.rhiot.component.kura.cloudclient;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.message.KuraPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.rhiot.component.kura.cloudclient.KuraCloudClientConstants.CAMEL_KURA_CLOUD_MESSAGEID;
import static io.rhiot.component.kura.cloudclient.KuraCloudClientConstants.CAMEL_KURA_CLOUD_PRIORITY;
import static io.rhiot.component.kura.cloudclient.KuraCloudClientConstants.CAMEL_KURA_CLOUD_RETAIN;
import static io.rhiot.component.kura.cloudclient.KuraCloudClientConstants.CAMEL_KURA_CLOUD_QOS;
import static org.apache.camel.ServiceStatus.Started;

public class CamelCloudClient implements CloudClient {

    private final CamelContext camelContext;

    private final ProducerTemplate producerTemplate;

    private final String applicationId;

    public CamelCloudClient(CamelContext camelContext, String applicationId) {
        this.camelContext = camelContext;
        this.producerTemplate = camelContext.createProducerTemplate();
        this.applicationId = applicationId;
    }

    @Override
    public String getApplicationId() {
        return applicationId;
    }

    @Override
    public void release() {
    }

    @Override
    public boolean isConnected() {
        return camelContext.getStatus() == Started;
    }

    @Override
    public int publish(String topic, KuraPayload kuraPayload, int qos, boolean retain) throws KuraException {
        return publish(topic, kuraPayload, qos, retain, 5);
    }

    @Override
    public int publish(String topic, KuraPayload kuraPayload, int qos, boolean retain, int priority) throws KuraException {
        int kuraMessageId = Math.abs(new Random().nextInt());

        Map<String, Object> headers = new HashMap<>();
        headers.put(CAMEL_KURA_CLOUD_MESSAGEID, kuraMessageId);
        headers.put(CAMEL_KURA_CLOUD_QOS, qos);
        headers.put(CAMEL_KURA_CLOUD_RETAIN, retain);
        headers.put(CAMEL_KURA_CLOUD_PRIORITY, priority);

        producerTemplate.sendBodyAndHeaders(topic, kuraPayload, headers);
        return kuraMessageId;
    }

    @Override
    public int publish(String s, byte[] bytes, int i, boolean b, int i1) throws KuraException {
        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.setBody(bytes);
        return publish(s, kuraPayload, i, b);
    }

    @Override
    public int controlPublish(String s, KuraPayload kuraPayload, int i, boolean b, int i1) throws KuraException {
        return 0;
    }

    @Override
    public int controlPublish(String s, String s1, KuraPayload kuraPayload, int i, boolean b, int i1) throws KuraException {
        return 0;
    }

    @Override
    public int controlPublish(String s, String s1, byte[] bytes, int i, boolean b, int i1) throws KuraException {
        return 0;
    }

    @Override
    public void subscribe(final String topic, final int qos) throws KuraException {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(topic).
                            routeId(topic).
                            setHeader(CAMEL_KURA_CLOUD_QOS, constant(qos)).
                            to("seda:" + applicationId);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void controlSubscribe(String s, int i) throws KuraException {

    }

    @Override
    public void unsubscribe(String topic) throws KuraException {
        try {
            camelContext.stopRoute(topic);
            camelContext.removeRoute(topic);
        } catch (Exception e) {
            throw new KuraException(KuraErrorCode.INTERNAL_ERROR, e);
        }
    }

    @Override
    public void controlUnsubscribe(String s) throws KuraException {

    }

    @Override
    public void addCloudClientListener(CloudClientListener cloudClientListener) {

    }

    @Override
    public void removeCloudClientListener(CloudClientListener cloudClientListener) {

    }

    @Override
    public List<Integer> getUnpublishedMessageIds() throws KuraException {
        return null;
    }

    @Override
    public List<Integer> getInFlightMessageIds() throws KuraException {
        return null;
    }

    @Override
    public List<Integer> getDroppedInFlightMessageIds() throws KuraException {
        return null;
    }

}
