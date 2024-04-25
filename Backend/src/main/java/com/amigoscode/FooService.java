package com.amigoscode;

import com.amigoscode.*;
import org.springframework.stereotype.Service;

@Service
public class FooService {

    private final SpringBootExampleApplication.foo foo;

    public FooService(SpringBootExampleApplication.foo foo) {
        this.foo = foo;
        System.out.println();
    }

    String getFooName(){
        return foo.name();
    }


}
