package edu.bilak.setdemo.controller;

import edu.bilak.setdemo.dto.*;
import edu.bilak.setdemo.model.SETMessage;
import edu.bilak.setdemo.service.SETProtocolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class SETController
 * @since 03/06/2025 — 20.23
 **/
@RestController
@RequestMapping("/api/set")
public class SETController {
    private final SETProtocolService setService;

    public SETController(SETProtocolService setService) {
        this.setService = setService;
    }

    @PostMapping("/init/{participantName}")
    public ResponseEntity<ApiResponse<PublicKeyDto>> initializeParticipant(@PathVariable String participantName) throws Exception {
        setService.initializeKeys(participantName);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Ключі для " + participantName + " створені",
                        new PublicKeyDto(participantName, setService.getPublicKeyBase64(participantName)))
        );
    }

    @PostMapping("/load/{name}")
    public ResponseEntity<ApiResponse<Void>> load(@PathVariable String name) throws Exception {
        setService.loadKeys(name);
        return ResponseEntity.ok(new ApiResponse<>("success", "Ключі завантажені", null));
    }

    @PostMapping("/oaep/{enabled}")
    public ResponseEntity<ApiResponse<Void>> oaep(@PathVariable boolean enabled) {
        setService.setOAEPEnabled(enabled);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "OAEP " + (enabled ? "увімкнено" : "вимкнено"),
                null));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<SETMessage>> send(@RequestBody SendRequest request) throws Exception {
        SETMessage m = setService.createSecureMessage(request.getSender(), request.getMessage(), request.getRecipient());
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Повідомлення зашифровано", m)
        );
    }

    @PostMapping("/receive")
    public ResponseEntity<ApiResponse<ReceiveResultDto>> receive(@RequestBody ReceiveRequest request) throws Exception {
        String plain = setService.processSecureMessage(request.getMessage(), request.getRecipient());
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Повідомлення перевірено", new ReceiveResultDto(plain, request.getMessage().getSenderInfo()))
        );
    }

    @GetMapping("/publickey/{name}")
    public ResponseEntity<ApiResponse<PublicKeyDto>> publicKey(@PathVariable String name) {

        String keyB64 = setService.getPublicKeyBase64(name);

        if (keyB64 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("error", "Учасника не знайдено", null));
        }

        var dto = new PublicKeyDto(name, keyB64);
        return ResponseEntity.ok(new ApiResponse<>("success", "Ключ знайдено", dto));
    }
}
