package com.ms.wx.message;

import com.ms.wx.exception.UnimplementedMessageHandlerException;
import com.ms.wx.message.annotation.MessageConsumer;
import com.ms.wx.message.handler.MessageHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandlerProvider implements ApplicationContextAware {

    private Map<MessageType, MessageHandler> handlerMapping;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        Map<MessageType, MessageHandler> mapping = new HashMap<>();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MessageConsumer.class);

        beans.forEach((k, v) -> {
            Class<?> clz = v.getClass();
            if (MessageHandler.class.isAssignableFrom(clz) && clz.isAnnotationPresent(MessageConsumer.class)) {
                MessageConsumer annotation = clz.getAnnotation(MessageConsumer.class);
                // override if there are multiple message handlers handle one type of messages
                mapping.put(annotation.value(), (MessageHandler) v);
            }
        });

        this.handlerMapping = mapping;
    }

    public MessageHandler getMessageHandler(MessageType type) {
        MessageHandler handler = handlerMapping.get(type);
        if (handler == null) {
            // This type is not supported yet
            throw new UnimplementedMessageHandlerException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return handler;
    }
}
