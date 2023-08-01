package org.ashe.security.infra;

/**
 * For Service Layer
 */
@SuppressWarnings("unused")
public class ServiceException extends RuntimeException {

    public ServiceException(){
        super();
    }

    public ServiceException(String msg){
        super(msg);
    }

    public ServiceException(String msg, Throwable cause){
        super(msg, cause);
    }

    public ServiceException(Throwable cause){
        super(cause);
    }

    @java.io.Serial
    private static final long serialVersionUID = -5205630128856061314L;
}