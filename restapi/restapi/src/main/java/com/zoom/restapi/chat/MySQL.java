package com.zoom.restapi.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MySQL {
    @Autowired
    ZoomTokensRepository zoomTokensRepository;

    public ZoomTokens create() {
        return zoomTokensRepository.save(new ZoomTokens("testid", "testaccess_token", "testrefresh_token"));
    }
}
