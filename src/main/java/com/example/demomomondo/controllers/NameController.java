package com.example.demomomondo.controllers;

import com.example.demomomondo.dtos.Age;
import com.example.demomomondo.dtos.Gender;
import com.example.demomomondo.dtos.NameResponse;
import com.example.demomomondo.dtos.Nationality;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class NameController {

    @RequestMapping("/name-info")
    public NameResponse getDetails(@RequestParam String name){

        Mono<Age> age = getAgeForName(name);
        Mono<Gender> gender = getGenderForName(name);
        Mono<Nationality> nationality = getNationalityForName(name);

        var resMono = Mono.zip(age,gender,nationality).map(t-> {
            NameResponse ns = new NameResponse();
            ns.setAge(t.getT1().getAge());
            ns.setAgeCount(t.getT1().getCount());

            ns.setGender(t.getT2().getGender());
            ns.setGenderProbability(t.getT2().getProbability());

            ns.setCountry(t.getT3().getCountry().get(0).getCountry_id());
            ns.setCountryProbability(t.getT3().getCountry().get(0).getProbability());

            return ns;
        });

        NameResponse res = resMono.block();
        res.setName(name);

        return res;
    }

    Mono<Age> getAgeForName(String name) {
        WebClient client = WebClient.create();
        Mono<Age> age = client.get()
                .uri("https://api.agify.io?name="+name)
                .retrieve()
                .bodyToMono(Age.class);
        return age;
    }

    Mono<Gender> getGenderForName(String name) {
        WebClient client = WebClient.create();
        Mono<Gender> gender = client.get()
                .uri("https://api.genderize.io?name="+name)
                .retrieve()
                .bodyToMono(Gender.class);
        return gender;
    }

    Mono<Nationality> getNationalityForName(String name) {
        WebClient client = WebClient.create();
        Mono<Nationality> nationality = client.get()
                .uri("https://api.nationalize.io?name="+name)
                .retrieve()
                .bodyToMono(Nationality.class);
        return nationality;
    }

}
