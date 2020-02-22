package org.rnilbong.lophorina.core.response;

import org.apache.http.Header;
import org.rnilbong.lophorina.core.header.HttpHeader;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponseSender {

    public void returnResponse(org.apache.http.HttpResponse httpResponse, DataOutputStream dataOutputStream) throws IOException {
        HttpHeader.TransferEncoding encoding = getTransferEncoding(httpResponse);

        HttpResponseServlet responseServlet = null;
        if(HttpHeader.TransferEncoding.NONE == encoding){
            responseServlet = new HttpNormalResponseServlet();
        }

        if(HttpHeader.TransferEncoding.CHUNKED == encoding){
            responseServlet = new HttpChunkedResponseServlet();
        }

        responseServlet.sendResponse(httpResponse, dataOutputStream);
    }

    private HttpHeader.TransferEncoding getTransferEncoding(org.apache.http.HttpResponse httpResponse){
        if(httpResponse.getEntity().isChunked()){
            return HttpHeader.TransferEncoding.CHUNKED;
        }

        return HttpHeader.TransferEncoding.NONE;
    }
}
