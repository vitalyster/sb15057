package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DemoServer {
    private List<SseEmitter> sessions = new CopyOnWriteArrayList<>();

    private void sendSseEvent(String msg, String name) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.sessions.forEach(session -> {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter.event()
                        .name(name)
                        .data(msg);
                session.send(builder);
            } catch (Throwable e) {
                session.complete();
                deadEmitters.add(session);
            }
        });
        this.sessions.removeAll(deadEmitters);
    }

    @Scheduled(fixedDelay = 1000)
    void helloMessage() {
        sendSseEvent("Hello", "msg");
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoServer.class, args);
    }

    public List<SseEmitter> getSessions() {
        return sessions;
    }
}
