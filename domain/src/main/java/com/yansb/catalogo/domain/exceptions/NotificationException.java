package com.yansb.catalogo.domain.exceptions;


import com.yansb.catalogo.domain.validation.handler.Notification;

public class NotificationException extends DomainException {
    public NotificationException(String aMessage, Notification notification) {
        super(aMessage, notification.getErrors());
    }

    public static NotificationException with(final String message, final Notification notification) {
        return new NotificationException(message, notification);
    }
}
