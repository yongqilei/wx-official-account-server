package com.ms.wx.message.annotation;

import com.ms.wx.message.MessageType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates the which type of messages the handler should handle. <br/>
 * Refer to {@link MessageType} for all supported message types.
 *
 * @since 1.0
 * @author yongqi.lei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MessageConsumer {
    MessageType value();
}
