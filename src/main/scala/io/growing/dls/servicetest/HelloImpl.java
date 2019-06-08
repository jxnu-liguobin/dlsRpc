package io.growing.dls.servicetest;

import com.google.inject.Singleton;

@Singleton
public class HelloImpl implements Hello {

    @Override
    public String sayHello(String name) {
        return name.concat("-hello!");
    }

}
