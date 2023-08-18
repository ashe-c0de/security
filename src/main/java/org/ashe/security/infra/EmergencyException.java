package org.ashe.security.infra;

import lombok.Getter;

/**
 * For DingTalk warning
 */
@SuppressWarnings("unused")
public class EmergencyException extends RuntimeException{

    /**
     * developer phone number
     */
    @Getter
    private final String developer;
    /**
     * title
     */
    @Getter
    private final String title;

    public EmergencyException(String developer, String title){
        super();
        this.developer = developer;
        this.title = title;
    }

    public EmergencyException(String msg, String developer, String title){
        super(msg);
        this.developer = developer;
        this.title = title;
    }

    public EmergencyException(String msg, Throwable cause, String developer, String title){
        super(msg, cause);
        this.developer = developer;
        this.title = title;
    }

    public EmergencyException(Throwable cause, String developer, String title){
        super(cause);
        this.developer = developer;
        this.title = title;
    }
}
