package org.rnilbong.lophorina.core.response;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;


public class HttpNormalResponseServlet implements HttpResponseServlet {

    @Override
    public void sendResponse(HttpResponse httpResponse, DataOutputStream dataOutputStream) throws IOException {
        StringBuilder outputHeader = new StringBuilder();
        outputHeader.append(httpResponse.getStatusLine())
                .append(CRLF);

        for (Header header : httpResponse.getAllHeaders()) {
            outputHeader.append(header.toString())
                    .append(CRLF);
        }

        String body = "";
        if (String.valueOf(httpResponse.getStatusLine().getStatusCode()).startsWith("2")) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            body = handler.handleResponse(httpResponse);
        }

        logger.debug(outputHeader + CRLF + body);

        dataOutputStream.writeBytes(outputHeader + CRLF);
        dataOutputStream.writeBytes(body);

        dataOutputStream.flush();
        dataOutputStream.close();
    }
}
