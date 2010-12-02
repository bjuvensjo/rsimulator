package org.rsimulator.example.unittest;

import java.io.IOException;

public interface WebServiceClient {

    String greet(Greeting greeting) throws IOException;
}
