package com.ms.wx.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.wx.exception.WxServiceException;
import com.ms.wx.message.BaseMessage;
import com.ms.wx.service.WxCommonService;
import com.ms.wx.service.WxMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx")
public class WxServiceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxServiceEndpoint.class);

    private final WxCommonService wxCommonService;
    private final WxMessageService wxMessageService;

    public WxServiceEndpoint(WxCommonService wxCommonService, WxMessageService wxMessageService) {
        this.wxCommonService = wxCommonService;
        this.wxMessageService = wxMessageService;
    }

    @GetMapping
    public ResponseEntity<String> verify(String signature, String nonce, String timestamp, String echostr) {
        try {
            if (wxCommonService.checkSignature(signature, nonce, timestamp, echostr)) {
                return ResponseEntity.ok("success");
            }
        } catch (WxServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body("Unknown issue");
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE}, produces = { MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE })
    public ResponseEntity<BaseMessage> handleMessage(@RequestBody String request) throws JsonProcessingException {
        return ResponseEntity.ok(wxMessageService.handleMessage(request));
    }

}
