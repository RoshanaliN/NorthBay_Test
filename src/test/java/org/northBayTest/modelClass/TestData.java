package org.northBayTest.modelClass;

public class TestData {
  private String request;
  private String response;

  // Getters and setters
  public String getRequest() {
    return request;
  }

  public void setRequest(String request) {
    this.request = request;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  @Override
  public String toString() {
    return "TestData{" +
        "request='" + request + '\'' +
        ", response='" + response + '\'' +
        '}';
  }
}

