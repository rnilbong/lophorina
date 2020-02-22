package org.rnilbong.lophorina.core.header;

import java.util.Optional;

public class HttpHeader {



    public enum TransferEncoding{
        NONE("none"), CHUNKED("chunked"), COMPRESS("compress"), DEFLATE("defalte"), GZIP("gzip"), IDENTITY("identity");

        private String entity;
        TransferEncoding(String entity){
            this.entity = entity;
        }

        public static TransferEncoding getEntity(String entity){
            return Optional.ofNullable(TransferEncoding.valueOf(entity.toUpperCase())).orElse(NONE);
        }
    }

}
