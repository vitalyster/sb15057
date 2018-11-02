package com.demo.api;

import com.demo.DemoServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class Events {
    private static final Logger logger = LoggerFactory.getLogger(Events.class);

    @Autowired
    private DemoServer server;

    @GetMapping("/api/events")
    public SseEmitter handle() {
        logger.info("client connected");
        SseEmitter emitter = new SseEmitter(600000L);
        server.getSessions().add(emitter);

        emitter.onCompletion(() -> endSession(emitter));
        emitter.onTimeout(() -> endSession(emitter));

        return emitter;
    }

    private void endSession(SseEmitter emitter) {
        server.getSessions().stream()
                .filter(s -> s.equals(emitter))
                .forEach(session -> server.getSessions().remove(session));
    }


}
