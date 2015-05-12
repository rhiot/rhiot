package com.github.camellabs.component.pubnub;

public enum PubNubEndpointType {
	pubsub("pubsub"), presens("presens");

	private final String text;

	private PubNubEndpointType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
