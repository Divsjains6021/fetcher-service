package com.propertydekho.fetcherservice;

import com.propertydekho.fetcherservice.models.PropIDs;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/")
public class PropertyFetchResource
{

    @RequestMapping("/fetch-propids")
    public PropIDs getPropetyIDs(){

        List<String> propIDs = Arrays.asList("Prop-ID1", "Prop-ID2");
        return PropIDs.builder().propIDs(propIDs).build();
    }
}
