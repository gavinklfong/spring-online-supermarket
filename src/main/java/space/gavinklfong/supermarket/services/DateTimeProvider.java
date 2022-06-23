package space.gavinklfong.supermarket.services;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DateTimeProvider {

    public Instant now() {
        return Instant.now();
    }
}
