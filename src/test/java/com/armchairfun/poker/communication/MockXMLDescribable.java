package com.armchairfun.poker.communication;

import com.armchairfun.poker.common.XMLDescribable;

public class MockXMLDescribable implements XMLDescribable {

	private String id;

	public MockXMLDescribable(String id) {
		this.id = id;
	}

	public String toXML() {
		return "<mockObject id=\""+id+"\"/>";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
