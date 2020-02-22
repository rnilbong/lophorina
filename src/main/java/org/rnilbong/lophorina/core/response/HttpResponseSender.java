package org.rnilbong.lophorina.core.response;

import org.apache.http.Header;
import org.rnilbong.lophorina.core.header.HttpHeader;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponseSender {

    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    public void returnResponse(org.apache.http.HttpResponse httpResponse, DataOutputStream dataOutputStream) throws IOException {
        HttpHeader.TransferEncoding encoding = getTransferEncoding(httpResponse);

        HttpResponseServlet responseServlet = null;
        if(HttpHeader.TransferEncoding.NONE == encoding){
            responseServlet = new HttpNormalResponseServlet();
        }

        if(HttpHeader.TransferEncoding.CHUNKED == encoding){
            responseServlet = new HttpCheckedResponseServlet();
        }

        responseServlet.sendResponse(httpResponse, dataOutputStream);
    }

    private HttpHeader.TransferEncoding getTransferEncoding(org.apache.http.HttpResponse httpresponse){
        Header header = httpresponse.getFirstHeader(TRANSFER_ENCODING);
        return header == null ? HttpHeader.TransferEncoding.NONE : HttpHeader.TransferEncoding.getEntity(header.getValue());
    }
}
