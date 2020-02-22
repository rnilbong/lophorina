package org.rnilbong.lophorina.core.response;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public interface HttpResponseServlet {
    public static final String CRLF = "\r\n";
    public static final Logger logger = LoggerFactory.getLogger(HttpResponseServlet.class);

    void sendResponse(HttpResponse httpResponse, DataOutputStream dataOutputStream) throws IOException;
}
