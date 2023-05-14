package com.ezdigar.outbox.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/custom")
    @ResponseStatus(HttpStatus.OK)
    public void testGruelBox(@RequestBody String body){
        orderService.create(body);
    }
}
