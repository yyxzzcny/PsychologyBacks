package org.example.psychologyback.Controller;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.validation.Valid;
import org.example.psychologyback.AIService.PsychologySupportService;
import org.example.psychologyback.AIService.StructOutPut;
import org.example.psychologyback.DTO.comand.ConsultationSessionCreateDTO;
import org.example.psychologyback.DTO.comand.ConsultationStreamDTO;
import org.example.psychologyback.DTO.comand.UserRegisterCommandDTO;
import org.example.psychologyback.common.Result;
import org.example.psychologyback.common.ResultCode;
import org.example.psychologyback.unit.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class AIChat {
    @Autowired
    private PsychologySupportService psychologySupportService;
    @PostMapping("/session/start")
    public Result<StructOutPut.StreamChatSession> startChatSession(@Valid @RequestBody ConsultationSessionCreateDTO SessionCreateDTO) {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        StructOutPut.StreamChatSession session = psychologySupportService.StartSession(userId, SessionCreateDTO);
        return Result.ok(session);
    }
    @PostMapping(value="/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ConsultationStreamDTO streamDTO) {
        Long userId = JwtTokenUtil.getCurrentUserId();
        if(userId == null){
            return Flux.just(ServerSentEvent.<String>builder()
                            .event("error")
                            .data(JSONUtil.toJsonStr(JSONUtil.toJsonStr(Result.error(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(),null))))
                             .build());
                   }
        return psychologySupportService.streamPsychologicalChat(streamDTO.getSessionId(),streamDTO.getInitialMessage())
                .map(Fragment ->{
                    return ServerSentEvent.<String>builder()
                            .event("message")
                            .data(JSONUtil.toJsonStr(Result.ok(Map.of("content", Fragment, "type", "normal"))))
                            .build();
                })
                .concatWith(Flux.just(ServerSentEvent.<String>builder()
                        .event("done")
                        .data("{}")
                        .build()
                ))
                .delayElements(Duration.ofMillis(30));

    }
}
