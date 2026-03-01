package com.pk.queryapi.util;

import com.pk.queryapi.db.EventQueryRepository.Cursor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.queryapi.db.EventQueryRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CursorCodec {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static String encode(Cursor c){
        try{
            String json = MAPPER.writeValueAsString(new Payload(c.createdAt().toString(), c.id()));
            return Base64.getUrlEncoder().withoutPadding().encodeToString((json.getBytes()));
        }catch(Exception ex){
            throw new IllegalArgumentException("Failed to encode cursor", ex);
        }
    }

    public static Cursor decode(String cursor){
        try{
            byte[] bytes = Base64.getUrlDecoder().decode(cursor);
            Payload p = MAPPER.readValue(new String(bytes, StandardCharsets.UTF_8), Payload.class);
            return new EventQueryRepository.Cursor(java.time.Instant.parse(p.createdAt), p.id);
        }catch(Exception ex){
            throw new IllegalArgumentException("Invalid cursor", ex);
        }
    }

    private static class Payload{
        public String createdAt;
        public long id;
        public Payload(){}
        public Payload(String createdAt, long id){
            this.createdAt = createdAt;
            this.id = id;
        }
    }


}
